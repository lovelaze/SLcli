package com.lovelaze.slcmd;

public class Departure {
	
	private String destination;
	private String time;
	private String line;
	private String transport;
	
	public Departure(String destination, String time, String line, String transport) {
		this.destination = destination;
		this.time = time;
		this.line = line;
		this.transport = transport;
	}
	
	public String getDestination() {
		return destination;
	}
	public String getLine() {
		return line;
	}
	public String getTime() {
		return time;
	}
	public String getTransport() {
		return transport;
	}
	
}
