package gov.nci.webanalyticstests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.testng.Assert;

import gov.nci.Utilities.ExcelManager;
import gov.nci.webanalytics.AnalyticsMetaData;
import gov.nci.webanalytics.Beacon;

public class AnalyticsTestLoadBase extends AnalyticsTestBase {

	private final String REGEX_BROWSER_SIZE = "^(Extra wide|Desktop|Tablet|Mobile)$";
	private final String REGEX_MMDDYY = "^(0[1-9]|1[012])[- \\/.](0[1-9]|[12][0-9]|3[01])[- \\/.](19|20)\\d\\d$"; // 01/01/2018
	private final String REGEX_PAGELOAD_TIME = "^\\d{1,4}$";
	private final String REGEX_TIME_PARTING = "^\\d{1,2}:\\d{2} (AM|PM)\\|[a-zA-z]+day$"; // 2:35 PM|Tuesday
	private final String REGEX_TIMESTAMP_PIPE = "^\\d{4}\\|\\d{1,2}\\|\\d{1,2}\\|\\d{1,2}$"; // 05/06/2018

	/**
	 * Get the 'load' beacon for testing.
	 * 
	 * @return Beacon
	 */
	protected Beacon getBeacon(int... index) {
		List<String> harList = getHarUrlList(proxy);
		List<Beacon> beaconList = getBeaconList(harList);
		Beacon beacon = beaconList.get(beaconList.size() - 1);
		System.out.println("Load beacon to test: ");
		System.out.println(beacon.url + "\n");
		return beacon;
	}

	/**
	 * Create a list of load Beacon objects. loadBeacons -> a list of analytics
	 * request URLs fired off by an analytics load event, ie s.tl()
	 * 
	 * @param urlList
	 */
	protected List<Beacon> getBeaconList(List<String> urlList) {

		List<Beacon> loadBeacons = new ArrayList<Beacon>();
		int clickBeacons = 0;

		// For each server URL, check if it is an analytics click
		// or load event, then add it to the correct list
		for (String url : urlList) {
			// Populate the beacon lists
			Beacon beacon = new Beacon(url);
			if (!beacon.isClickTypeEvent()) {
				loadBeacons.add(beacon);
			} else {
				++clickBeacons;
			}
		}

		// Debug analytics beacon counts
		System.out.println("Total analytics requests: " + urlList.size() + " (load: " + loadBeacons.size() + ", click: "
				+ clickBeacons + ")");

		return loadBeacons;
	}

	/**
	 * Shared Assert() calls for all pageLoad tracking beacons.
	 * 
	 * @param beacon
	 * @param analyticsMetaData
	 * @param path
	 */
	protected void doCommonLoadAssertions(Beacon beacon, AnalyticsMetaData analyticsMetaData, String path) {

		String currUrl = driver.getCurrentUrl();
		String currUrlMax = currUrl.substring(0, Math.min(currUrl.length(), 100));

		// Common Suites
		Assert.assertTrue(beacon.hasSuite("nciglobal", currUrl), "Missing Global Suite");

		// Common events
		Assert.assertTrue(beacon.hasEvent(1), "Missing event");
		Assert.assertTrue(beacon.hasEvent(47), "Missing event");

		// Props
		Assert.assertEquals(beacon.props.get(1), currUrlMax);
		Assert.assertEquals(beacon.props.get(3), path.toLowerCase());
		Assert.assertEquals(beacon.props.get(6), analyticsMetaData.getMetaTitle());
		Assert.assertEquals(beacon.props.get(8), analyticsMetaData.getLanguageName());
		Assert.assertEquals(beacon.props.get(10), analyticsMetaData.getPageTitle());
		Assert.assertTrue(beacon.props.get(25).matches(REGEX_MMDDYY), "Invalid date");
		Assert.assertTrue(beacon.props.get(26).matches(REGEX_TIMESTAMP_PIPE), "Invalid timestamp");
		Assert.assertTrue(beacon.props.get(29).matches(REGEX_TIME_PARTING), "Invalid time parting value");
		Assert.assertEquals(beacon.props.get(42), "Normal");
		Assert.assertEquals(beacon.props.get(44), analyticsMetaData.getMetaIsPartOf());
		Assert.assertTrue(beacon.props.get(65).matches(REGEX_PAGELOAD_TIME), "Invalid pageload time value");

		// Evars
		Assert.assertTrue(beacon.eVars.get(1).contains("www.cancer.gov"), "Pagename error");
		Assert.assertEquals(beacon.eVars.get(2), analyticsMetaData.getLanguageName());
		Assert.assertTrue(beacon.eVars.get(5).matches(REGEX_BROWSER_SIZE), "Invalid browser width");
		Assert.assertEquals(beacon.eVars.get(44), analyticsMetaData.getMetaIsPartOf());

		// HIer
		Assert.assertEquals(beacon.hiers.get(1), buildHier1(currUrl));

	}

	/**
	 * Get an iterator data object with path and content type Strings.
	 * 
	 * @param testDataFilePath
	 * @param sheetName
	 * @return Iterator<Object[]> myObjects
	 */
	public Iterator<Object[]> getPathContentTypeData(String testDataFilePath, String sheetName) {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);
		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();
		for (int rowNum = 2; rowNum <= excelReader.getRowCount(sheetName); rowNum++) {
			String path = excelReader.getCellData(sheetName, "Path", rowNum);
			String contentType = excelReader.getCellData(sheetName, "ContentType", rowNum);
			Object ob[] = { path, contentType };
			myObjects.add(ob);
		}
		return myObjects.iterator();
	}

	/**
	 * Get an iterator data object with path and content type Strings, filtered by a
	 * given value and column.
	 * 
	 * @param testDataFilePath
	 * @param sheetName
	 * @param filterColumn
	 * @param myFilter
	 * @return
	 */
	public Iterator<Object[]> getFilteredPathContentTypeData(String testDataFilePath, String sheetName,
			String filterColumn, String myFilter) {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);
		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();
		for (int rowNum = 2; rowNum <= excelReader.getRowCount(sheetName); rowNum++) {
			String path = excelReader.getCellData(sheetName, "Path", rowNum);
			String contentType = excelReader.getCellData(sheetName, "ContentType", rowNum);
			String filter = excelReader.getCellData(sheetName, filterColumn, rowNum);
			if (filter.equalsIgnoreCase(myFilter)) {
				Object ob[] = { path, contentType };
				myObjects.add(ob);
			}
		}
		return myObjects.iterator();
	}

	/**
	 * Recreate the "Hierarchy 1" variable from a URL.
	 * 
	 * @param myUrl
	 * @return formatted hier1 string
	 */
	private String buildHier1(String myUrl) {
		try {
			URL url = new URL(myUrl);
			String hier = url.getHost() + url.getPath();
			if (hier.charAt(hier.length() - 1) == '/') {
				hier = hier.substring(0, hier.length() - 1);
			}
			return hier.replaceAll("/", "|");
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			return "";
		}
	}
	

	/**
	 * Common method to log any non-assert exceptions in test methods.
	 * 
	 * @param Object representing the test class
	 * @param Exception 
	 */
	protected void handleTestErrors(Object obj, Exception ex) {
		String testMethod = obj.getClass().getEnclosingMethod().getName();
		String msg = ex.toString();
		msg = (msg.length() > 255) ? msg.substring(0, 255) : msg;

		System.out.println("Exception: " + msg + "\n");
		Assert.fail("Load event exception in " + testMethod + "(): " + msg);
	}

}
