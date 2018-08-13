package gov.nci.WebAnalytics;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;

public class AnalyticsRequest {
	// TODO: move server strings into config	
	// TODO: remove unused methods
	// TODO: build setter/getter for channel? 

	// Constants
	public static final String STATIC_SERVER = "static.cancer.gov";
	public static final String TRACKING_SERVER = "nci.122.2o7.net";
	
	// A request URL
	private String url;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	// A request URI
	private URI uri;	
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	// A list of query parameters
	private List<NameValuePair> paramsList;
	public List<NameValuePair> getParamsList() {
		return paramsList;
	}
	public void setParamsList(List<NameValuePair> paramsList) {
		this.paramsList = paramsList;
	}
		
	/**
	 * Constructor with 'url' arg
	 * @param url
	 */
	public AnalyticsRequest(String url) {
		setUrl(url);
		setUri(null);
		setParamsList(new ArrayList<NameValuePair>());
	}
	
	/**
	 * Build the 
	 * @throws NullPointerException
	 */
	public void buildParamsList() throws NullPointerException {
		setUri(createUri(url));
		setParamsList(AnalyticsParams.getList(uri));		
	}
	
	/**
	 * Create URI object from a given URL string
	 * @param url
	 * @return
	 */
	private static URI createUri(String url) {
		try {
			URI rtnUri = URI.create(url);
			return rtnUri;
		} catch (IllegalArgumentException ex) {
			System.out.println("Invalid request URL \"" + url + 
					"\\\" at AnalyticsRequest:createURI()");
			return null;
		}
	}
	
	/**
	 * Get the reporting suites (s_account) value
	 * as an array of strings
	 * @param uri
	 * @return
	 */
	protected String[] getSuites(URI uri) {
		try {
			String[] path = uri.getPath().split("/");
			String[] rtnSuites = path[3].split(",");
			return rtnSuites;
		} 
		catch(ArrayIndexOutOfBoundsException ex) {
			System.out.println("Invalid URI path: \"" + uri.getPath() + "\\\" at AnalyticsBase:getSuitesI()");			
			return null;
		}
	}
	
	/**
	 * Get channel value 
	 * @param parms
	 * @return
	 */
	public String getChannel(List<NameValuePair> parms) {
		for (NameValuePair param : parms) {
			if (param.getName().equalsIgnoreCase(AnalyticsParams.CHANNEL)) {
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
			if (param.getName().equalsIgnoreCase(AnalyticsParams.EVENTS)) {
				rtnEvents = param.getValue();
				break;
			}
		}
		return rtnEvents.split(",");
	}
	
	/**
	 * Get list of props ('c' values in request)
	 * @param parms
	 * @return
	 */
	public List<NameValuePair> getProps() {
		return getNumberedParams(paramsList, AnalyticsParams.PROP_PARTIAL, "prop");
	}
	
	/**
	 * Get list of eVars ('v' values in request)
	 * @param parms
	 * @return
	 */
	public List<NameValuePair> getEvars() {
		return getNumberedParams(paramsList, AnalyticsParams.EVAR_PARTIAL, "eVar");
	}
	
	/**
	 * Get list of hierarchy values ("h" values in request)
	 * @param parms
	 * @return
	 */
	public List<NameValuePair> getHiers(List<NameValuePair> parms) {
		return getNumberedParams(parms, AnalyticsParams.HIER_PARTIAL, "hier");
	}

	/**
	 * Get "Link Type" value (pe)(
	 * @return
	 */
	public String getLinkType() {
		for (NameValuePair param : paramsList) {
			if (param.getName().equalsIgnoreCase(AnalyticsParams.LINKTYPE)) {
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
			if (param.getName().equalsIgnoreCase(AnalyticsParams.LINKNAME)) {
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
			if (param.getName().equalsIgnoreCase(AnalyticsParams.LINKURL)) {
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
			if (param.getName().equalsIgnoreCase(AnalyticsParams.LINKTYPE)) {
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

	/**
	 * Get a list of numbered parameters and their values (e.g. [prop1="www.cancer.gov", prop2="/home", prop3="NCI"])
	 * @param paramList
	 * @param parm
	 * @param replacement
	 * @return
	 */
	private static List<NameValuePair> getNumberedParams(List<NameValuePair> paramList, String parm, String replacement) {
		List<NameValuePair> rtnList = new ArrayList<>();
		for (NameValuePair param : paramList) {
			// Regex: parameter name followed by 1 or more digits, starting with 1-9 only
			if(param.getName().matches("^" + parm + "[1-9]\\d*$")) {
				String rtnName = param.getName().replace(parm, replacement);
				String rtnValue = param.getValue();
				rtnList.add(new BasicNameValuePair(rtnName, rtnValue));
			}
		}
		return rtnList;
	}
	
	/**
	 * Overload for getNumberedParams
	 * Can be used for cases where the parameter name and analytics variable name match 
	 * @param paramList
	 * @param parm
	 * @return
	 */
	private static List<NameValuePair> getNumberedParams(List<NameValuePair> paramList, String parm) {
		return getNumberedParams(paramList, parm, parm);
	}	
		
	/**************** Checks for given values ****************/

	/**
	 * Utility function to check for a given heirarchy and value
	 * @param num
	 * @param val
	 * @return
	 */
	public boolean hasHier(int num, String val) {
		String blob = this.getEvars().toString();
		if(blob.toLowerCase().contains("hier" + Integer.toString(num) + "=" + val.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Utility function to check for a user-specified analytics variable and value
	 * @param name
	 * @param value
	 * @return bool
	 */
	public boolean hasVariable(String name, String value) {
		// TODO: fill this out
		return false;
	}		

}