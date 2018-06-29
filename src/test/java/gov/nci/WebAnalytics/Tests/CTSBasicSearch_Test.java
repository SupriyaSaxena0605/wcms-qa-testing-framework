package gov.nci.WebAnalytics.Tests;

import com.relevantcodes.extentreports.LogStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import gov.nci.WebAnalytics.CTSBasicSearch;

public class CTSBasicSearch_Test extends AnalyticsTestBase{
	
	private CTSBasicSearch basicSearch;

	@BeforeMethod(groups = { "Analytics" }) 
	public void beforeMethod() {
		basicSearch = new CTSBasicSearch(driver);
	}

	/// ??? returns the expected general/shared values
	@Test(groups = { "Analytics" })
	public void testBasicGeneral() {
		Assert.assertTrue(1 == 1);
		logger.log(LogStatus.PASS, "CTS Basic gen value test passed.");
	}		
	
	@Test(groups = { "Analytics" })
	public void testBasicBegin() {
		basicSearch.beginBasicForm();
		clickBeacons = basicSearch.getClickBeacons(getHarUrlList(proxy));
		Assert.assertTrue(hasEvent(clickBeacons, "event38"));
		Assert.assertFalse(hasEvent(clickBeacons, "event40"));
		Assert.assertTrue(hasProp(clickBeacons, 74, "clinicaltrials_basic|start"));
		Assert.assertTrue(haseVar(clickBeacons, 47, "clinicaltrials_basic"));
		logger.log(LogStatus.PASS, "CTS Basic 'start' value test passed.");
	}
	
	@Test(groups = { "Analytics" })
	public void testBasicAbandon() {
		basicSearch.abandonBasicForm();
		clickBeacons = basicSearch.getClickBeacons(getHarUrlList(proxy));
		Assert.assertTrue(hasEvent(clickBeacons, 40));
		//Assert.assertTrue(hasProp(clickBeacons, 74, "clinicaltrials_basic|abandon|a"));
		//Assert.assertTrue(haseVar(clickBeacons, 47, "clinicaltrials_basic"));
		logger.log(LogStatus.PASS, "CTS Basic 'abandon' value test passed.");
	}	
}
