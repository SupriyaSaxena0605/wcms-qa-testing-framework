package gov.nci.webanalyticstests.load;

import java.util.Iterator;

import com.relevantcodes.extentreports.LogStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Assert;

import gov.nci.Utilities.ConfigReader;
import gov.nci.webanalytics.AnalyticsPageLoad;
import gov.nci.webanalytics.Beacon;

public class BlogSeriesPage_Test extends AnalyticsTestLoadBase {

	/**
	 * The following page / content types are covered by this test class:
	 * - Blog Series Page (English and Spanish)
	 */
	
	private AnalyticsPageLoad analyticsPageLoad;
	private Beacon beacon;	
	private String testDataFilePath;
	private final String TESTDATA_SHEET_NAME = "BlogSeriesPage";
	
	@BeforeClass(groups = { "Analytics" }) 
	@Parameters({ "environment" })
	public void setup(String environment) {
		config = new ConfigReader(environment);
		testDataFilePath = config.getProperty("AnalyticsPageLoadData");
	}
	
	/// BlogSeries page loads return expected values
	@Test(dataProvider = "BlogSeriesPageLoad", groups = { "Analytics" })
	public void testBlogSeriesPageLoad(String path, String contentType) {
		try {
			driver.get(config.goHome() + path);
			analyticsPageLoad = new AnalyticsPageLoad(driver);
			System.out.println(contentType + " load event (" + analyticsPageLoad.getLanguageName() + "):");
			beacon = getBeacon();
			DoCommonLoadAssertions(beacon, analyticsPageLoad, path);
			logger.log(LogStatus.PASS, contentType + " load values are correct.");
		}
		catch (Exception e) {
			Assert.fail("Error loading " + contentType);
			e.printStackTrace();
		}
	}

	@DataProvider(name = "BlogSeriesPageLoad")
	public Iterator<Object[]> getBlogSeriesPageLoadData() {
		return getPathContentTypeData(testDataFilePath, TESTDATA_SHEET_NAME);
	}
	
}