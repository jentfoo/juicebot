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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The core data element for the Information API.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public class SessionData {
	public final Server server;

	/**
	 * Creates a new session data object.
	 */
	public SessionData() {
		this.server = new Server();
	}

	/**
	 * Receives a new message for parsing.
	 * 
	 * @param message
	 *            the message to parse
	 */
	public void receive(Message message) {
		receive(message.toString());
	}

	/**
	 * Receives a new message for parsing.
	 * 
	 * @param message
	 *            the message to parse
	 */
	public void receive(String message) {
		receive(message.split(" "));
	}

	/**
	 * Receives a new message for parsing.
	 * 
	 * @param message
	 *            the message to parse
	 */
	public void receive(String[] message) {
		if (message.length >= 5) {
			Channel ch;
			if (this.server.channels.get(message[4]) != null)
				ch = this.server.channels.get(message[4]);
			else
				ch = new Channel(message[4]);
			if (message[1].equals(ServerResponseCode.RPL_NAMREPLY.toString())) {
				ch.users.add(message[2]);
				for (int i = 6; i < message.length; i++) {
					ch.users.add(message[i]);
				}
				this.server.channels.put(message[4], ch);
			} else if (message[1].equals(ServerResponseCode.RPL_TOPIC.toString())) {
				System.err.print(Bot.joinFromIndex(" ", 4, message).substring(1));
				ch.topic = Bot.joinFromIndex(" ", 4, message).substring(1);
			} else if (message[1].equals(ServerResponseCode.RPL_NOTOPIC.toString())) {
				ch.topic = null;
			} else if (message[1].equals("PART")) {
				ch.users.remove(message[0].substring(1, message[0].indexOf("!")));
			} else if (message[1].equals("QUIT")) {
				for (Channel chx : this.server.channels.values()) {
					chx.users.remove(message[0].substring(1, message[0].indexOf("!")));
				}
			}
			this.server.channels.put(ch.name, ch);
		}
	}

	/**
	 * An object representation of the IRC Server.
	 * 
	 * @author Aaron Weiss
	 * @version 1.0
	 * @since 2.0
	 */
	public class Server {
		public final Map<String, Channel> channels;

		/**
		 * Creates a new <code>Server</code>.
		 */
		public Server() {
			this.channels = new HashMap<String, Channel>();
		}

		/**
		 * Gets the total number of users online as determined from received
		 * messages.
		 * 
		 * @return the total number of users online
		 */
		public int online() {
			int ret = 0;
			for (Channel ch : channels.values())
				ret += ch.userCount();
			return ret;
		}

		/**
		 * Gets the total number of opers online as determined from received
		 * messages.
		 * 
		 * @return the total number of opers online
		 */
		public int opersOnline() {
			int ret = 0;
			for (Channel ch : channels.values())
				ret += ch.operCount();
			return ret;
		}
	}

	/**
	 * An object representation of an IRC Channel.
	 * 
	 * @author Aaron Weiss
	 * @version 1.0
	 * @since 2.0
	 */
	public class Channel {
		public final String name;
		public final List<String> users;
		protected String topic = "feature currently broken. :(";

		/**
		 * Creates a new <code>Channel</code>.
		 * 
		 * @param name
		 *            the name of the channel
		 */
		public Channel(String name) {
			this.name = name;
			this.users = new ArrayList<String>();
		}

		/**
		 * Gets the number of users in the channel.
		 * 
		 * @return the number users in channel
		 */
		public int userCount() {
			return users.size();
		}

		/**
		 * Gets the number of opers in the channel.
		 * 
		 * @return the number opers in channel
		 */
		public int operCount() {
			int ret = 0;
			for (String user : users)
				if (user.startsWith("&") || user.startsWith("@"))
					ret++;
			return ret;
		}

		/**
		 * Gets the number of half ops in the channel.
		 * 
		 * @return the number half ops in channel
		 */
		public int halfOpCount() {
			int ret = 0;
			for (String user : users)
				if (user.startsWith("%"))
					ret++;
			return ret;
		}

		/**
		 * Gets the number of voiced users in the channel.
		 * 
		 * @return the number voiced users in channel
		 */
		public int voiceCount() {
			int ret = 0;
			for (String user : users)
				if (user.startsWith("+"))
					ret++;
			return ret;
		}

		/**
		 * Gets the topic of the channel. n.b. as of juicebot 2.0.0, this
		 * feature is not working
		 * 
		 * @return the topic, or null if none set
		 */
		public String topic() {
			return topic;
		}
	}
}
