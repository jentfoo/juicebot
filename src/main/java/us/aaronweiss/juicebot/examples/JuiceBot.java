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
import us.aaronweiss.juicebot.SimpleBot;

/**
 * A bot that responds to phrases containing "gas" and "jews" with a fun
 * message. <code>JuiceBot</code> serves as an example of the simple messaging
 * API.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public class JuiceBot extends SimpleBot {
	/**
	 * Creates a <code>JuiceBot</code>.
	 */
	public JuiceBot() {
		super("JuiceBot");
	}

	/**
	 * Creates a <code>JuiceBot</code> over SSL.
	 * 
	 * @param useSSL
	 *            whether or not to use SSL
	 */
	public JuiceBot(boolean useSSL) {
		super("JuiceBot", useSSL);
	}

	/**
	 * Instructs the bot to join all of its home channels.
	 */
	public void joinAll() {
		this.join("#vana");
	}

	@Override
	public void receive(String[] message, Channel session) {
		if (message[0].endsWith(username()) && message[1].equals("MODE") && message[2].equals(username()))
			this.joinAll();
		boolean gas = false, jews = false;
		for (String token : message) {
			if (token.equalsIgnoreCase("gas") || token.equalsIgnoreCase(":gas"))
				gas = true;
			else if (token.equalsIgnoreCase("jews") || token.equalsIgnoreCase("joos"))
				jews = true;
		}
		if (gas || jews)
			this.say("No, I said pass the juice!", message[2], session);
	}

	/**
	 * Runs this bot.
	 * 
	 * @param args
	 *            the server to connect to
	 */
	public static void main(String[] args) {
		String server = "irc.fyrechat.net:6667";
		if (args.length > 0)
			server = args[0];
		Bot bot = new JuiceBot();
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
