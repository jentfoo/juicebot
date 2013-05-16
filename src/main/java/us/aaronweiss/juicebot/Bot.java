/*
 * Copyright (C) 2013 Aaron Weiss <aaronweiss74@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom 
 * the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package us.aaronweiss.juicebot;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import us.aaronweiss.juicebot.internal.InternalUtilities;
import us.aaronweiss.juicebot.net.ClientHandlerAdapter;

/**
 * 
 * 
 * @author Aaron Weiss
 * @version 2.0
 * @since 1.0
 */
public abstract class Bot implements Client {
	protected static ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	protected final ChannelGroup sessions = new DefaultChannelGroup("sessions");
	protected TimeUnit timeUnit = TimeUnit.SECONDS;
	protected final Bootstrap bootstrap;
	protected int periodicTime = -1;
	private final boolean simple;
	private String username;
	
	public Bot(String username) {
		this(username, false);
	}
		
	public Bot(String username, boolean simple) {
		this(username, simple, false);
	}
	
	public Bot(String username, boolean simple, final boolean useSSL) {
		this.username = username;
		this.simple = simple;
		bootstrap = new Bootstrap();
		bootstrap.group(new OioEventLoopGroup());
		bootstrap.channel(OioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<OioSocketChannel>() {
			@Override
			protected void initChannel(OioSocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				
				// SSL Support
				if (useSSL) {
					SSLEngine engine = SSLContext.getDefault().createSSLEngine();
					engine.setUseClientMode(true);
					pipeline.addLast("ssl", new SslHandler(engine));
				}
				
				// Decoders
				pipeline.addLast("frameDecoder", new LineBasedFrameDecoder(1000));
				pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));

				// Encoder
				pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));

