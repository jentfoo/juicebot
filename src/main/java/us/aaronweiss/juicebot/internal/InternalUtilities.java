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
package us.aaronweiss.juicebot.internal;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * An internal utilities library to prevent code repetition.
 * 
 * @author Aaron Weiss
 * @version 1.0
 * @since 2.0
 */
public final class InternalUtilities {
	/**
	 * Prints the specified message to console with a timestamp.
	 * 
	 * @param message
	 *            the message to print.
	 */
	public static void print(String message) {
		print(message, false);
	}

	/**
	 * Prints the specified message to console with a timestamp with an included
	 * newline.
	 * 
	 * @param message
	 *            the message to print.
	 */
	public static void println(String message) {
		print(message, true);
	}

	/**
	 * Outputs text to the console for a user to read.
	 * 
	 * @param message
	 *            the text to output
	 * @param newLine
	 *            whether or not to end the message with a new line carriage
	 */
	private static void print(String message, boolean newLine) {
		System.out.print("[" + now() + "] " + message + ((newLine) ? "\n" : ""));
	}

	/**
	 * Gets a timestamp for the current moment in time.
	 * 
	 * @return a timestamp for this moment in time
	 */
	private static String now() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		return ((hours + (TimeZone.getDefault().useDaylightTime() ? 0 : -1)) + ":" + ((minutes < 10) ? "0" + minutes : "" + minutes) + ":" + ((seconds < 10) ? "0" + seconds : "" + seconds));
	}
}
