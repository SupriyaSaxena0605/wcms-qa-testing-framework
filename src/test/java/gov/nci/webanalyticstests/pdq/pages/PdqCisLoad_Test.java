package gov.nci.webanalyticstests.pdq.pages;

import java.util.Iterator;

import com.relevantcodes.extentreports.LogStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;

import gov.nci.webanalytics.AnalyticsMetaData;
import gov.nci.webanalytics.Beacon;
import gov.nci.webanalyticstests.AnalyticsTestLoadBase;

public class PdqCisLoad_Test extends AnalyticsTestLoadBase {

	private final String TESTDATA_SHEET_NAME = "PDQCisPage";

	private AnalyticsMetaData analyticsMetaData;
	private String testDataFilePath;

	// ==================== Setup methods ==================== //

	@BeforeClass(groups = { "Analytics" })
	public void setupClass() {
		testDataFilePath = config.getProperty("AnalyticsPDQData");
	}

	// ==================== Test methods ==================== //

	/// Test PDQ Cancer Info Summary Page load event
	@Test(dataProvider = "PDQPageLoad", groups = { "Analytics" })
	public void testPdqCisPageLoad(String path, String contentType) {
		System.out.println("Test PDQ Cancer Info Summary Page load event (" + path + "):");
		driver.get(config.goHome() + path);
		driver.navigate().refresh();

		try {
			analyticsMetaData = new AnalyticsMetaData(driver);
			Beacon beacon = getBeacon();

			String[] pathNoId = path.split("#");
			doCommonLoadAssertions(beacon, analyticsMetaData, pathNoId[0]);
			logger.log(LogStatus.PASS, "Test PDQ Cancer Info Summary Page load event (" + path + ") passed.");
		} catch (Exception e) {
			String currMethod = new Object() {
			}.getClass().getEnclosingMethod().getName();
			Assert.fail("Error loading page in " + currMethod + "()");
		}
	}

	// ==================== Data providers ==================== //

	@DataProvider(name = "PDQPageLoad")
	public Iterator<Object[]> getPDQPageLoadData() {
		return getPathContentTypeData(testDataFilePath, TESTDATA_SHEET_NAME);
	}

}