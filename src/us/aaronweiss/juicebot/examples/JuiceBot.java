/*
 * Copyright (c) 2012 Aaron Weiss <aaronweiss74@gmail.com>
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

import java.util.Scanner;
import org.jboss.netty.channel.Channel;
import us.aaronweiss.juicebot.AutoBot;
import us.aaronweiss.juicebot.BotBootstrap;
import us.aaronweiss.juicebot.BotUtils;

/**
 * A basic IRC bot, who just wants some juice. 
 * @author Aaron Weiss
 * @version 1.0
 * @since 1.0
 */
public class JuiceBot extends AutoBot {
	public JuiceBot(String server, String port) {
		super("JuiceBot", server, port);
	}

	@Override
	public void onConnect(Channel session) {
		BotUtils.register(this);
		BotUtils.join("#mapledev", this);
		BotUtils.join("#vana", this);
		BotUtils.join("#mapldev", this);
	}
	
	@Override
	public void onMessage(String[] message) {
		boolean isAdminCommand = false;
		if (message[0].startsWith(":" + config.get("BOT_OWNER") + "!")) {
			if (message[1].equals("PRIVMSG")) {
				String line = BotUtils.joinStringFrom(message, 3);
				if (line.startsWith(":JuiceBot:") && (line.contains("quit") || line.contains("gtfo"))) {
					BotUtils.quit("Pass the juice!", this);
					this.disconnect();
					isAdminCommand = true;
				}
			}
		}
		if (!isAdminCommand) {
			if (message[1].equals("PRIVMSG")) {
				String line = BotUtils.joinStringFrom(message, 3);
				if (line.contains("gas the jews")) {
					BotUtils.say("No, I said \"pass the juice.\"", message[2], this);
				}
			} else {
				super.onMessage(message);
			}
		}
	}
	
	/**
	 * @param args the server and port to connect to
	 */
	public static void main(String[] args) {
		String serverInfo = args[0];
		String[] serverData = serverInfo.split(":");
		JuiceBot juice = new JuiceBot(serverData[0], serverData[1]);
		juice.getConfiguration().put("BOT_OWNER", "aaronweiss74");
		BotBootstrap bootstrap = new BotBootstrap(juice);
		bootstrap.run();
		Scanner input = new Scanner(System.in);
		while (true) {
			String cmd = input.nextLine();
			if (cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("shutdown")) {
				bootstrap.shutdown();
				break;
			}
		}
	}
}