				// Handlers
				pipeline.addLast("botHandler", new ClientHandlerAdapter(Bot.this));
			}
		});
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
	}
	
	@Override
	public Channel connect(String address) {
		if (address.contains("[") || address.contains("]")) {
			if (address.lastIndexOf(":") > address.indexOf("]")) {
				return this.connect(address.substring(0, address.lastIndexOf(":")), address.substring(address.lastIndexOf(":")));
			} else {
				return this.connect(address, "6667");
			}
		} else if (address.contains(":")) {
			if (address.indexOf(":") == address.lastIndexOf(":")) {
				String[] adr = address.split(":");
				return this.connect(adr[0], adr[1]);
			} else {
				return this.connect(address, "6667");
			}
		} else {
			return this.connect(address, "6667");
		}
	}

	@Override
	public Channel connect(String address, String port) {
		return this.connect(new InetSocketAddress(address, Integer.parseInt(port)));
	}
	
	@Override
	public Channel connect(SocketAddress address) {
		final ChannelFuture cf = this.bootstrap.connect(address);
		sessions.add(cf.channel());
		if (periodicTime > 0) {
			// this could be simplified with lambda expressions... (cmd = this::periodic(cf.channel())
			Runnable cmd = new Runnable() {
				@Override
				public void run() {
					periodic(cf.channel());
				}
			};
			executor.scheduleAtFixedRate(cmd, periodicTime, periodicTime, timeUnit);
		}
		return cf.channel();
	}

	@Override
	public void disconnect(Channel session) {
		session.close();
	}
	
	@Override
	public void connected(SocketAddress address) {
		this.setUsername(username);
	}
	
	@Override
	public void periodic(Channel session) {
		InternalUtilities.println("Default periodic() method is being run (every " + periodicTime + " " + timeUnit.toString() + ").");
	}

	@Override
	public void disconnected(Channel session) {
		// Do nothing by default.
	}

	@Override
	public boolean isConnected() {
		return !this.sessions.isEmpty();
	}

	@Override
	public boolean isSimpleMessageReceiver() {
		return this.simple;
	}

	@Override
	public void send(String message) {
		if (!message.endsWith("\r\n"))
			message += "\r\n";
		this.sessions.write(message);
	}
	
	@Override
	public void send(String message, Channel session) {
		if (!this.sessions.contains(session))
			throw new IllegalArgumentException("Not connected to specified session.");
		if (!message.endsWith("\r\n"))
			message += "\r\n";
		session.write(message);
	}
	
	@Override
	public void send(String[] message) {
		this.send(Bot.join(" ", message));
	}
	
	@Override
	public void send(String[] message, Channel session) {
		this.send(Bot.join(" ", message), session);
	}

	@Override
	public void receive(String message, Channel session) {
		this.receive(message.split(" "), session);
	}

	@Override
	public void receive(String[] message, Channel session) {
		this.receive(new Message(message, this, session));
	}

	@Override
	public void receive(Message message) {
		this.receive(message.toString(), message.session());
	}
	
	protected String username() {
		return this.username;
	}
	
	protected void setUsername(String username) {
		this.username = username;
		this.send("NICK :" + username + "\r\n");
		this.send("USER " + username + " 0 * :" + username + "\r\n");
	}
	
	// Common IRC Operations
	public void join(String channel) {
		this.send("JOIN " + channel + "\r\n");
	}
	
	public void join(String channel, Channel session) {
		this.send("JOIN " + channel + "\r\n", session);
	}
	
	public void part(String channel) {
		this.send("PART " + channel + "\r\n");
	}	
	
	public void part(String channel, Channel session) {
		this.send("PART " + channel + "\r\n", session);
	}
	
	public void part(String channel, String reason) {
		this.send("PART " + channel + " :" + reason + "\r\n");
	}
	
	public void part(String channel, String reason, Channel session) {
		this.send("PART " + channel + " :" + reason + "\r\n", session);
	}
	
	public void quit() {
		this.send("QUIT\r\n");
	}
	
	public void quit(Channel session) {
		this.send("QUIT\r\n", session);
	}
	
	public void quit(String reason) {
		this.send("QUIT :" + reason + "\r\n");
	}

	public void quit(String reason, Channel session) {
		this.send("QUIT :" + reason + "\r\n", session);
	}
	
	public void say(String message, String channel) {
		this.send("PRIVMSG " + channel + " :" + message + "\r\n");
	}
	
	public void say(String message, String channel, Channel session) {
		this.send("PRIVMSG " + channel + " :" + message + "\r\n", session);
	}
	
	public void sayMe(String message, String channel) {
		this.send("PRIVMSG " + channel + " :\u0001ACTION " + message + "\r\n");
	}
	
	public void sayMe(String message, String channel, Channel session) {
		this.send("PRIVMSG " + channel + " :\u0001ACTION " + message + "\r\n", session);
	}
	
	// Static Bot utilities
	
	/**
	 * Finds a string within another string ignoring case.
	 * @param needle the string to find
	 * @param haystack the string to search in
	 * @return whether or not the string was found
	 */
	public static boolean containsIgnoreCase(String needle, String haystack) {
		if (needle == null || haystack == null)
			return false;
		return haystack.matches("(?i)(.*)" + needle + "(.*)");
	}

	/**
	 * Joins a string from an array.
	 * @param joinString the string to use for joining
	 * @param message the array to use
	 * @return the joined string
	 */
	public static String join(String joinString, String[] message) {
		return joinFromIndex(joinString, 0, message);
	}
	
	/**
	 * Joins a string in an array starting at the specified index.
	 * @param joinString the string to use for joining
	 * @param index the index to start at
	 * @param message the array to use
	 * @return the string joined from the specified index
	 */
	public static String joinFromIndex(String joinString, int index, String[] message) {
		try {
			StringBuilder sb = new StringBuilder();
			for (int i = index; i < message.length; i++) {
				sb.append(message[i]).append(joinString);
			}
			return sb.substring(0, sb.lastIndexOf(joinString));
		} catch (NullPointerException e) {
			return null;
		}
	}
}
