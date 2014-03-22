package com.lovelaze.slcmd;

public class SubTrip {
	
	private String origin;
	private String destination;
	private String departureTime;
	private String arrivalTime;
	private String transportType;
	private String transportLine;
	private String transportTowards;
	private String intermediateStopsURI;
	
	public SubTrip(String origin, String destination, String departureTime, String arrivalTime,
			String transportType, String transportLine, String transportTowards,String stopsURI) {
		
		this.origin = origin;
		this.destination = destination;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.transportType = transportType;
		this.transportLine = transportLine;
		this.transportTowards = transportTowards;
		this.intermediateStopsURI = stopsURI;
		
	}
	
	public String getArrivalTime() {
		return arrivalTime;
	}
	
	public String getDepartureTime() {
		return departureTime;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getIntermediateStopsURI() {
		return intermediateStopsURI;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public String getTransportLine() {
		return transportLine;
	}
	
	public String getTransportType() {
		return transportType;
	}
	
	public String getTransportTowards() {
		return transportTowards;
	}
	

}
