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
package us.aaronweiss.juicebot.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import us.aaronweiss.juicebot.Client;
import us.aaronweiss.juicebot.Message;
import us.aaronweiss.juicebot.internal.InternalUtilities;
import us.aaronweiss.juicebot.internal.JuiceBotDefaults;

/**
 * An inbound-only message handler to provide client functionality.
 * 
 * @author Aaron Weiss
 * @version 2.0
 * @since 1.0
 */
public class ClientHandlerAdapter extends ChannelInboundMessageHandlerAdapter<String> {
	protected final boolean verbose, autoPing;
	private boolean connected = false;
	protected final Client client;

	/**
	 * Creates a <code>ClientHandlerAdapter</code> with auto-ping.
	 * 
	 * @param client
	 *            the client to handle
	 */
	public ClientHandlerAdapter(Client client) {
		this(client, JuiceBotDefaults.VERBOSE_BY_DEFAULT, true);
	}

	/**
	 * Creates a <code>ClientHandlerAdapter</code> with auto-ping.
	 * 
	 * @param client
	 *            the client to handle
	 * @param verbose
	 *            whether or not to be verbose
	 */
	public ClientHandlerAdapter(Client client, boolean verbose) {
		this(client, verbose, true);
	}

	/**
	 * Creates a <code>ClientHandlerAdapter</code>.
	 * 
	 * @param client
	 *            the client to handle
	 * @param verbose
	 *            whether or not to be verbose
	 * @param autoPing
	 *            whether or not to automatically handle pings
	 */
	public ClientHandlerAdapter(Client client, boolean verbose, boolean autoPing) {
		this.client = client;
		this.verbose = verbose;
		this.autoPing = autoPing;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, String message) throws Exception {
		if (!connected) {
			connected = true;
			this.client.connected(ctx.channel());
		}
		for (String command : message.split("\r\n")) {
			if (verbose)
				InternalUtilities.println(command);
			if (autoPing && command.contains("PING") && command.indexOf("PING") < command.indexOf(':', 1) && !command.contains("supported")) {
				String pong = command.replaceFirst("PING", "PONG").substring(command.indexOf("PING"));
				if (verbose)
					InternalUtilities.println(pong);
				this.client.send(pong);
			} else if (this.client.isSimpleMessageReceiver()) {
				this.client.receive(command, ctx.channel());
			} else {
				this.client.receive(new Message(command.split(" "), this.client, ctx.channel()));
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.connected = false;
		this.client.disconnected(ctx.channel());
	}
}
