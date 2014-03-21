package com.lovelaze.slcmd;
/*
 * This is the main class for the command-line SL-parser
 */

import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;

public class Main {

	private Options options;
	private SLParser sl;

	public Main() {
		options = new Options();
		createOptions();
		sl = new SLParser();

	}

	/*
	 * Create the cmd-line options.
	 */
	private void createOptions() {

		options.addOption("h", false, "Display help.");
		options.addOption("d", true, "Get departures from a given location.");
		options.addOption("t", true, "Time Window");
	}

	/*
	 * Return an arraylist of the option descriptions
	 */
	public void getOptionsDescriptions() {

	}

	private void printDepartures(String station, int timeWindow)
			throws Exception {

		ArrayList<ArrayList<Departure>> departures = sl.getDepartures(station, 10);
		
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
	

	public static void main(String[] args) throws Exception {

		Main main = new Main();

		String[] testArgs = { "-d slussen 10" };

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(main.options, args);

		// commandline arguments
		/*
		 * if (cmd.hasOption("h")) { System.out.println("-h");
		 * 
		 * }
		 */

		if (cmd.hasOption("d")) {
			if (cmd.hasOption("t")) {
				String station = cmd.getOptionValue("d").trim();
				int timeWindow = Integer.parseInt(cmd.getOptionValue("t")
						.trim());
				main.printDepartures(station, timeWindow);

			} else {
				String station = cmd.getOptionValue("d").trim();
				main.printDepartures(station, 10);
			}

		}

		/*
		 * ArrayList<String> temp = main.sl.getTravelTrips("finnboda%hamn",
		 * "slussen"); for (String s : temp) { System.out.println(s); }
		 */

		main.printDepartures("slussen", 10);

	}

}
