package us.aaronweiss.juicebot.examples;

import us.aaronweiss.juicebot.AutoBot;
import us.aaronweiss.juicebot.Bot;
import us.aaronweiss.juicebot.Message;
import us.aaronweiss.juicebot.internal.JuiceBotDefaults;

public class MetalGear extends AutoBot {
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
	
	public String newMetalGear() {
		StringBuilder sb = new StringBuilder("METAL GEA");
		for (int i = 0; i < (int) (Math.random() * 3); i++)
			sb.append("A");
		for (int i = 0; i < (int) (Math.random() * 17 + 5); i++)
			sb.append("R");
		return sb.toString();
	}

	public static void main(String[] args) {
		JuiceBotDefaults.VERBOSE_BY_DEFAULT = false;
		String server = "irc.fyrechat.net:6667";
		Bot bot = new MetalGear();
		bot.connect(server);
	}
}
