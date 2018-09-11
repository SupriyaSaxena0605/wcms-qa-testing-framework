package gov.nci.webanalytics;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;

// Represents a single Adobe Analytics request beacon with query parameters
public class Beacon extends AnalyticsRequest {
	// TODO: move server strings into config	
	// TODO: remove unused methods
	// TODO: reuse collection method from ParsedURL() instead of the local getList()
	//	TODO: Handle null exceptions in has() methods
	//	TODO: Create 'catch-all' Contains() method		
	
	// Constants
	public static final String TRACKING_SERVER = "nci.122.2o7.net";
	
	// Parameter values from URL
	static final String CHANNEL = "ch";
	static final String EVENTS = "events";
	static final String LINKTYPE = "pe";
	static final String LINKNAME = "pev2";
	static final String LINKURL = "pev1";
	
	// Partial parameter values. Each prop, eVar, and hier is its own query parameter. 
	// The getNumberedParams() method handles the logic of appending the number values
	// to each of these query parameter
	static final String PROP_PARTIAL = "c";
	static final String EVAR_PARTIAL = "v";
	static final String HIER_PARTIAL = "h";
	
	/// Regexes to match certain tracking value patterns
	public final String REGEX_MMDDYY = "(0[1-9]|1[012])[- \\/.](0[1-9]|[12][0-9]|3[01])[- \\/.](19|20)\\d\\d"; // "mm/dd/yyyy"
	public final String REGEX_TIMESTAMP_PIPE = "\\d{4}\\|\\d{1,2}\\|\\d{1,2}\\|\\d{1,2}"; // e.g. 05/06/2018
	public final String REGEX_TIME_PARTING = "\\d{1,2}:\\d{2} (AM|PM)\\|[a-zA-z]+day"; // e.g. 2:35 PM|Tuesday
	public final String REGEX_BROWSER_SIZE = "(Extra wide|Desktop|Tablet|Mobile)";
	public final String REGEX_PAGELOAD_TIME = "\\d(1,4)";
	
	// Testable variable names
	public String[] suites; // s.account or s_account
	public String[] events; // events
	public String channels; // s.channel
	public List<String> props; // props, aka "Traffic Variables"
	public List<String> eVars; // eVars, aka "Conversion Variables"
	public List<String> hiers; // Hierarchy variables
	
	// This class represents an Adobe Analytics request beacon
	public Beacon(String url) {
		super(url);
		
		// Set our variables
		this.suites = getSuites();
		this.channels = getChannels();
		this.events = getEvents();
		this.props = getPropList();
		this.eVars = geteVarList();
		this.hiers = getHierList();
	}
	
	/**
	 * Get an array of suites (s.account) values from the URL path.
	 * @return suites (String[])
	 */
	private String[] getSuites() {
		try {
			String[] path = this.url.split("/");
			String[] suites = path[5].split(",");
			return suites;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Could not locate suite values in URL: " + url + "\nin Beacon: GetSuites()");
			return null;
		}
	}

	/**
	 * Get channel parameter value. 
	 * @return channel (String)
	 */
	private String getChannels() {
		for (NameValuePair param : this.paramsList) {
			if (param.getName().equalsIgnoreCase(CHANNEL)) {
				return param.getValue().trim();
			}
		}
		return "";
	}
	
	/**
	 * Get array of event values
	 * @param parms
	 * @return
	 */
	public String[] getEvents() {
		String rtnEvents = "";
		for (NameValuePair param : paramsList) {
			if (param.getName().equalsIgnoreCase(EVENTS)) {
				rtnEvents = param.getValue();
				break;
			}
		}
		return rtnEvents.split(",");
	}
	
	/**
	 * 
	 * @return
	 */
	private List<String> getPropList() {
		List <String> rtn = getNumParams(PROP_PARTIAL, paramsList, 75);
		return rtn;
	}

	/**
	 * 
	 * @return
	 */
	private List<String> geteVarList() {
		List <String> rtn = getNumParams(EVAR_PARTIAL, paramsList, 75);
		return rtn;
	}

	/**
	 * 
	 * @return
	 */
	private List<String> getHierList() {
		List <String> rtn = getNumParams(HIER_PARTIAL, paramsList, 2);
		return rtn;
	}


