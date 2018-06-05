package gov.nci.WebAnalytics;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

public class PageLoad extends AnalyticsBase {

	// Local driver object
	public WebDriver driver;
	
	// Constructor to initialize the page object	
	public PageLoad(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		System.out.println("PageLoad object PageFactory initialized");
	}
	
	/**
	 * All the proxy browser 'actions' go in here. These are not tests, but things that we do 
	 * to fire off analytics events. These actions will populate our list of har objects, which will
	 * then be tested.
	 * @throws RuntimeException
	 */
	private void loadPageTypes() {
		driver.navigate().to(WaNav.homePage);		
		driver.navigate().to(WaNav.landingPage);
		driver.navigate().to(WaNav.cthpPatient);
		driver.navigate().to(WaNav.cthpHP);
		driver.navigate().to(WaNav.appModulePage);
		driver.navigate().to(WaNav.blogSeriesPage);
	}

	public void goHomeAndBack() {
		// Home page
		driver.navigate().to(WaNav.homePage);
		driver.navigate().to(WaNav.spanishPage);
		driver.navigate().back();
		driver.navigate().refresh();
	}	
	
	/// Click around pages
	public void doPageLoadActions() throws RuntimeException {
		System.out.println("Begin PageLoad actions");
		loadPageTypes();
		goHomeAndBack();		
		System.out.println("End PageLoad actions");
	}
	
}
