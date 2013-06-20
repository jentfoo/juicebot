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
 * @version 1.0.1
 * @since 2.0.0
 */
public class Message {
	private final String fullMessage;
	private final Client receiver;
	private final Channel session;
	private final String source;
	private final String messageType;
	private String channel;
	private String[] message;

	/**
	 * Creates a new {@code Message}.
	 *
	 * @param message  the full message itself, split by spaces.
	 * @param receiver the {@code Client} receiving the message
	 * @param session  the session the message was received from
	 */
	public Message(String[] message, Client receiver, Channel session) {
		fullMessage = Bot.join(" ", message);
		source = message[0].substring(1);
		messageType = message[1];
		for (String token : message) {
			if (token.startsWith("#")) {
				channel = token;
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

	/**
	 * Gets the source of the message
	 *
	 * @return the message source
	 */
	public String source() {
		return source;
	}

	/**
	 * Gets the type of the message.
	 *
	 * @return the message type
	 */
	public String type() {
		return messageType;
	}

	/**
	 * Gets the IRC Channel of the message.
	 *
	 * @return the message's irc channel
	 */
	public String channel() {
		return channel;
	}

	/**
	 * Gets the complete last element (following the second colon) from the full
	 * message. To get the full message, use toString().
	 *
	 * @return the message portion of the message
	 */
	public String message() {
		return (message != null) ? Bot.join(" ", message) : null;
	}

	/**
	 * Gets the message portion split by spaces. To get the full message split
	 * by spaces, use toString().split(" ").
	 *
	 * @return the message portion of the message split by spaces
	 */
	public String[] splitMessage() {
		return message;
	}

	/**
	 * Gets the {@code Client} receiving the message.
	 *
	 * @return the message receiver
	 */
	public Client receiver() {
		return receiver;
	}

	/**
	 * Gets the session that the message is from.
	 *
	 * @return the message session
	 */
	public Channel session() {
		return session;
	}

	/**
	 * Replies to this message on the same channel and session.
	 *
	 * @param message the message to reply with
	 */
	public void reply(String message) {
		if (!messageType.equals("PRIVMSG"))
			throw new UnsupportedOperationException("Cannot reply to messages that are not PRIVMSGs.");
		receiver.send("PRIVMSG " + channel + " :" + message + "\r\n", session);
	}

	/**
	 * Replies directly to the user who sent this message on the same channel and
	 * session. Prepends the message with the username of the source.
	 *
	 * @param message the message to reply with
	 */
	public void replyDirect(String message) {
		reply(source.subSequence(0, source.indexOf("!")) + ": " + message);
	}

	@Override
	public String toString() {
		return fullMessage;
	}
}
