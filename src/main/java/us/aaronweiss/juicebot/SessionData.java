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
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public class SessionData {
	public final Server server;
	
	public SessionData() {
		this.server = new Server();
	}

	public void receive(Message message) {
		receive(message.toString());
	}

	public void receive(String message) {
		receive(message.split(" "));
	}
	
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
	 * 
	 * @author Aaron Weiss
	 * @version 1.0
	 * @since 2.0
	 */
	public class Server {
		public final Map<String, Channel> channels;
		
		public Server() {
			this.channels = new HashMap<String, Channel>();
		}
		
		public int online() {
			int ret = 0;
			for (Channel ch : channels.values()) 
				ret += ch.userCount();
			return ret;
		}
		
		public int opersOnline() {
			int ret = 0;
			for (Channel ch : channels.values()) 
				ret += ch.operCount();
			return ret;
		}
	}
	
	/**
	 * 
	 * @author Aaron Weiss
	 * @version 1.0
	 * @since 2.0
	 */
	public class Channel {
		public final String name;
		public final List<String> users;
		protected String topic = "feature currently broken. :(";
		
		public Channel(String name) {
			this.name = name;
			this.users = new ArrayList<String>();
		}
		
		public int userCount() {
			return users.size();
		}
		
		public int operCount() {
			int ret = 0;
			for (String user : users)
				if (user.startsWith("&") || user.startsWith("@"))
					ret++;
			return ret;
		}
		
		public int halfOpCount() {
			int ret = 0;
			for (String user : users)
				if (user.startsWith("%"))
					ret++;
			return ret;
		}
		
		public int voiceCount() {
			int ret = 0;
			for (String user : users)
				if (user.startsWith("+"))
					ret++;
			return ret;
		}
		
		public String topic() {
			return topic;
		}
	}
}
