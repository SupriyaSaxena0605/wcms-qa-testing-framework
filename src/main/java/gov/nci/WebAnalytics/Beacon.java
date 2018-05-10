package gov.nci.WebAnalytics;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.openqa.selenium.WebDriver;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;


public class Beacon {
	
	// Constants
	public static final String S_CODE_NAME = "s_code.js";
	public static final String S_ACCOUNT = "s_account";
	public static final String NCI_FUNCTIONS_NAME = "NCIAnalytics";
	public static final String PAGE_NAME = "www.cancer.gov";
	public static final String TRACKING_SERVER = "nci.122.2o7.net";

	// Driver object
	public WebDriver driver;	

	// Beacon properties
	public URI uri;
	public String[] suites;	
	public List<NameValuePair> params;
	public String channel;
	public String[] events;
	public List<NameValuePair> props; 
	public List<NameValuePair> eVars; 
	public List<NameValuePair> hiers; 
	public String linkType;
	public String linkName;
	public String linkUrl;	
	
	/**
	 * No-arg constructor
	 */
	public Beacon() {
		uri = null;
		suites = new String[0];		
		params = new ArrayList<>();
		channel = "";
		events = new String[0];
		props = new ArrayList<>();
		eVars = new ArrayList<>();
		hiers = new ArrayList<>();
		linkType = "";
		linkName = "";
		linkUrl = "";
	}
	
	/**
	 * Constructor
	 * @param beaconUrl
	 * @throws MalformedURLException
	 */
	public Beacon(String beaconUrl) throws MalformedURLException {
		uri = URI.create(beaconUrl);
		suites = getSuites(uri);
		params = buildParamsList(uri);
		channel = getChannel(params);
		events = getEvents(params);
		props = getProps(params);
		eVars = getEvars(params);
		hiers = getHiers(params);
		linkType = getLinkType(params);
		linkName = getLinkName(params);
		linkUrl = getLinkUrl(params);
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
	protected List<NameValuePair> buildParamsList(URI uri) {
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
	 * Get "Link Type" value (pe)(
	 * @param parms
	 * @return
	 */
	public String getLinkType(List<NameValuePair> parms) {
		String rtn = "";		
		for (NameValuePair param : parms) {
			if (param.getName().toLowerCase().equals("pe")) {
				rtn = param.getValue();
				break;
			}
		}
		return rtn;
	}	

	/**
	 * Get "Link Name" value (pev2)(
	 * @param parms
	 * @return
	 */	
	public String getLinkName(List<NameValuePair> parms) {
		String rtn = "";		
		for (NameValuePair param : parms) {
			if (param.getName().toLowerCase().equals("pev2")) {
				rtn = param.getValue();
				break;
			}
		}
		return rtn;
	}	

	/**
	 * Get "Link URL" value (pev1)(
	 * @param parms
	 * @return
	 */		
	public String getLinkUrl(List<NameValuePair> parms) {
		String rtn = "";		
		for (NameValuePair param : parms) {
			if (param.getName().toLowerCase().equals("pev1")) {
				rtn = param.getValue();
				break;
			}
		}
		return rtn;
	}	
	
	
	
	/**
	 * Configure BrowserMob Proxy for Selenium.<br>
	 * Modified from https://github.com/lightbody/browsermob-proxy#using-with-selenium
	 * @throws RuntimeException
	 */
	// TODO: clean this up - also should this have a return value? 
	// TODO: set up filters 
	// TODO: remove duplicates
	public static void setHar(BrowserMobProxy proxy, List<String> harList) throws RuntimeException {		

		// A HAR (HTTP Archive) is a file format that can be used by HTTP monitoring tools to export collected data. 
		// BrowserMob Proxy allows us to manipulate HTTP requests and responses, capture HTTP content, 
	    // and export performance data as a HAR file object.
	    Har har = proxy.getHar();
	    
	    List<HarEntry> entries = har.getLog().getEntries();
    	System.out.println("Total HAR entries: " + entries.size());
    	
	    for (HarEntry entry : entries) {
	    	if(entry.getRequest().getUrl().contains(TRACKING_SERVER))
			{
	    		String result = entry.getRequest().getUrl();
	    		try {
					//result = URLDecoder.decode(result, "UTF-8");
					if(result.contains("pageName=" + PAGE_NAME)) {
						harList.add(result);
					}
				} catch (Exception e) {
					result = "bleah";
				} 
				//System.out.println(result);
			}
	    }  
	    
		System.out.println("BMP proxy setup done");
	}
	
	
}