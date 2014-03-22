package com.lovelaze.slcmd;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * This is the class used to parse XML data from the SL server
 */
public class SLParser {
	private final String realtid1Key = "QZYhoDUfzQNAOsK0DYcJAJKQZED6zOfx";
	private final String realtid2Key = "aIKTXKNqnQF1IkT9qCNuI8zvplXjUth5";
	private final String reseplanKey = "IqqpBW7AdnksiLFc9cvgegBKYsu7qYgW";

	public SLParser() {

	}

	public HashMap<String, Integer> getStations(String station) throws Exception {
		HashMap<String, Integer> stations = new HashMap<String, Integer>();
		
		URL url = new URL("https://api.trafiklab.se/sl/realtid/GetSite?stationSearch="+ station + "&key=" + realtid1Key);
		Document doc = parseXML(url);
		

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList siteNodes = (NodeList) xPath.evaluate("//Site",doc.getDocumentElement(), XPathConstants.NODESET);
		
		for (int i=0; i < siteNodes.getLength(); i++) {;
			String name = siteNodes.item(i).getChildNodes().item(3).getTextContent();
			int id = Integer.parseInt(siteNodes.item(i).getChildNodes().item(1).getTextContent());

			stations.put(name, id);
		}
		
		return stations;
		
	}

	/*
	 * Return the ID of a given site, returns null if no station was found
	 */
	public int getSiteID(String station) throws Exception {
		URL url = new URL("https://api.trafiklab.se/sl/realtid/GetSite?stationSearch="+ station + "&key=" + realtid1Key);
		Document doc = parseXML(url);

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xPath.evaluate("//Site/Number", doc.getDocumentElement(), XPathConstants.NODESET);

		int len = nodes.getLength();
		if (len < 1) {
			throw new Exception("No station returned.");

		} else if (len > 1) {
			throw new Exception("Multiple stations returned. Be more specific.");
		}

		System.out.println(nodes.item(0).getTextContent());
		return Integer.parseInt(nodes.item(0).getTextContent());
	}
	
	
	/*
	 * Return a list of departures give a site ID
	 */
	public ArrayList<ArrayList<Departure>> getDepartures(int siteID,
			int timeWindow) throws Exception {
		ArrayList<ArrayList<Departure>> departures = new ArrayList<ArrayList<Departure>>();
		for (int i=0; i<4; i++) {
			departures.add(new ArrayList<Departure>());
		}
		

		URL url = getAllDeparturesURL(siteID, timeWindow);
		Document doc = parseXML(url);
		XPath xPath = XPathFactory.newInstance().newXPath();

		// GATHER BUS DEPARTURES
		NodeList tempNodes = (NodeList) xPath.evaluate("//Bus",doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i = 0; i < tempNodes.getLength(); i++) {	// add each bus departure to the first arraylist
			NodeList childNodes = tempNodes.item(i).getChildNodes();
			String destination = childNodes.item(0).getTextContent();
			String time = childNodes.item(2).getTextContent();
			String line = childNodes.item(4).getTextContent();
			
			Departure d = new Departure(destination, time, line, "bus");
			departures.get(0).add(d);
		}

		// GATHER METRO DEPARTURES
		tempNodes = (NodeList) xPath.evaluate("//Metro//DepartureInfo",
				doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i = 0; i < tempNodes.getLength(); i++) {
			NodeList childNodes = tempNodes.item(i).getChildNodes();
			String destination = childNodes.item(0).getTextContent();
			String time = childNodes.item(1).getTextContent();
			String line = childNodes.item(2).getTextContent();
			Departure d = new Departure(destination, time, line, "metro");
			departures.get(1).add(d);
		}

		// GATHER TRAINS DEPARTURES
		tempNodes = (NodeList) xPath.evaluate("//Train",
				doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i = 0; i < tempNodes.getLength(); i++) {
			NodeList childNodes = tempNodes.item(i).getChildNodes();
			String destination = childNodes.item(0).getTextContent();
			String time = childNodes.item(2).getTextContent();
			String line = childNodes.item(4).getTextContent();
			Departure d = new Departure(destination, time, line, "train");
			departures.get(2).add(d);
		}

		// GATHER TRAMS DEPARTURES
		tempNodes = (NodeList) xPath.evaluate("//Tram",
				doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i = 0; i < tempNodes.getLength(); i++) {
			NodeList childNodes = tempNodes.item(i).getChildNodes();
			String destination = childNodes.item(0).getTextContent();
			String time = childNodes.item(2).getTextContent();
			String line = childNodes.item(4).getTextContent();
			Departure d = new Departure(destination, time, line, "tram");
			departures.get(3).add(d);
		}

		return departures;

	}

