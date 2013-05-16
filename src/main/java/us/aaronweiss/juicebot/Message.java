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
 * An immutable received message over IRC.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public class Message {
	private final String fullMessage;
	private final Client receiver;
	private final Channel session;
	private final String source;
	private final String messageType;
	private String channel;
	private String[] message;
	
	public Message(String[] message, Client receiver, Channel session) {
		this.fullMessage = Bot.join(" ", message);
		this.source = message[0].substring(1);
		this.messageType = message[1];
		for (String token : message) {
			if (token.startsWith("#")) {
				this.channel = token;
				break;
			}
		}
		for (int i = 1; i < message.length; i++) {
			if (message[i].startsWith(":")) {
				this.message = Bot.joinFromIndex(" ", i, message).substring(1).split(" ");
				break;
			}
		}
		this.receiver = receiver;
		this.session = session;
	}
	
	public String source() {
		return this.source;
	}
	
	public String type() {
		return this.messageType;
	}
	
	public String channel() {
		return this.channel;
	}
	
	public String message() {
		return (message != null) ? Bot.join(" ", message) : null;
	}
	
	public String[] splitMessage() {
		return this.message;
	}
	
	public Client receiver() {
		return this.receiver;
	}
	
	public Channel session() {
		return this.session;
	}
	
	public void reply(String message) {
		if (!messageType.equals("PRIVMSG"))
			throw new UnsupportedOperationException("Cannot reply to messages that are not PRIVMSGs.");
		this.receiver.send("PRIVMSG " + channel + " :" + message + "\r\n", session);
	}
	
	public void replyDirect(String message) {
		this.reply(source.subSequence(0, source.indexOf("!")) + ": " + message);
	}
	
	public String toString() {
		return this.fullMessage;
	}
}
