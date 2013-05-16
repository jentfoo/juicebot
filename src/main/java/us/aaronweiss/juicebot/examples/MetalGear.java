package us.aaronweiss.juicebot.examples;

import us.aaronweiss.juicebot.AutoBot;
import us.aaronweiss.juicebot.Bot;
import us.aaronweiss.juicebot.Message;
import us.aaronweiss.juicebot.internal.JuiceBotDefaults;

/**
 * A bot that says "METAL GEAR" upon seeing its name, Snake.
 * <code>MetalGear</code> serves as a demonstration of
 * <code>JuiceBotDefaults</code> and <code>AutoBot</code>s.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public class MetalGear extends AutoBot {
	/**
	 * Creates a <code>MetalGear</code> bot named SolidSnake.
	 */
	public MetalGear() {
		super("SolidSnake");
	}

	@Override
	public void joinAll() {
		this.join("#vana");
	}

	@Override
	public void receive(Message message) {
		if (message.type().equals("PRIVMSG")) {
			if (Bot.containsIgnoreCase("snake", message.message()))
				message.replyDirect(this.newMetalGear());
		} else {
			super.receive(message);
		}
	}

	/**
	 * Creates a new slightly randomized "METAL GEAR" string.
	 * 
	 * @return the new "METAL GEAR" string
	 */
	public String newMetalGear() {
		StringBuilder sb = new StringBuilder("METAL GEA");
		for (int i = 0; i < (int) (Math.random() * 3); i++)
			sb.append("A");
		for (int i = 0; i < (int) (Math.random() * 17 + 5); i++)
			sb.append("R");
		return sb.toString();
	}

	/**
	 * Runs this bot.
	 * 
	 * @param args
	 *            the server to connect to
	 */
	public static void main(String[] args) {
		JuiceBotDefaults.VERBOSE_BY_DEFAULT = false;
		String server = "irc.fyrechat.net:6667";
		if (args.length > 0)
			server = args[0];
		Bot bot = new MetalGear();
		bot.connect(server);
	}
}
