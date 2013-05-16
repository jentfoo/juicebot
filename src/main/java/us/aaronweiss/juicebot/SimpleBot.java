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
 * A <code>Bot</code> using the Simple String Array Messaging API.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public abstract class SimpleBot extends Bot {
	/**
	 * Creates a <code>SimpleBot</code>.
	 * 
	 * @param username
	 *            the bot's username
	 */
	public SimpleBot(String username) {
		super(username, true);
	}

	/**
	 * Creates a <code>SimpleBot</code> over SSL.
	 * 
	 * @param username
	 *            the bot's username
	 * @param useSSL
	 *            whether or not to use SSL
	 */
	public SimpleBot(String username, boolean useSSL) {
		super(username, true, useSSL);
	}

	@Override
	public abstract void receive(String[] message, Channel session);

	@Override
	public void receive(Message message) {
		throw new IllegalArgumentException("SimpleBots don't support Message objects.");
	}

}
