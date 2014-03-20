/*
 * This is the main class for the command-line SL-parser
 */


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	}
	
	/*
	 * Return an arraylist of the option descriptions
	 */
	public void getOptionsDescriptions() {
		
	}
	

	public static void main(String[] args) throws Exception {

		Main main = new Main();

		String[] testArgs = { "-h", "-d test" };

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(main.options, testArgs);

		
		// commandline arguments
		/*if (cmd.hasOption("h")) {
			System.out.println("-h");
			
		}

		if (cmd.hasOption("d")) {
			System.out.println("-d");

		}*/
		
		//
		
		ArrayList<String> departures = main.sl.getDepartures("slussen", 60);
		
		for (String i: departures) {
			System.out.println(i);
		}

	}
}
