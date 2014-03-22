package com.lovelaze.slcmd;
import java.util.ArrayList;


public class Trip {

	private String origin;
	private String destination;
	private String departureTime;
	private String arrivalTime;
	private String duration;
	private ArrayList<SubTrip> subTrips;
	
	public Trip(String origin, String destination, String departureTime, String arrivalTime, String duration) {
		this.origin = origin;
		this.destination = destination;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.duration = duration;
		subTrips = new ArrayList<SubTrip>();
	}
	
	public void addSubTrip(SubTrip subTrip) {
		subTrips.add(subTrip);
	}
	
	public ArrayList<SubTrip> getSubTrips() {
		return subTrips;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getArrivalTime() {
		return arrivalTime;
	}
	
	public String getDepartureTime() {
		return departureTime;
	}
	
	public String getDuration() {
		return duration;
	}
	
}
