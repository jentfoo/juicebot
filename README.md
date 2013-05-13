# juicebot #
juicebot is a simple and fast library for creating IRC bots using [Netty](http://www.netty.io). It provides a very simple API for creating an IRC bot and an even simpler utility class for working with them. 

## An example. ##

	public class Example1 extends Bot {
		public Example1(String server, String port) {
			super("Example1", server, port);
		}
		
		@Override
		public void onReady() {
			BotUtils.join("#channel", this);
		}
		
		@Override
		public void onMessage(String[] message) {
			BotUtils.say("I'm really annoying.", message[2], this);
			super.onMessage(message);
		}
	}
	
## Another example. ##

	public class Example2 extends Bot {
		public Example2(String server, String port) {
			super("Example2", server, port);
		}
	
		@Override
		public void onReady() {
		BotUtils.join("#channel", this);
		}
	
		@Override
		public void onMessage(String[] message) {
			if (message[1].equals("PRIVMSG")) {
				BotUtils.say("I'm just slightly less annoying.", message[2], this);
			} else {
				super.onMessage(message);
			}
		}
	}

## Once more... with feeling! ##

	public class Example3 extends Bot {
		public Example3(String server, String port) {
			super("Example3", server, port);
		}
	
		@Override
		public void onReady() {
			BotUtils.join("#channel", this);
		}
		
		@Override
		public void onMessage(String[] message) {
			if (message[1].equals("PRIVMSG")) {
				String line = BotUtils.joinStringFrom(message, 3);
				if (line.matches("(?i)(.*)say you're happy now(.*)")) {
					BotUtils.say("once more, with feeling.", message[2], this);
				}
				} else {
					super.onMessage(message);
				}
			}
		}

### Acknowledgements ###
* [angelsl](http://www.github.com/angelsl) for helping with the IRC protocol stuff.
* [Peter Atashian](http://www.github.com/retep998) for helping with testing.
* [rice](http://www.github.com/wahlao) for the inspiration.
* [FyreChat](http://www.fyrechat.net/) for allowing me to build this library on it.
