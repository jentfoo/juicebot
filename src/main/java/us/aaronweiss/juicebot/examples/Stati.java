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
package us.aaronweiss.juicebot.examples;

import io.netty.channel.Channel;

import java.util.Scanner;

import us.aaronweiss.juicebot.Bot;
import us.aaronweiss.juicebot.DataAwareBot;
import us.aaronweiss.juicebot.Message;
import us.aaronweiss.juicebot.SessionData;

public class Stati extends DataAwareBot {

	public Stati() {
		super("Stati", false);
	}
	
	public Stati(boolean useSSL) {
		super("Stati", false, useSSL);
	}

	@Override
	public void joinAll() {
		this.join("#vana");
	}
	
	@Override
	public void receive(Message message) {
		SessionData sdo = message.session().attr(DataAwareBot.sessionData).get();
		if (message.type().equals("PRIVMSG") && message.message().contains(username())) {
			if (Bot.containsIgnoreCase("users", message.message())) {
				message.replyDirect("Channel Users: " + sdo.server.channels.get(message.channel()).userCount());
				message.replyDirect("Server Users: " + sdo.server.online());
			} else if (Bot.containsIgnoreCase("opers", message.message())) {
				message.replyDirect("Channel Opers: " + sdo.server.channels.get(message.channel()).operCount());
			} else if (Bot.containsIgnoreCase("topic", message.message())) {
				String topic = sdo.server.channels.get(message.channel()).topic();
				message.replyDirect("Topic: " + ((topic == null) ? "None set." : topic));
			} else if (Bot.containsIgnoreCase("join", message.message())) {
				for (String token : message.splitMessage()) {
					if (token.startsWith("#"))
						this.join(token);
				}
				message.replyDirect("Done.");
			}
		} else {
			super.receive(message);
		}
	}

	public static void main(String[] args) {
		// String server = args[0];
		String server = "irc.fyrechat.net:6667";
		Bot bot = new Stati();
		Channel session = bot.connect(server);
		Scanner input = new Scanner(System.in);
		while (true) {
			String cmd = input.nextLine();
			if (cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("shutdown")) {
				bot.disconnect(session);
				break;
			}
		}
		input.close();
	}
}
