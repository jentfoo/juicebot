package us.aaronweiss.juicebot.examples;

import java.util.Scanner;

import us.aaronweiss.juicebot.AutoBot;
import us.aaronweiss.juicebot.BotBootstrap;
import us.aaronweiss.juicebot.BotUtils;

/**
 * A basic IRC bot to demonstrate SSL support.
 * n.b. You'll need to work some magic for self-signed SSL certificates.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 1.1
 */
public class SSLDemoBot extends AutoBot {
	/**
	 * Constructs an SSLDemoBot.
	 * @param server the server to connect to
	 * @param port the port to connect on
	 */
	public SSLDemoBot(String server, String port) {
		super("SSLDemoBot", server, port, true);
	}

	@Override
	public void onConnect() {
		BotUtils.register(this);
	}
	
	@Override
	public void onReady() {
		BotUtils.join("#vana", this);
		BotUtils.say("So, this is what SSL tastes like.", "#vana", this);
	}
	
	@Override
	public void onMessage(String[] message) {
		boolean isAdminCommand = false;
		if (message[0].startsWith(":" + config.get("BOT_OWNER") + "!")) {
			if (message[1].equals("PRIVMSG")) {
				String line = BotUtils.joinStringFrom(message, 3);
				if (line.startsWith(":SSLDemoBot:") && (line.contains("quit") || line.contains("gtfo"))) {
					BotUtils.quit("Mission accomplished.", this);
					this.disconnect();
					isAdminCommand = true;
				}
			}
		}
		if (!isAdminCommand) {
			super.onMessage(message);
		}
	}
	
	/**
	 * @param args the server and port to connect to
	 */
	public static void main(String[] args) {
		// String serverInfo = args[0];
		String serverInfo = "irc.fyrechat.net:6697";
		String[] serverData = serverInfo.split(":");
		SSLDemoBot demo = new SSLDemoBot(serverData[0], serverData[1]);
		demo.getConfiguration().put("BOT_OWNER", "aaronweiss74");
		BotBootstrap bootstrap = new BotBootstrap(demo);
		bootstrap.run();
		Scanner input = new Scanner(System.in);
		while (true) {
			String cmd = input.nextLine();
			if (cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("shutdown")) {
				bootstrap.shutdown();
				break;
			}
		}
		input.close();
	}
}
