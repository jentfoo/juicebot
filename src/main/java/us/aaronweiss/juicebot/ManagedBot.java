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

/**
 * An <code>AutoBot</code> with a built-in owner.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public abstract class ManagedBot extends AutoBot {
	protected String owner;

	/**
	 * Creates a message-based <code>ManagedBot</code> with the desired name and
	 * owner.
	 * 
	 * @param username
	 *            the desired username
	 * @param owner
	 *            the bot's owner
	 */
	public ManagedBot(String username, String owner) {
		super(username);
		this.owner = owner;
	}

	/**
	 * Creates a message-based <code>ManagedBot</code> with the desired name and
	 * owner over SSL.
	 * 
	 * @param username
	 *            the desired username
	 * @param owner
	 *            the bot's owner
	 * @param useSSL
	 *            whether or not to use SSL.
	 */
	public ManagedBot(String username, String owner, boolean useSSL) {
		super(username, false, useSSL);
		this.owner = owner;
	}

	@Override
	public void receive(Message message) {
		boolean skipUser = false;
		if (message.source().startsWith(owner))
			skipUser = this.receivedAdmin(message);
		if (!skipUser)
			this.receivedUser(message);
		super.receive(message);
	}

	/**
	 * Receives a message from the bot owner.
	 * 
	 * @param message
	 *            the message received
	 * @return whether or not any admin commands were executed
	 */
	public abstract boolean receivedAdmin(Message message);

	/**
	 * Receives a normal user-message.
	 * 
	 * @param message
	 *            the message received
	 */
	public abstract void receivedUser(Message message);
}
