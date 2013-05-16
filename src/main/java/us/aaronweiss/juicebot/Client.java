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
	public Channel connect(String address);
	public Channel connect(String address, String port);
	public Channel connect(SocketAddress address);
	public void disconnect(Channel session);
	
	public void connected(SocketAddress address);
	public void periodic(Channel session);
	public void disconnected(Channel session);
	
	public boolean isConnected();
	public boolean isSimpleMessageReceiver();
	
	public void send(String message);
	public void send(String message, Channel session);
	public void send(String[] message);
	public void send(String[] message, Channel session);
	
	public void receive(String message, Channel session);
	public void receive(String[] message, Channel session);
	public void receive(Message message);
}
