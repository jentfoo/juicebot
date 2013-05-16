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
 * A basic bot that auto-rejoins upon being kicked, and attempts to avert poor bans.
 * n.b. You must call super.receive(...) when you extend it in order to receive desired functionality.
 * 
 * @author Aaron Weiss
 * @version 2.0
 * @since 1.0
 */
public abstract class AutoBot extends Bot {
	/**
	 * Creates a message-based <code>AutoBot</code> with the desired name.
	 * 
	 * @param username the desired username
	 */
	public AutoBot(String username) {
		super(username);
	}
	
	/**
	 * Creates a simple or message <code>AutoBot</code> with the desired name.
	 * 
	 * @param username the desired username
	 * @param simple whether or not to use the simple messaging API
	 */
	public AutoBot(String username, boolean simple) {
		super(username, simple);
	}

	/**
	 * Creates a simple or message <code>AutoBot</code> with the desired name over SSL.
	 * 
	 * @param username the desired username
	 * @param simple whether or not to use the simple messaging API
	 * @param useSSL whether or not to use SSL
	 */
	public AutoBot(String username, boolean simple,  boolean useSSL) {
		super(username, simple, useSSL);
	}
	
	/**
	 * Instructs the bot to join all of its home channels.
	 */
	public abstract void joinAll();
	
	@Override
	public void receive(Message message) {
		if (message.type().equals("KICK") && message.message().contains(username())) {
			this.send("JOIN " + message.channel() + "\r\n");
		} else if (message.type().equals(ServerResponseCode.ERR_BANNEDFROMCHAN.value)) {
			this.setUsername(username() + "_");
		} else if (message.type().equals(ServerResponseCode.ERR_CANNOTSENDTOCHAN.value)) {
			this.part(message.channel());
			this.setUsername(username() + "_");
			this.join(message.channel());
		} else if (message.toString().startsWith(":" + username() + " MODE " + username())) {
			this.joinAll();
		}
	}
}
