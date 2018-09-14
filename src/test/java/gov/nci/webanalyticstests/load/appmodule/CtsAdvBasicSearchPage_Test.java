package gov.nci.webanalyticstests.load.appmodule;

import com.relevantcodes.extentreports.LogStatus;
import org.testng.annotations.Test;
import org.testng.Assert;

import gov.nci.webanalytics.AnalyticsPageLoad;
import gov.nci.webanalytics.Beacon;
import gov.nci.webanalyticstests.load.AnalyticsTestLoadBase;

public class CtsAdvBasicSearchPage_Test extends AnalyticsTestLoadBase {

	/**
	 * This test class covers Clinical Trial Basic and Advanced Search pages
	 */
	
	private final String ADV_PATH = "/about-cancer/treatment/clinical-trials/advanced-search";
	private final String BASIC_PATH = "/about-cancer/treatment/clinical-trials/search";
	private final String ADV_CONTENT_TYPE = "Clinical Trials: Advanced";
	private final String BASIC_CONTENT_TYPE = "Clinical Trials: Basic";

	private AnalyticsPageLoad analyticsPageLoad;
	private Beacon beacon;

	@Test(groups = { "Analytics" })
	public void testCTSAdvancedSearchPageLoad() {
		try {
			driver.get(config.goHome() + ADV_PATH);
			analyticsPageLoad = new AnalyticsPageLoad(driver);
			System.out.println(ADV_CONTENT_TYPE + " load event (" + analyticsPageLoad.getLanguageName() + "):");
			beacon = getBeacon();
			
			doCommonLoadAssertions(beacon, analyticsPageLoad, ADV_PATH);
			Assert.assertEquals(beacon.props.get(62), ADV_CONTENT_TYPE);
			Assert.assertEquals(beacon.eVars.get(62), ADV_CONTENT_TYPE);
			logger.log(LogStatus.PASS, ADV_CONTENT_TYPE + " load values are correct.");
		}
		catch (Exception e) {
			Assert.fail("Error loading " + ADV_CONTENT_TYPE);
			e.printStackTrace();
		}
	}
	
	@Test(groups = { "Analytics" })
	public void testCTSBasicSearchPageLoad() {
		try {
			driver.get(config.goHome() + BASIC_PATH);
			analyticsPageLoad = new AnalyticsPageLoad(driver);
			System.out.println(BASIC_CONTENT_TYPE + " load event (" + analyticsPageLoad.getLanguageName() + "):");
			beacon = getBeacon();
			
			doCommonLoadAssertions(beacon, analyticsPageLoad, BASIC_PATH);
			Assert.assertEquals(beacon.props.get(62), BASIC_CONTENT_TYPE);
			Assert.assertEquals(beacon.eVars.get(62), BASIC_CONTENT_TYPE);
			logger.log(LogStatus.PASS, BASIC_CONTENT_TYPE + " load values are correct.");
		}
		catch (Exception e) {
			Assert.fail("Error loading " + BASIC_CONTENT_TYPE);
			e.printStackTrace();
		}
	}
	
}