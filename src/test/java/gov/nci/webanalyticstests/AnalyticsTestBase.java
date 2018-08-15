package gov.nci.webanalyticstests;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import gov.nci.Utilities.BrowserManager;
import gov.nci.Utilities.ConfigReader;
import gov.nci.WebAnalytics.AnalyticsRequest;

public class AnalyticsTestBase {

	/**
	* TODO: Set default URLs at test level, not base
	* 	- URL may need to be used as a param
	* TODO: Move has/is methods back into AnalyticsRequest 
	*  - Handle null exceptions in has() methods
	*  - Create 'catch-all' Contains() method
	* TODO: Clean up setters / getters
	* TODO: Refactor setBeacon() methods
	* TODO: Create timer
	* TODO: Move "Logger Path" output up one level
	**/	
	public static WebDriver driver;
    public static BrowserMobProxy proxy;
	protected static ExtentReports report;
	protected static ExtentTest logger;
	protected ConfigReader config = new ConfigReader();	
	
	protected List<String> harUrlList;	
	protected List<AnalyticsRequest> loadBeacons;
	protected List<AnalyticsRequest> clickBeacons;
	protected AnalyticsRequest beacon;
	
	/**************************************
	 * Section: TextNG Befores & Afters *
	 **************************************/
	
	/**
	* Configuration information for a TestNG class (http://testng.org/doc/documentation-main.html): 
	* @BeforeSuite: The annotated method will be run before all tests in this suite have run. 
	* @AfterSuite: The annotated method will be run after all tests in this suite have run. 
	* @BeforeTest: The annotated method will be run before any test method belonging to the classes inside the <test> tag is run. 
	* @AfterTest: The annotated method will be run after all the test methods belonging to the classes inside the <test> tag have run. 
	* @BeforeGroups: The list of groups that this configuration method will run before. 
	* 				 This method is guaranteed to run shortly before the first test method that belongs to any of these groups is invoked. 
	* @AfterGroups: The list of groups that this configuration method will run after. 
	* 				This method is guaranteed to run shortly after the last test method that belongs to any of these groups is invoked. 
	* @BeforeClass: The annotated method will be run before the first test method in the current class is invoked. 
	* @AfterClass: The annotated method will be run after all the test methods in the current class have been run. 
	* @BeforeMethod: The annotated method will be run before each test method. 
	* @AfterMethod: The annotated method will be run after each test method.
	**/
	
	@BeforeTest(groups = { "Analytics" })
	@Parameters({ "browser" })
	public void beforeTest(String browser) throws MalformedURLException {
		// Start the BrowserMob proxy on the site homepage
		String initUrl = config.goHome();
		System.out.println("=== Starting BrowserMobProxy ===");
		this.initializeProxy(initUrl);
		
		// Initialize driver and open browser
		System.out.println("=== Starting Driver ===");
		driver = BrowserManager.startProxyBrowser(browser, initUrl, proxy);
    	System.out.println("Requests to " + AnalyticsRequest.TRACKING_SERVER + " will be tested.");
		System.out.println("Analytics test group setup done.\r\nStarting from " + initUrl);
	}
	
	@BeforeClass(groups = { "Analytics" })
	public void beforeClass() {
		String testClass = this.getClass().getSimpleName();
		String fileName = new SimpleDateFormat("yyyy-MM-dd HH-mm-SS").format(new Date());
		String extentReportPath = config.getExtentReportPath();
		
		System.out.println(testClass);
		System.out.println("Logger Path:" + extentReportPath + "\n");
		report = new ExtentReports(extentReportPath + config.getProperty("Environment") + "-" + fileName + ".html");
		report.addSystemInfo("Environment", config.getProperty("Environment"));
		logger = report.startTest(testClass);
	}
	
	@BeforeMethod(groups = { "Analytics" })
	public void beforeMethod() throws RuntimeException {
		// Reset our browser to full screen before each method
		driver.manage().window().maximize();
	}

