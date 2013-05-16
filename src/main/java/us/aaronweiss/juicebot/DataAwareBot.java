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

import java.net.SocketAddress;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * A bot with embedded access to the information API.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public abstract class DataAwareBot extends Bot {
	protected static final AttributeKey<SessionData> sessionData = new AttributeKey<SessionData>("session-data");

	/**
	 * Creates a message-based <code>DataAwareBot</code> with the desired name.
	 * 
	 * @param username the desired username
	 */
	public DataAwareBot(String username) {
		super(username);
	}

	/**
	 * Creates a simple or message <code>DataAwareBot</code> with the desired name.
	 * 
	 * @param username the desired username
	 * @param simple whether or not to use the simple messaging API
	 */
	public DataAwareBot(String username, boolean simple) {
		super(username, simple);
	}

	/**
	 * Creates a simple or message <code>DataAwareBot</code> with the desired name over SSL.
	 * 
	 * @param username the desired username
	 * @param simple whether or not to use the simple messaging API
	 * @param useSSL whether or not to use SSL
	 */
	public DataAwareBot(String username, boolean simple,  boolean useSSL) {
		super(username, simple, useSSL);
	}
	
	@Override
	public Channel connect(SocketAddress address) {
		Channel ch = super.connect(address);
		ch.attr(sessionData).set(new SessionData());
		return ch;
	}

	/**
	 * Instructs the bot to join all of its home channels.
	 */
	public abstract void joinAll();

	@Override
	public void receive(Message message) {
		if (message.toString().startsWith(":" + username() + " MODE " + username())) {
			this.joinAll();
		}
		if (message != null)
			message.session().attr(DataAwareBot.sessionData).get().receive(message);
	}
}
