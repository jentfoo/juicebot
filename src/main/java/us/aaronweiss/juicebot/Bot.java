/*
 * Copyright (c) 2012 Aaron Weiss <aaronweiss74@gmail.com>
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
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import us.aaronweiss.juicebot.net.BotHandler;

/**
 * The abstract base for all bots, as it handles network bootstrapping.
 * @author Aaron Weiss
 * @version 1.3
 * @since 1.0
 */
public abstract class Bot implements IBot {
	protected final Bootstrap bootstrap;
	protected final Configuration config;
	protected Channel session;

	public Bot(String username, String server, String port) {
		this(username, server, port, false);
	}
	
	public Bot(String username, String server, String port, final boolean useSSL) {
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
				pipeline.addLast("botHandler", new BotHandler(Bot.this));
			}
		});
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		config = new Configuration(username, server, port);
		config.put("BOT_PERIODIC", "-1");
	}

	public void onMessage(String[] message) {
		if (this.isReadyMessage(message)) {
			this.onReady();
		}
	}

	public void onDisconnect() {
		this.session = null;
	}

	public void periodic() {
		BotUtils.output("BOT_PERIODIC is set without overriding default Bot.periodic()");
	}

	public void connect() {
		BotUtils.output("Connecting to " + config.get("IRC_SERVER") + ":" + config.get("IRC_PORT"));
		ChannelFuture cf = this.bootstrap.connect(new InetSocketAddress(config.get("IRC_SERVER"), Integer.parseInt(config.get("IRC_PORT"))));
		cf.syncUninterruptibly();
		this.session = cf.channel();
	}

	public void write(String message) {
		this.session.write(message);
	}

	public void disconnect() {
		ChannelFuture cf = this.session.disconnect();
		cf.syncUninterruptibly();
		this.session = null;
	}

	public boolean isConnected() {
		return this.session.isActive();
	}

	protected boolean isReadyMessage(String[] message) {
		return message[1].equals("MODE") &&
				message[0].equals(":" + this.getConfiguration().get("BOT_NAME")) &&
				message[2].equals(this.getConfiguration().get("BOT_NAME"));
	}

	public Configuration getConfiguration() {
		return config;
	}

}