	/*
	 * Return a list of trips from destination A to B
	 */
	public ArrayList<Trip> getTravelTrips(int SID, int ZID)
			throws Exception {
		
		
		/*int SID = getSiteID(start);
		int ZID = getSiteID(end);*/

		URL url = getPlannerURL(SID, ZID);
		Document doc = parseXML(url);
		
		/* //SAVE XML DOCUMENT TO FILE FOR DEBUGGING PURPOSES
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		Result output = new StreamResult(new File("output.xml"));
		Source input = new DOMSource(doc);
		transformer.transform(input, output);
		// END OF SAVING */
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		// create list of trips
		ArrayList<Trip> trips = new ArrayList<Trip>();
		
		// Create trips
		NodeList tripNodes = (NodeList) xPath.evaluate("//Trip", doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i=0; i < tripNodes.getLength(); i++) {
			NodeList tempList = tripNodes.item(i).getChildNodes();
			// first get the summary
			Node summaryNode = tempList.item(0);
			String summaryOrigin = summaryNode.getChildNodes().item(0).getTextContent();
			String summaryDestination = summaryNode.getChildNodes().item(1).getTextContent();
			String summaryDepartureTime = summaryNode.getChildNodes().item(3).getTextContent();
			String summaryArrivalTime = summaryNode.getChildNodes().item(5).getTextContent();
			String summaryDuration = summaryNode.getChildNodes().item(8).getTextContent();
			
			Trip trip = new Trip(summaryOrigin, summaryDestination, summaryDepartureTime, summaryArrivalTime, summaryDuration);
			
			// then gather all subtrips
			for (int j=1; j < tempList.getLength(); j++) { // maybe could use xpath to find subtrips from tripNodes instead
				Node subTripNode = tempList.item(j);
				String origin = subTripNode.getChildNodes().item(0).getTextContent(); // origin
				String destination = subTripNode.getChildNodes().item(1).getTextContent(); // destination
				String departureTime = subTripNode.getChildNodes().item(3).getTextContent(); // start-time
				String arrivalTime = subTripNode.getChildNodes().item(5).getTextContent(); // end-time
				String transportType = subTripNode.getChildNodes().item(6).getChildNodes().item(0).getTextContent(); // type
				String transportLine = subTripNode.getChildNodes().item(6).getChildNodes().item(2).getTextContent(); // line
				String transportTowards = subTripNode.getChildNodes().item(6).getChildNodes().item(3).getTextContent(); // towards
				String stopsURI = subTripNode.getChildNodes().item(7).getTextContent(); // intermediate stops uri
				
				SubTrip sub = new SubTrip(origin, destination, departureTime, arrivalTime, transportType, transportLine, transportTowards, stopsURI);
				trip.addSubTrip(sub);
			}
			
			/* NON-FUNCTIONAL
			NodeList subNodes = (NodeList) xPath.evaluate("//SubTrip", tempList, XPathConstants.NODESET);
			for (int j=0; j< subNodes.getLength(); j++) {
				Node subTripNode = subNodes.item(i);
				String origin = subTripNode.getChildNodes().item(0).getTextContent(); // origin
				String destination = subTripNode.getChildNodes().item(1).getTextContent(); // destination
				String departureTime = subTripNode.getChildNodes().item(3).getTextContent(); // start-time
				String arrivalTime = subTripNode.getChildNodes().item(5).getTextContent(); // end-time
				String transportType = subTripNode.getChildNodes().item(6).getChildNodes().item(0).getTextContent(); // type
				String transportLine = subTripNode.getChildNodes().item(6).getChildNodes().item(2).getTextContent(); // line
				String stopsURI = subTripNode.getChildNodes().item(7).getTextContent(); // intermediate stops uri
				
				SubTrip sub = new SubTrip(origin, destination, departureTime, arrivalTime, transportType, transportLine, stopsURI);
				trip.addSubTrip(sub);
				
			}*/
			
				
			
			trips.add(trip);
		}

		return trips;
	}

	/*
	 * Return an XML-URL given a site ID and time window
	 */
	private URL getAllDeparturesURL(int siteID, int timeWindow)
			throws IOException {
		String uri = "https://api.trafiklab.se/sl/realtid2/GetAllDepartureTypes.xml/"
				+ siteID + "/" + timeWindow + "?key=" + realtid2Key;
		return new URL(uri);
	}

	private URL getPlannerURL(int startID, int endID)
			throws MalformedURLException {
		String uri = "https://api.trafiklab.se/sl/reseplanerare.xml?S="
				+ startID + "&Z=" + endID + "&Timesel=depart&Lang=sv&key="
				+ reseplanKey;
		return new URL(uri);
	}

	/*
	 * Parses a given URL into a Document
	 */
	private Document parseXML(URL url) throws IOException,
			ParserConfigurationException, SAXException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		InputStream xml = connection.getInputStream();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xml);

		return doc;
	}

}
