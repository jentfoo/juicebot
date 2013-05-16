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
package us.aaronweiss.juicebot;

import io.netty.channel.Channel;

import java.net.SocketAddress;

/**
 * A basic abstraction for all IRC clients.
 * 
 * @author Aaron Weiss
 * @version 2.0
 * @since 1.0
 */
public interface Client {
	/**
	 * Connects to the specified address.
	 * @param address hostname, IPv6, or IPv4 name with or without port (assumes default as 6667)
	 * @return the newly created session
	 */
	public Channel connect(String address);

	/**
	 * Connects to the specified address.
	 * @param address hostname, IPv6, or IPv4 name
	 * @param port the port to connect on
	 * @return the newly created session
	 */
	public Channel connect(String address, String port);
	

	/**
	 * Connects to the specified address.
	 * @param address the <code>SocketAddress</code> to connect to
	 * @return the newly created session
	 */
	public Channel connect(SocketAddress address);
	
	/**
	 * Disconnects from the specified session.
	 * 
	 * @param session the session to disconnect from
	 */
	public void disconnect(Channel session);
	
	/**
	 * Performs a set of actions upon opening a new session.
	 * 
	 * @param session the newly connected session
	 */
	public void connected(Channel session);
	
	/**
	 * Performs a set of actions periodically.
	 * n.b. you must set Bot.timeUnit and/or Bot.periodicTime. (Defaults: Seconds and -1, respectively)
	 * 
	 * @param session the open session
	 */
	public void periodic(Channel session);
	
	/**
	 * Performs a set of actions upon closing a session.
	 * 
	 * @param session the now-closed session
	 */
	public void disconnected(Channel session);
	
	/**
	 * Gets whether or not the client is connected to any sessions.
	 * 
	 * @return the client's current connection status
	 */
	public boolean isConnected();
	
	/**
	 * Gets whether or not the client uses the simple messaging API.
	 * 
	 * @return the client's usage of the simple messaging API
	 */
	public boolean isSimpleMessageReceiver();
	
	/**
	 * Sends a message to all connected sessions.
	 * 
	 * @param message the message to send
	 */
	public void send(String message);
	
	/**
	 * Sends a message to the specified session.
	 * 
	 * @param message the message to send
	 * @param session the session to send it to 
	 */
	public void send(String message, Channel session);
	
	/**
	 * Sends a message to all connected sessions.
	 * 
	 * @param message the message to send (split as words)
	 */
	public void send(String[] message);
	
	/**
	 * Sends a message to the specified session.
	 * 
	 * @param message the message to send (split as words)
	 * @param session the session to send it to 
	 */
	public void send(String[] message, Channel session);
	
	/**
	 * Receives a whole message for the simple messaging API.
	 * 
	 * @param message the message being received
	 * @param session the session receiving it from
	 */
	public void receive(String message, Channel session);
	

	/**
	 * Receives a message split as words for the simple messaging API.
	 * 
	 * @param message the message being received (split as words)
	 * @param session the session receiving it from
	 */
	public void receive(String[] message, Channel session);
	
	/**
	 * Receives a message through the POJO messaging API.
	 * 
	 * @param message the message being received
	 */
	public void receive(Message message);
}
