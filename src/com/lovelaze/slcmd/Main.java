package com.lovelaze.slcmd;
/*
 * This is the main class for the command-line SL-parser
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class Main {

	private Options options;
	private SLParser sl;

	public Main() {
		options = new Options();
		createOptions(options);
		sl = new SLParser();

	}

	/*
	 * Create the cmd-line options.
	 */
	@SuppressWarnings("static-access")
	private void createOptions(Options options) {
		

		Option d = OptionBuilder.withArgName("-d")
				.hasArg()
				.withDescription("List departures from a given station")
				.create("d");
		
		Option w = OptionBuilder.withArgName("-w")
				.hasArg()
				.withDescription("Time window for a departure")
				.create("w");
		
		Option t = OptionBuilder.hasArgs(2)
				.withArgName("-t")
				.withDescription("List of trips from A to B")
				.create("t");
		
		Option h = OptionBuilder.withDescription("Print help")
				.withArgName("-h")
				.create("h");
		
		options.addOption(d);
		options.addOption(w);
		options.addOption(t);
		options.addOption(h);
		
	}

	/*
	 * Print help 
	 */
	public void printHelp() {
		@SuppressWarnings("unchecked")
		Iterator<Option> it = (Iterator<Option>) options.getOptions().iterator();
		
		while (it.hasNext()) {
			Option op = (Option) it.next();
			System.out.println( op.getArgName() + "\t" + op.getDescription());
		}
		

	}
	
	
	/*
	 * print print departures from a station given a time window
	 */
	public void printDepartures(String station, int timeWindow)
			throws Exception {
		
		int siteID = chooseStation(station);

		ArrayList<ArrayList<Departure>> departures = sl.getDepartures(siteID, timeWindow);
		
		for (int i=0; i<departures.size(); i++) {
			if (departures.get(i).size() > 0) {
				System.out.println("-----"+departures.get(i).get(0).getTransport().toUpperCase()+"-----");
			}
			
			for (Departure d : departures.get(i)) {
				String temp = "\t"+d.getTime() + "\t- " +d.getLine() + " " + d.getDestination();
				System.out.println(temp);
			}
		}
		
	}
	
	/*
	 * Print trips from A to B
	 */
	public void printTrips(String origin, String destination) throws Exception {
		
		int SID = chooseStation(origin);
		int ZID = chooseStation(destination);
		
		ArrayList<Trip> trips = sl.getTravelTrips(SID, ZID);
		
		for (Trip t : trips) {
			System.out.println(t.getOrigin() + " " + t.getDepartureTime() + " -> " + t.getDestination() + " " + t.getArrivalTime() + " [" + t.getDuration()+"]");
			ArrayList<SubTrip> subs = t.getSubTrips();
			for (SubTrip s : subs) {
				System.out.println("\t("+s.getTransportType() + " " + s.getTransportLine()  + " " + s.getTransportTowards()+ "): " + s.getOrigin() + " " +s.getDepartureTime()+ " -> " +s.getDestination() +" "+s.getArrivalTime());
			}
		}
		
	}
	
	/*
	 * Returns a siteID from a chosen station
	 */
	private int chooseStation(String station) throws Exception {
		int siteID = 0;
		
		// given a list of station choices, choose one
		Map<String, Integer> stations = new TreeMap<String, Integer>(sl.getStations(station)); // use treemap to sort the keys
		
		if (stations.size() == 1) {
			siteID = (int) stations.values().toArray()[0];
		} else {
			int i=0;
			for (Map.Entry<String, Integer> entry : stations.entrySet()) {
				String name = entry.getKey();
				int id = entry.getValue();
				System.out.println("["+i+"] " +name + " - " + id);
				i++;
			}
			
			System.out.print("> ");
			Scanner in = new Scanner(System.in);
			i = in.nextInt();
			in.close();
			
			siteID = (int) stations.values().toArray()[i];
		}

		return siteID;
		
	}
	

	public static void main(String[] args) throws Exception {

		Main main = new Main();

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(main.options, args);

		if (cmd.hasOption("d")) {
			if (cmd.hasOption("w")) {
				String station = cmd.getOptionValue("d").trim();
				int timeWindow = Integer.parseInt(cmd.getOptionValue("w").trim());
				main.printDepartures(station, timeWindow);

			} else {
				String station = cmd.getOptionValue("d").trim();
				main.printDepartures(station, 30);
			}
		} else if(cmd.hasOption("t")) {
			String start = cmd.getOptionValues("t")[0].trim();
			String end = cmd.getOptionValues("t")[1].trim();
			main.printTrips(start, end);
		} else if (cmd.hasOption("-h")) {
			main.printHelp();
		}
		
	}
	
}