	@AfterMethod(groups = { "Analytics" })
	public void afterMethod(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			logger.log(LogStatus.FAIL, "Fail => " + result.getName());
		}
		else if (result.getStatus() == ITestResult.SKIP) {
			logger.log(LogStatus.SKIP, "Skipped => " + result.getName());
		}
		else {
			logger.log(LogStatus.PASS, "Pass => "+ result.getName());
		}
	}
	
	@AfterClass(groups = { "Analytics" })
	public void afterClass() {
		report.endTest(logger);
		report.flush();
	}
	
	@AfterTest(groups = { "Analytics"})
	public void afterTest() {
		System.out.println("=== Quitting Driver ===");
		driver.quit();
		System.out.println("=== Stopping BrowserMobProxy ===");
		proxy.stop();
	}
	
	/******************************************************
	 * Section: Initialize BMP and request beacon objects *
	 ******************************************************/
	
	/**
	 * Start and configure BrowserMob Proxy for Selenium.<br/>
	 * Modified from https://github.com/lightbody/browsermob-proxy#using-with-selenium
	 * @throws RuntimeException
	 */
	protected void initializeProxy(String url) throws RuntimeException {

		// New BrowserMobProxy instance - this is needed to create the HAR (HTTP archive) object
		proxy = new BrowserMobProxyServer();
	    proxy.start(0);

	    // Selenium proxy object, capabilities, and WebDriver instantiation are handled in BrowserManager:startProxyBrowser()
	    // Enable more detailed HAR capture, if desired (see CaptureType for the complete list)
	    proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

	    // Create a new HAR with a label matching the hostname
	    proxy.newHar(url);	    
		System.out.println("== Started BrowserMobProxy successfully ==");
	}
	
	/**
	 * Build the list of HAR (HTTP archive) request URLs 
	 * TODO: refactor into har Url list and tracking server list
	 * TODO: only print tested URL
	 * Modified from https://github.com/lightbody/browsermob-proxy#using-with-selenium
	 * @throws RuntimeException
	 * @throws IllegalArgumentException
	 */
	protected void setHarUrlList(BrowserMobProxy proxy) throws RuntimeException, IllegalArgumentException {		

		// A HAR (HTTP Archive) is a file format that can be used by HTTP monitoring tools to export collected data. 
		// BrowserMob Proxy allows us to manipulate HTTP requests and responses, capture HTTP content, 
	    // and export performance data as a HAR file object.
	    Har har = proxy.getHar();
	    List<HarEntry> entries = har.getLog().getEntries();
    	
    	// Reset HAR URL list
    	harUrlList = new ArrayList<String>();

    	// Build a list of requests to the analytics tracking server from the HAR
	    for (HarEntry entry : entries) {
	    	String result = entry.getRequest().getUrl();
	    	if(result.contains(AnalyticsRequest.TRACKING_SERVER))
	    	{
	    		harUrlList.add(result);
	    	}
	    }
	    
	    // Debug size of HAR list
    	System.out.println("Total HAR entries: " + entries.size());
		
		// The HAR list has been created; clear the log for next pass
		har.getLog().getEntries().clear();		
		// For further reading, see https://en.wiktionary.org/wiki/hardy_har_har
	}
	
	/**
	 * Set create lists of AnalyticsRequest objects for load and click events
	 * loadBeacons -> a list of analytics request URLs fired off by an analytics load event, ie s.tl() 
	 * clickBeacons -> a list of analytics request URLs fired off by an analytics load event, ie s.t() 			 * 
	 * @param urlList
	 */
	protected void setBeaconLists(List<String> urlList) {
		
		// Reset beacon lists
		loadBeacons = new ArrayList<AnalyticsRequest>();
		clickBeacons = new ArrayList<AnalyticsRequest>();		
		
		// For each server URL, check if it is an analytics click
		// or load event, then add it to the correct list
		for(String url : urlList)
		{  
			AnalyticsRequest request = new AnalyticsRequest(url);
			request.buildParamsList();
			
			// Populate the beacon lists
			if(request.isClickTypeEvent()) {
				clickBeacons.add(request);
			}
			else {
				loadBeacons.add(request);
			}
		}

	    // Debug analytics beacons 	
		System.out.println(urlList.size() + " total analytics requests ("
			+ loadBeacons.size() + " loads, " 
			+ clickBeacons.size() + " clicks)"
		);
	}
	
	/**
	 * Set the global loadBeacons and beacon variables
	 */
	protected void setClickBeacon() {
		setHarUrlList(proxy);
		setBeaconLists(harUrlList);
		beacon = getLastReq(clickBeacons);
		System.out.println("Click beacon to test: ");
		System.out.println(beacon.getUrl() + "\n");
	}
	
	/**
	 * Set the global clickBeacons and beacon variables
	 */
	protected void setLoadBeacon() {
		setHarUrlList(proxy);
		setBeaconLists(harUrlList);
		beacon = getLastReq(loadBeacons);
		System.out.println("Load beacon to test: ");
		System.out.println(beacon.getUrl() + "\n");
	}
	
	/**
	 * Utility function to get the last element in a list of AnalyticsRequest objects
	 * @param requests
	 * @return the last AnalyticsRequest object
	 */
	private static AnalyticsRequest getLastReq(List<AnalyticsRequest> requests) {
		int index = requests.size() - 1;
		AnalyticsRequest request = (index >= 0) ? requests.get(index) : null;
		return request;
	}
	
	
	/*********************************
	 * Section - Common methods used by tests *
	 *********************************/
	/***
	 * TODO: move these into request class
	 * TODO: replace "contains' with "matches" where possible
	 * TODO: add shared click / load tests here
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
	 * Utility function to check for a given channel name
	 * @return
	 */
	public boolean hasChannel() {
		// TODO: fill this out
		return false;
	}
	
	/**
	 * Utility function to check for a link name value within a click beacon.
	 * @param name
	 * @return
	 */
	public boolean hasLinkName(String name) {
		if(beacon.getLinkName().equalsIgnoreCase(name)) {
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
		for(String event : beacon.getEvents()) {
			if(evt.equalsIgnoreCase("event47")) {
				if(event.matches("^event47=\\d+")) {
					return true;
				}
			} 
			else if(event.equalsIgnoreCase(evt)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Utility function to check for a given prop and value
	 * @param num
	 * @param val
	 * @return
	 */
	public boolean hasProp(int num, String val) {
		String blob = beacon.getProps().toString();
		if(blob.toLowerCase().contains("prop" + Integer.toString(num) + "=" + val.toLowerCase())) {
			return true;
		}
		return false;
	}	
	
	/**
	 * Utility function to check for a given eVar and value
	 * @param num
	 * @param val
	 * @return
	 */
	public boolean haseVar(int num, String val) {
		String blob = beacon.getEvars().toString();
		if(blob.toLowerCase().contains("evar" + Integer.toString(num) + "=" + val.toLowerCase())) {
			return true;
		}
		return false;
	}

	/**
	 * Utility function overload to check for a given eVar 
	 * @param num
	 * @return
	 */
	public boolean haseVar(int num) {
		return haseVar(num, "");
	}
	
}