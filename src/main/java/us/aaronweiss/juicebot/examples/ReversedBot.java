package us.aaronweiss.juicebot.examples;

import io.netty.channel.Channel;
import us.aaronweiss.juicebot.Bot;
import us.aaronweiss.juicebot.ManagedBot;
import us.aaronweiss.juicebot.Message;

import java.util.Scanner;

/**
 * A bot that says whatever the owner says in reverse. This
 * makes liberal use of the {@code ManagedBot} base as a
 * simple, and easy means of filtering messages from a user.
 *
 * @author Aaron Weiss
 * @version 1.0.0
 * @since 2.0.1
 */
public class ReversedBot extends ManagedBot {
	/**
	 * Creates a {@code ReversedBot} for a particular {@code username}.
	 *
	 * @param username the username to reverse messages from.
	 */
	public ReversedBot(String username) {
		super("reversed_" + username, username);
	}

	@Override
	public boolean receivedAdmin(Message message) {
		if (message.type().equals("PRIVMSG")) {
			message.reply(reverse(message.message()));
			return true;
		}
		return false;
	}

	@Override
	public void receivedUser(Message message) {
		return;
	}

	@Override
	public void joinAll() {
		join("#vana");
	}

	/**
	 * Reverses the {@code message} completely.
	 *
	 * @param message the string to reverse
	 * @return the reversed string
	 */
	protected String reverse(String message) {
		return new StringBuilder(message).reverse().toString();
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
		Bot bot = new ReversedBot("calc0000");
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
