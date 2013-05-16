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
import us.aaronweiss.juicebot.ManagedBot;
import us.aaronweiss.juicebot.Message;

/**
 * A bot modelled after the demon, Sweet, from Buffy the Vampire Slayer's "Once More With Feeling."
 * <code>Sweet</code> is a demonstration of <code>ManagedBot</code>s as well as the POJO message API.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public class Sweet extends ManagedBot {
	/**
	 * Creates the demon, <code>Sweet</code>.
	 * 
	 * @param owner the owner of the bot
	 */
	public Sweet(String owner) {
		super("Sweet", owner);
	}

	@Override
	public void joinAll() {
		this.join("#vana");
	}
	
	@Override
	public boolean receivedAdmin(Message message) {
		boolean received = false;
		if (message.message().contains(username())) {
			if (Bot.containsIgnoreCase("quit", message.message()) || Bot.containsIgnoreCase("bye", message.message())) {
				this.quit(owner + " said so.");
				received = true;
			}
		}
		return received;
	}

	@Override
	public void receivedUser(Message message) {
		if (Bot.containsIgnoreCase("say you're happy now", message.message())) {
			message.reply("Once more, with feeling!");
		}
	}

	/**
	 * Runs this bot.
	 * 
	 * @param args the server to connect to
	 */
	public static void main(String[] args) {
		String server = "irc.fyrechat.net:6667";
		if (args.length > 0)
			server = args[0];
		Bot bot = new Sweet("aaronweiss74");
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
