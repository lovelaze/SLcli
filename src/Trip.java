import java.util.ArrayList;


public class Trip {

	private String origin;
	private String destination;
	private String departureTime;
	private String arrivalTime;
	private ArrayList<SubTrip> subTrips;
	
	public Trip(String origin, String destination, String departureTime, String arrivalTime) {
		this.origin = origin;
		this.destination = destination;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
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
	
	
}
