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

import io.netty.channel.Channel;

/**
 * A <code>Bot</code> using the POJO Messaging API.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public abstract class MessageBot extends Bot {
	/**
	 * Creates a <code>MessageBot</code>.
	 * 
	 * @param username
	 *            the bot's username
	 */
	public MessageBot(String username) {
		super(username, false);
	}

	/**
	 * Creates a <code>MessageBot</code> over SSL.
	 * 
	 * @param username
	 *            the bot's username
	 * @param useSSL
	 *            whether or not to use SSL
	 */
	public MessageBot(String username, boolean useSSL) {
		super(username, false, useSSL);
	}

	@Override
	public void receive(String[] message, Channel session) {
		throw new IllegalArgumentException("MessageBots don't support the simple messaging API.");
	}

	@Override
	public abstract void receive(Message message);
}
