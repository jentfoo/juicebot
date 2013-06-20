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
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import us.aaronweiss.juicebot.internal.InternalUtilities;
import us.aaronweiss.juicebot.net.ClientHandlerAdapter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The core of all juicebot bots, handling all necessary internals.
 *
 * @author Aaron Weiss
 * @version 2.0.1
 * @since 1.0.0
 */
public abstract class Bot implements Client {
	protected static ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	protected final ChannelGroup sessions = new DefaultChannelGroup("sessions", GlobalEventExecutor.INSTANCE);
	protected TimeUnit timeUnit = TimeUnit.SECONDS;
	protected final Bootstrap bootstrap;
	protected int periodicTime = -1;
	private final boolean simple;
	private String username;

	/**
	 * Constructs a new {@code Bot}.
	 *
	 * @param username the username of the bot
	 */
	public Bot(String username) {
		this(username, false);
	}

	/**
	 * Constructs a new {@code Bot}.
	 *
	 * @param username the username of the bot
	 * @param simple   whether or not the bot should use the simple messaging API
	 */
	public Bot(String username, boolean simple) {
		this(username, simple, false);
	}

	/**
	 * Constructs a new {@code Bot}.
	 *
	 * @param username the username of the bot
	 * @param simple   whether or not the bot should use the simple messaging API
	 * @param useSSL   whether or not the bot should use SSL
	 */
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
				return connect(address.substring(0, address.lastIndexOf(":")), address.substring(address.lastIndexOf(":")));
			} else {
				return connect(address, "6667");
			}
		} else if (address.contains(":")) {
			if (address.indexOf(":") == address.lastIndexOf(":")) {
				String[] adr = address.split(":");
				return connect(adr[0], adr[1]);
			} else {
				return connect(address, "6667");
			}
		} else {
			return connect(address, "6667");
		}
	}

	@Override
	public Channel connect(String address, String port) {
		return connect(new InetSocketAddress(address, Integer.parseInt(port)));
	}

	@Override
	public Channel connect(SocketAddress address) {
		final ChannelFuture cf = bootstrap.connect(address);
		sessions.add(cf.channel());
		if (periodicTime > 0) {
			// this could be simplified with lambda expressions... cmd = this::periodic(cf.channel())
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
	public void connected(Channel session) {
		setUsername(username);
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
		return sessions.isEmpty();
	}

	@Override
	public boolean isSimpleMessageReceiver() {
		return simple;
	}

	@Override
	public void send(String message) {
		if (!message.endsWith("\r\n"))
			message += "\r\n";
		sessions.write(message);
	}

	@Override
	public void send(String message, Channel session) {
		if (!sessions.contains(session))
			throw new IllegalArgumentException("Not connected to specified session.");
		if (!message.endsWith("\r\n"))
			message += "\r\n";
		session.write(message);
	}

	@Override
	public void send(String[] message) {
		send(join(" ", message));
	}

	@Override
	public void send(String[] message, Channel session) {
		send(join(" ", message), session);
	}

	@Override
	public void receive(String message, Channel session) {
		receive(message.split(" "), session);
	}

	@Override
	public void receive(String[] message, Channel session) {
		receive(new Message(message, this, session));
	}

	@Override
	public void receive(Message message) {
		receive(message.toString(), message.session());
	}

	/**
	 * Gets the username of the bot.
	 *
	 * @return the bot's username
	 */
	protected String username() {
		return username;
	}

	/**
	 * Sets the bot's username and sends re-registration commands.
	 *
	 * @param username the desired new username
	 */
	protected void setUsername(String username) {
		this.username = username;
		send("NICK :" + username + "\r\n");
		send("USER " + username + " 0 * :" + username + "\r\n");
	}

	// Common IRC Operations

	/**
	 * Instructs the bot to join the specified channel.
	 *
	 * @param channel the channel to join
	 */
	public void join(String channel) {
		send("JOIN " + channel + "\r\n");
	}

	/**
	 * Instructs the bot to join the specified channel.
	 *
	 * @param channel the channel to join
	 * @param session the session to join on
	 */
	public void join(String channel, Channel session) {
		send("JOIN " + channel + "\r\n", session);
	}

	/**
	 * Instructs the bot to part the specified channel.
	 *
	 * @param channel the channel to part
	 */
	public void part(String channel) {
		send("PART " + channel + "\r\n");
	}

	/**
	 * Instructs the bot to part the specified channel.
	 *
	 * @param channel the channel to part
	 * @param session the session to part on
	 */
	public void part(String channel, Channel session) {
		send("PART " + channel + "\r\n", session);
	}

	/**
	 * Instructs the bot to part the specified channel with a reason.
	 *
	 * @param channel the channel to part
	 * @param reason  the reason for parting
	 */
	public void part(String channel, String reason) {
		send("PART " + channel + " :" + reason + "\r\n");
	}

	/**
	 * Instructs the bot to part the specified channel with a reason.
	 *
	 * @param channel the channel to part
	 * @param reason  the reason for parting
	 * @param session the session to part on
	 */
	public void part(String channel, String reason, Channel session) {
		send("PART " + channel + " :" + reason + "\r\n", session);
	}

	/**
	 * Instructs the bot to quit.
	 */
	public void quit() {
		send("QUIT\r\n");
	}

	/**
	 * Instructs the bot to quit.
	 *
	 * @param session the session to quit from
	 */
	public void quit(Channel session) {
		send("QUIT\r\n", session);
	}

	/**
	 * Instructs the bot to quit with a reason.
	 *
	 * @param reason the reason for quitting
	 */
	public void quit(String reason) {
		send("QUIT :" + reason + "\r\n");
	}

	/**
	 * Instructs the bot to quit with a reason.
	 *
	 * @param reason  the reason for quitting
	 * @param session the session to quit from
	 */
	public void quit(String reason, Channel session) {
		send("QUIT :" + reason + "\r\n", session);
	}

	/**
	 * Instructs the bot to send a message to the specified channel.
	 *
	 * @param message the message to send
	 * @param channel the channel to send to
	 */
	public void say(String message, String channel) {
		send("PRIVMSG " + channel + " :" + message + "\r\n");
	}

	/**
	 * Instructs the bot to send a message to the specified channel.
	 *
	 * @param message the message to send
	 * @param channel the channel to send to
	 * @param session the session to send on
	 */
	public void say(String message, String channel, Channel session) {
		send("PRIVMSG " + channel + " :" + message + "\r\n", session);
	}

	/**
	 * Instructs the bot to send a message in /me form to the specified channel.
	 *
	 * @param message the message to send
	 * @param channel the channel to send to
	 */
	public void sayMe(String message, String channel) {
		send("PRIVMSG " + channel + " :\u0001ACTION " + message + "\r\n");
	}

	/**
	 * Instructs the bot to send a message in /me form to the specified channel.
	 *
	 * @param message the message to send
	 * @param channel the channel to send to
	 * @param session the channel to send on
	 */
	public void sayMe(String message, String channel, Channel session) {
		send("PRIVMSG " + channel + " :\u0001ACTION " + message + "\r\n", session);
	}

	// Static Bot utilities

	/**
	 * Finds a string within another string ignoring case.
	 *
	 * @param needle   the string to find
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
	 *
	 * @param joinString the string to use for joining
	 * @param message    the array to use
	 * @return the joined string
	 */
	public static String join(String joinString, String[] message) {
		return joinFromIndex(joinString, 0, message);
	}

	/**
	 * Joins a string in an array starting at the specified index.
	 *
	 * @param joinString the string to use for joining
	 * @param index      the index to start at
	 * @param message    the array to use
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
