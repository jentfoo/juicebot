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

public abstract class ManagedBot extends AutoBot {
	protected String owner;
	
	public ManagedBot(String username, String owner) {
		super(username);
		this.owner = owner;
	}
	
	public ManagedBot(String username, String owner, boolean useSSL) {
		super(username, useSSL);
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
	
	public abstract boolean receivedAdmin(Message message);
	public abstract void receivedUser(Message message);
}
