# juicebot #
juicebot is a simple and fast library for creating IRC bots using [Netty](http://www.netty.io). It provides a very simple, easy-to-use API for creating an IRC bot. juicebot 2 now sports a redesigned API offering both the traditional simple API with string arrays, and an elegant new POJO-based API. You can find javadocs for juicebot [here](http://aaronweiss74.github.io/juicebot/doc/).

### Tell me about your use cases! ###
That's right, folks! If you're using (or would like to use) juicebot, I want to hear about it. I want to know what you like, what you don't like, what's working, what isn't, and where you think this project needs to go. You can find a bunch of ways to contact me on my website [here](http://www.aaronweiss.us/) or you can just [email me](mailto:aaronweiss74@gmail.com).

### An example. ###

	public class Example1 extends SimpleBot {
		public Example1() {
			super("Example1");
		}
		
		@Override
		public void receive(String[] message, Channel session) {
			for (String token : message)
				if (token.startsWith("#"))
					this.join(token);
			this.say("I'm really annoying.", message[2], session);
		}
	}
	
### Another example. ###

	public class Example2 extends MessageBot {
		public Example2() {
			super("Example2");
		}
	
		@Override
		public void receive(Message message);
			if (message.type().equals("PRIVMSG")) {
				for (String token : message)
					if (token.startsWith("#"))
						this.join(token);
				if (message.channel().startsWith("#"))
					message.reply("I'm just slightly less annoying.");
			}
		}
	}

### Once more... with feeling! ###

	public class Example3 extends AutoBot {
		public Example3() {
			super("Example3");
		}
	
		@Override
		public void joinAll() {
			this.join("#channel");
		}
		
		@Override
		public void receive(Message message) {
			if (Bot.containsIgnoreCase("say you're happy now", message.message())) {
				message.reply("Once more, with feeling!");
			}
			super.receive(message);
		}
	}

### Now with SSL support! ###

	public class Example4 extends AutoBot {
		public Example4() {
			// n.b. you'll need to add self-signed certs to your keystore or this'll be a problem.
			super("Example4", false, true); // Yep, it was that easy.
		}

		@Override
		public void joinAll() {
			this.join("#channel");
		}

		@Override
		public void receive(Message message) {
			if (Bot.containsIgnoreCase("say you're happy now", message.message())) {
				message.reply("Once more, with feeling!");
			}
			super.receive(message);
		}
	}
	
### Need more examples? ###
Just check out the Examples included in the source code! There's an improved version of the bot in Example3 named Sweet, made in honor of the demon from Buffy the Vampire Slayer's "Once More With Feeling." There's also a basic statistics bot named Stati (STAT-EE) that can provide you mostly useless information as a demonstration of the new work-in-progress information API. Of course, there's also a new version of the classic "pass the juice" JuiceBot from which the library got its name. There's also a bot named MetalGear that should demonstrate how to properly keep your bot silent on the client-side.

### Acknowledgements ###
* [angelsl](http://www.github.com/angelsl) for helping with the IRC protocol stuff.
* [Peter Atashian](http://www.github.com/retep998) for helping with testing.
* [rice](http://www.github.com/wahlao) for the inspiration.
* [FyreChat](http://www.fyrechat.net/) for allowing me to build this library on it.
* [Mitchell Andrews](http://wobbier.com/) for helping the library gain some steam.
