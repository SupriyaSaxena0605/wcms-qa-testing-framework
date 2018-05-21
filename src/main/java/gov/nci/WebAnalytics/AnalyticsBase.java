package gov.nci.WebAnalytics;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.openqa.selenium.WebDriver;

import com.nci.Utilities.ConfigReader;

public class AnalyticsBase {
	
	// Constants
	public static final String S_CODE_NAME = "s_code.js";
	public static final String S_ACCOUNT = "s_account";
	public static final String NCI_FUNCTIONS_NAME = "NCIAnalytics";
	public static final String PAGE_NAME = "www.cancer.gov";
	public static final String TRACKING_SERVER = "nci.122.2o7.net";

	// Driver object
	public WebDriver driver;	

	// Get our page navigation URLs
	public ConfigReader config = new ConfigReader();
	public String homePage = config.getPageURL("HomePage");
	public String blogPostPage = config.getPageURL("BlogPostPage");
	public String blogSeriesPage = config.getPageURL("BlogSeriesPage");
	public String cthpPatient = config.getPageURL("CTHPPatient");
	public String cthpHP = config.getPageURL("CTHPHP");
	public String innerPage = config.getPageURL("InnerPage");
	public String landingPage = config.getPageURL("LandingPage");
	public String pdqPage = config.getPageURL("PDQPage");
	public String topicPage = config.getPageURL("TopicPage");
	public String spanishPage = config.getPageURL("SpanishPage");
	public String appModulePage = config.getPageURL("AppModulePage");
	
	// Beacon properties
	public URI uri;
	public String[] suites;	
	public List<NameValuePair> params;
	public String channel;
	public String[] events;
	public List<NameValuePair> props; 
	public List<NameValuePair> eVars; 
	public List<NameValuePair> hiers;
	
	/**
	 * No-arg constructor - init all vars
	 */
	public AnalyticsBase() {
		uri = null;
		suites = new String[0];		
		params = new ArrayList<>();
		channel = "";
		events = new String[0];
		props = new ArrayList<>();
		eVars = new ArrayList<>();
		hiers = new ArrayList<>();
	}
	
	/**
	 * Constructor
	 * @param beaconUrl
	 * @throws MalformedURLException
	 */
	public AnalyticsBase(String beaconUrl) {
		uri = URI.create(beaconUrl);
		suites = getSuites(uri);
		params = buildParamsList(uri);
		channel = getChannel(params);
		events = getEvents(params);
		props = getProps(params);
		eVars = getEvars(params);
		hiers = getHiers(params);
	}
	
	/**
	 * Get the reporting suites (s_account) value
	 * as an array of strings
	 * @param uri
	 * @return
	 */
	protected String[] getSuites(URI uri) {
		String[] path = uri.getPath().split("/");
		String[] rtnSuites = path[3].split(",");
		return rtnSuites;
	}
	
	/**
	 * Split URI into list of encoded elements
	 * @param uri
	 * @return retParams
	 * TODO: replace deprecated parse() method
	 */
	public List<NameValuePair> buildParamsList(URI uri) {
		List<NameValuePair> rtnParams = new ArrayList<>();
		rtnParams = URLEncodedUtils.parse(uri, "UTF-8");
		return rtnParams;
	}
	
	/**
	 * Get channel value 
	 * @param parms
	 * @return
	 */
	public String getChannel(List<NameValuePair> parms) {
		String rtnChannel = "";		
		for (NameValuePair param : parms) {
			if (param.getName().toLowerCase().equals("ch")) {
				rtnChannel = param.getValue();
				break;
			}
		}
		return rtnChannel;
	}
	
	/**
	 * Get array of event values
	 * @param parms
	 * @return
	 */
	public String[] getEvents(List<NameValuePair> parms) {
		String rtnEvents = "";		
		for (NameValuePair param : parms) {
			if (param.getName().toLowerCase().equals("events")) {
				rtnEvents = param.getValue();
				break;
			}
		}
		return rtnEvents.split(",");
	}
	
	/**
	 * Get array of props ('c' values in beacon)
	 * @param parms
	 * @return
	 */
	public List<NameValuePair> getProps(List<NameValuePair> parms) {
		List<NameValuePair> rtnProps = new ArrayList<>();		
		for (NameValuePair param : parms) {
			// Regex: "c" followed by 1 or more digits, starting with 1-9 only
			if(param.getName().matches("^[Cc][1-9]\\d*$")) {
				String propName = param.getName().replaceAll("[Cc]", "prop");
				String propValue = param.getValue();
				rtnProps.add(new BasicNameValuePair(propName, propValue));
			}
		}
		return rtnProps;
	}
	
	/**
	 * Get array of eVars ('v' values in beacon)
	 * @param parms
	 * @return
	 */
	public List<NameValuePair> getEvars(List<NameValuePair> parms) {
		List<NameValuePair> rtnEvars = new ArrayList<>();		
		for (NameValuePair param : parms) {
			// Regex: "v" followed by 1 or more digits, starting with 1-9 only			
			if(param.getName().matches("^[Vv][1-9]\\d*$")) {
				String eVarName = param.getName().replaceAll("[Vv]", "eVar");
				String eVarValue = param.getValue();
				rtnEvars.add(new BasicNameValuePair(eVarName, eVarValue));
			}
		}
		return rtnEvars;
	}
	
	/**
	 * Get array of hierarchy values ("hiers" or "h" values in beacon)
	 * @param parms
	 * @return
	 */
	public List<NameValuePair> getHiers(List<NameValuePair> parms) {
		List<NameValuePair> rtnHiers = new ArrayList<>();		
		for (NameValuePair param : parms) {
			// Regex: "h" followed by 1 or more digits, starting with 1-9 only			
			if(param.getName().matches("^[Hh][1-9]\\d*$")) {
				String hierName = param.getName().replaceAll("[Hh]", "hier");
				String hierValue = param.getValue();
				rtnHiers.add(new BasicNameValuePair(hierName, hierValue));
			}
		}
		return rtnHiers;
	}

	/**
	 * Check query params to see if this is a link tracking event
	 * @param parms
	 * @return
	 */
	public boolean hasParam(List<NameValuePair> paramList, String myParam) {
		for (NameValuePair param : paramList) {
			if (param.getName().toLowerCase().equals(myParam)) {
				return true;
			}
		}
		return false;
	}
	
}
