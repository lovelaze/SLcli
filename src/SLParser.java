import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/*
 * This is the class used to parse XML data from the SL server
 */
public class SLParser {
	private final String realtid1Key = "QZYhoDUfzQNAOsK0DYcJAJKQZED6zOfx";
	private final String realtid2Key = "aIKTXKNqnQF1IkT9qCNuI8zvplXjUth5";
	
	public SLParser() {
		
		
	}
	
	
	
	
	public ArrayList<String> getDepartures(String station, int timeWindow) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		
		ArrayList<String> departures = new ArrayList<String>();
		
		int siteID;
		try {
			siteID = getSiteID(station);
		} catch (Exception e) {
			departures.add(e.getMessage());
			return departures;
		} 
		
		URL url = getAllDeparturesURL(siteID, timeWindow);
		Document doc = parse(url);
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		// GATHER BUS DEPARTURES
		NodeList tempNodes = (NodeList) xPath.evaluate("//Bus", doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i=0; i < tempNodes.getLength(); i++) {
			NodeList childNodes = tempNodes.item(i).getChildNodes();
			String destination = childNodes.item(0).getTextContent();
			String time = childNodes.item(2).getTextContent();
			String line = childNodes.item(4).getTextContent();
			departures.add(time + "\t- " + line + " " + destination);
		}
		
		// GATHER METRO DEPARTURES
		tempNodes = (NodeList) xPath.evaluate("//Metro//DepartureInfo", doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i=0; i < tempNodes.getLength(); i++) {
			NodeList childNodes = tempNodes.item(i).getChildNodes();
			String destination = childNodes.item(0).getTextContent();
			String time = childNodes.item(1).getTextContent();
			String line = childNodes.item(2).getTextContent();
			departures.add(time + "\t- " + line + " " + destination);
		}
		
		// GATHER TRAINS DEPARTURES
		tempNodes = (NodeList) xPath.evaluate("//Train", doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i=0; i < tempNodes.getLength(); i++) {
			NodeList childNodes = tempNodes.item(i).getChildNodes();
			String destination = childNodes.item(0).getTextContent();
			String time = childNodes.item(2).getTextContent();
			String line = childNodes.item(4).getTextContent();
			departures.add(time + "\t- " + line + " " + destination);
		}
		
		// GATHER TRAMS DEPARTURES
		tempNodes = (NodeList) xPath.evaluate("//Tram", doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i=0; i < tempNodes.getLength(); i++) {
			NodeList childNodes = tempNodes.item(i).getChildNodes();
			String destination = childNodes.item(0).getTextContent();
			String time = childNodes.item(2).getTextContent();
			String line = childNodes.item(4).getTextContent();
			departures.add(time + "\t- " + line + " " + destination);
		}
		
		
		return departures;
	}
	
	/*
	 * Return the ID of a given site, returns null if no station was found
	 */
	public int getSiteID(String station) throws Exception {
		URL url = new URL("https://api.trafiklab.se/sl/realtid/GetSite?stationSearch="+station+"&key="+realtid1Key);
		Document doc = parse(url);
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xPath.evaluate("//Site/Number", doc.getDocumentElement(), XPathConstants.NODESET);
		
		int len = nodes.getLength();
		if (len < 1) {
			throw new Exception("No station returned.");
			
		} else if (len > 1) {
			throw new Exception("Multiple stations returned. Be more specific.");
		}

		return Integer.parseInt(nodes.item(0).getTextContent());
	}
	
	
	/*
	 * Return an XML-URL given a site ID and time window
	 */
	private URL getAllDeparturesURL(int siteID, int timeWindow) throws IOException {
		String uri = "https://api.trafiklab.se/sl/realtid2/GetAllDepartureTypes.xml/"+siteID+ "/"+timeWindow+"?key=" + realtid2Key;
		return new URL(uri);
	}
	
	
	
	/*
	 * Parses a given URL into a Document
	 */
	private Document parse(URL url) throws IOException, ParserConfigurationException, SAXException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");
		
		InputStream xml = connection.getInputStream();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		db = dbf.newDocumentBuilder();
		doc = db.parse(xml);

		return doc;
	}

}