	/**
	 * Get "Link Type" value (pe)(
	 * @return
	 */
	public String getLinkType() {
		for (NameValuePair param : paramsList) {
			if (param.getName().equalsIgnoreCase(LINKTYPE)) {
				return param.getValue().trim();
			}
		}
		return "";
	}

	/**
	 * Get "Link Name" value (pev2)(
	 * @return
	 */	
	public String getLinkName() {
		for (NameValuePair param : paramsList) {
			if (param.getName().equalsIgnoreCase(LINKNAME)) {
				return param.getValue().trim();
			}
		}
		return "";
	}

	/**
	 * Get "Link URL" value (pev1)(
	 * @return
	 */		
	public String getLinkUrl() {
		for (NameValuePair param : paramsList) {
			if (param.getName().equalsIgnoreCase(LINKURL)) {
				return param.getValue().trim();
			}
		}
		return "";
	}
	
	/**
	 * Check for parameters to verify that this is a link event
	 * @param paramList
	 * @return
	 */
	public boolean hasLinkType() {
		for (NameValuePair param : paramsList) {
			if (param.getName().equalsIgnoreCase(LINKTYPE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check for parameters to verify that this is a click-type/link event
	 * @param paramList
	 * @return
	 */
	public boolean isClickTypeEvent() {
		if(getLinkType().isEmpty() && getLinkName().isEmpty() && getLinkUrl().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
		
	/**************** Methods to check for given values in a beacon ****************/
	/*
	 * TODO: Replace "contains' with "matches" where possible
	 * TODO: Refactor common key/var logic into util methods
	 */
	
	/**
	 * Utility function to check for a given suite name
	 * @return
	 */
	public boolean hasSuite() {
		// TODO: fill this out
		return false;
	}
	
	/**
	 * Utility function to check for a link name value within a click beacon.
	 * @param name
	 * @return
	 */
	public boolean hasLinkName(String name) {
		if(this.getLinkName().equalsIgnoreCase(name)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Utility function to check for an event value within a click beacon.
	 * @param evt
	 * TODO: fix hardcoded values
	 */
	public boolean hasEvent(int eventNumber) {
		String evt = "event" + Integer.toString(eventNumber);
		for(String event : this.getEvents()) {
			if(evt.equalsIgnoreCase("event47")) {
				if(event.matches("^event47=\\d+")) {
					return true;
				}
			}
			else if(evt.equalsIgnoreCase("event92")) {
				if(event.matches("^event92=\\d+")) {
					return true;
				}
			}			
			else if(event.equalsIgnoreCase(evt)) {
				return true;
			}
		}
		return false;
	}
	

	/******************** Utility methods ****************************************/	
	
	/**
	 * Initialize a numbered list of empty elements. 
	 * @param size
	 * @return rtnList List<String>
	 */
	private List<String> initNumberedArrayList(int size) {		
		List<String> myList = new ArrayList<String>();
		while(myList.size() <= size) {
			myList.add(null);
		}
		return myList;
	}
	
	/**
	 * Get a list of numbered parameters and their values (e.g. [prop1="www.cancer.gov", prop2="/home", prop3="NCI"])
	 * @param myParam
	 * @param myList
	 * @param total
	 * @return List of values (String)
	 */
	protected List<String> getNumParams(String myParam, List<NameValuePair> myList, int total) {
		// Create a list with x names and null values
		List<String> rtnList = initNumberedArrayList(total);
		
		// Go through the list of populated parameter values and add those to the return list 
		// where the number matches
		for(NameValuePair pair : myList) {
			String name = pair.getName();
			String val = pair.getValue();
			// Regex: parameter name followed by 1 or more digits, starting with 1-9 only
			if(name.matches("^" + myParam + "[1-9]\\d*$")) {
				int index = Integer.parseInt(name.replace(myParam, ""));
				rtnList.set(index, val);
			}
		}
		return rtnList;
	}

	/**
	 * Utility function to check for a user-specified variable and value
	 * @param name
	 * @param value
	 * @return bool
	 */
	protected boolean hasVariable(String name, String value) {
		// TODO: fill this out
		return false;
	}
}
