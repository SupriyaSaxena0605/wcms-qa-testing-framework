package gov.nci.testcases;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import gov.nci.Utilities.BrowserManager;
import gov.nci.Utilities.ExcelManager;
import gov.nci.clinicalTrial.pages.BasicSearch;
import gov.nci.clinicalTrial.common.ApiReference;
import gov.nci.clinicalTrial.common.Delighters;
import gov.nci.commonobjects.Banner;
import gov.nci.commonobjects.BreadCrumb;
import com.relevantcodes.extentreports.LogStatus;

public class BasicSearch_Test extends BaseClass {

	// WebDriver driver;
	public static final String TESTDATA_SHEET_NAME = "BasicSearch";
	public static final String API_REFERENCE_H3 = "The Clinical Trials API: Use our data to power your own clinical trial search";

	BasicSearch basicSearch;
	Delighters delighter;
	BreadCrumb crumb;
	Banner banner;
	ApiReference api;
	String resultPageUrl;
	String advSearchPageUrl;
	String testDataFilePath;


	@BeforeClass(groups = { "Smoke", "current" })
	@Parameters({ "browser" })
	public void setup(String browser) {
		logger = report.startTest(this.getClass().getSimpleName());

		pageURL = config.getPageURL("BasicClinicalTrialSearchURL");
		System.out.println("PageURL: " + pageURL);
		driver = BrowserManager.startBrowser(browser, pageURL);

		basicSearch = new BasicSearch(driver);
		delighter = new Delighters(driver);
		crumb = new BreadCrumb(driver);
		banner = new Banner(driver);
		api = new ApiReference(driver);
		System.out.println("Basic search setup done");
		testDataFilePath = config.getProperty("TestData");
	}

	// Verifying the UI components of Basic Search

	/**
	 * Verifies presence, visibility, and content of the search header text
	 */
	@Test(groups = { "Smoke" })
	public void uiVerificationHeaderText() {

		WebElement headerTextElement = basicSearch.getHeaderText();
		Assert.assertTrue(headerTextElement.isDisplayed(), "Basic CTS header text not displayed");
		Assert.assertTrue(headerTextElement.getText().contains("Steps to Find a Clinical Trial"), "header text mismatch");
		System.out.println("Basic CTS header text displayed is " + headerTextElement.getText());
	}

	/**
	 * Verifies presence, visibility, and content of the search help icon
	 */
	@Test(groups = { "Smoke" })
	public void uiVerificationSearchTip () {

		WebElement searchTipElememt = basicSearch.getSearchTipElement();
		Assert.assertTrue(searchTipElememt.isDisplayed(), "Search Tip not displayed");
		Assert.assertTrue(searchTipElememt.getText().contains("Search Tip: For more search options, use our advanced search"), "Search Tip text mismatched");
		System.out.println("Search Tip is displayed: " + searchTipElememt.getText());
	}

	/**
	 * Verifies presence, visibility, and content of the cancer type input field.
	 */
	@Test(groups = { "Smoke" })
	public void uiVerificationCancerType () {

		WebElement cancerTypeLabel = basicSearch.getCancerTypeLabel();
		Assert.assertTrue(cancerTypeLabel.isDisplayed(), "Cancer Type label not displayed");
		System.out.println("Cancer Type label is displayed: " + cancerTypeLabel.getText());

		WebElement cancerTypeHelpLink = basicSearch.getCancerTypeHelpLink();
		Assert.assertTrue(cancerTypeHelpLink.isDisplayed(), "Cancer Type help icon not displayed");

		WebElement cancerTypeMessageElement = basicSearch.getCancerTypeMessageElement();
		Assert.assertTrue(cancerTypeMessageElement.isDisplayed(), "Cancer Type Placeholder message not displayed");
		Assert.assertTrue(cancerTypeMessageElement.getAttribute("placeholder").contains("Start typing to select a cancer type"), "Cancer type message text mismatch");
		System.out.println("Cancer Type placeholder message is displayed: " + cancerTypeMessageElement.getText());
	}

	/**
	 * Verifies presence, visibility, and content of the age input field.
	 */
	@Test(groups = { "Smoke" })
	public void uiVerificationAgeField() {

		WebElement ageLabel = basicSearch.getAgeLabel();
		Assert.assertTrue(ageLabel.isDisplayed(), "Age label not displayed");
		System.out.println("Age label is displayed: " + ageLabel.getText());

		WebElement ageHelpLink = basicSearch.getAgeHelpLink();
		Assert.assertTrue(ageHelpLink.isDisplayed(), "Age help icon is displayed");

		WebElement ageHelpText = basicSearch.getTextAgeElement();
		Assert.assertTrue(ageHelpText.isDisplayed(), "Age help text not displayed");
		Assert.assertTrue(ageHelpText.getText().contains("Your age helps determine which trials are right for you."), "Age text mismatch");
		System.out.println("Age help text is displayed: " + ageHelpText.getText());
	}

	/**
	 * Verifies presence, visibility, and content of the ZIP code input field.
	 */
	@Test(groups = { "Smoke" })
	public void uiVerificationZipCodeField () {

		WebElement zipCodeLabel = basicSearch.getZipCodeLabel();
		Assert.assertTrue(zipCodeLabel.isDisplayed(), "ZipCode label not displayed");
		System.out.println("ZipCode label is displayed: " + zipCodeLabel.getText());

		WebElement zipCodeHelpLink = basicSearch.getZipCodeHelpLink();
		Assert.assertTrue(zipCodeHelpLink.isDisplayed(), "ZipCode help icon not displayed");

		WebElement zipCodeTextElement = basicSearch.getZipCodeTextElement();
		Assert.assertTrue(zipCodeTextElement.isDisplayed(), "ZipCode help text not displayed");
		Assert.assertTrue(zipCodeTextElement.getText().contains("Show trials near this U.S. ZIP code."), "Zip code help text mismatch");
		System.out.println("ZipCode help text is displayed: " + zipCodeTextElement.getText());
	}

	/**
	 * Verifies presence, visibility, and content of the search button
	 */
	@Test(groups = { "Smoke" })
	public void uiVerificationSearchButton () {

		WebElement searchButton = basicSearch.getSearchButton();
		Assert.assertTrue(searchButton.isDisplayed(), "Find Results button not displayed");
		System.out.println("Find Results button is displayed: " + searchButton.getAttribute("value"));
	}



	@Test(groups = { "Smoke" })
	public void verifyUI() {

		// What does this do?
		basicSearch.clickSteps();
		Assert.assertTrue(driver.getCurrentUrl().contains("search/trial-guide"));
		logger.log(LogStatus.PASS, "Pass => " + "Verify Steps to Find a Clinical Trial link on Basic CTS");
		driver.navigate().back();
	}

	@Test(groups = { "Smoke", "current" })
	public void searchDefault() {
		basicSearch.searchDefault();
		if (driver.findElement(By.name("printButton")).isDisplayed()) {
			System.out.println("All search results page should be displayed");
			resultPageUrl = driver.getCurrentUrl();
			System.out.println("Result page url: " + resultPageUrl);
		}

		// Checking the Page Title
		String pageTitle = driver.getTitle();
		System.out.println("Actual Page Title:" + pageTitle);
		Assert.assertTrue(driver.findElement(By.className("cts-results-label")).getText().contains("all trials"));
		driver.navigate().back();
		System.out.println("Page URL after the default search " + driver.getCurrentUrl());

		logger.log(LogStatus.PASS, "Pass => " + "Verify Default Search on Basic CTS");

	}

	@Test(dataProvider = "CancerType", groups = { "Smoke" })
	public void searchCancerType(String cancerType) throws InterruptedException {
		// String cancerType = "Breast Cancer";

		// Performing the search using Cancer type parameter
		basicSearch.searchCancerType(cancerType);

		if (driver.findElement(By.name("printButton")).isDisplayed()) {
			System.out.println("search results page should be displayed");
			resultPageUrl = driver.getCurrentUrl();
			System.out.println("Result page url: " + resultPageUrl);
		}

		// Checking the Page Title
		String pageTitle = driver.getTitle();
		System.out.println("Actual Page Title:" + pageTitle);
		Assert.assertEquals(pageTitle, "Clinical Trials Search Results - National Cancer Institute");

		// Checking the Search Results form Title
		String resultsTitle = driver.findElement(By.xpath(".//*[@id='main']/article/div[2]/h1/span")).getText();
		System.out.println("Results Title:" + resultsTitle);
		Assert.assertTrue(resultsTitle.contains("Clinical Trials Search Results"));

		// Checking the Results page URL for the parameters passed in order to
		// validate correct search results are displayed
		String cancerTypeWithoutSpace = cancerType.replace(" ", "+");
		System.out.println("New String: " + cancerTypeWithoutSpace);
		Assert.assertTrue(resultPageUrl.contains(cancerTypeWithoutSpace),
				"Keyword not found " + cancerTypeWithoutSpace);
		driver.navigate().back();
		System.out.println("Page URL after the cancer type search " + driver.getCurrentUrl());
		logger.log(LogStatus.PASS, "Pass => " + "Verify Search for Cancer Type on Basic CTS");

	}

	@Test(dataProvider = "Age", groups = { "Smoke" })
	public void searchAge(int age) {
		Object[][] data;
		// Thread.sleep(300);
		basicSearch.searchAge(age);

		if (age < 1 || age > 120) {
			WebElement error_Msg = driver.findElement(By.xpath("//div[@class='error-msg']"));
			error_Msg.getText();
			Assert.assertTrue(error_Msg.getText().contains("Please enter a number between 1 and 120."));
			System.out.println("Error message for age: " + error_Msg.getText());
			logger.log(LogStatus.PASS, "Verify Search by Age on basic CTS. Age = " + age);
		}

		else {
			// Verify page title
			String pageTitle = driver.getTitle();
			System.out.println("basicSearch_searchAge: Basic Search Results Page Title: " + pageTitle);
			Assert.assertTrue(pageTitle.contains("Clinical Trials Search Result"));

			// Verify search criteria in URL
			resultPageUrl = driver.getCurrentUrl();
			System.out.println("basicsearch_Age: Current Page URL: " + resultPageUrl);
			Assert.assertTrue(resultPageUrl.contains("a=" + age));
			System.out.println("basicsearch_Age: Age: a=" + age);

			// Verify that show search criteria table contains the selected
			// search criteria
			data = verifySearchCriteriaTable();

			Assert.assertEquals(data[1][1], String.valueOf(age), "Age not matched");
			System.out.println("data[1][1]==== " + data[1][1]);

			driver.findElement(By.linkText("Start Over")).click();

			System.out.println("Page URL after the age search " + driver.getCurrentUrl());
			logger.log(LogStatus.PASS, "Pass => " + "Verify Search for Age on Basic CTS");
		}
	}

	@Test(dataProvider = "ZipCode", groups = { "Smoke" })
	public void searchZip(String zip) throws InterruptedException {
		Object[][] data;
		Thread.sleep(300);
		basicSearch.searchZip(zip);

		// Verify that show search criteria table contains the selected
		// search criteria
		if (zip.equals("99999")) {
			System.out.println("**********zip=99999***********");
			// System.out.println(driver.getPageSource());
			Assert.assertTrue(driver.getPageSource().contains(
					"Sorry, you seem to have entered invalid criteria.  Please check the following, and try your search again:"));

			driver.findElement(By.linkText("Try a new search")).click();
			logger.log(LogStatus.PASS, "Verify Search by invalid zipcode on Basic CTS. Zipcode = " + zip);
		} else if (zip.equals("abc")) {

			WebElement error_Msg = driver.findElement(By.xpath("//div[@class='error-msg']"));
			error_Msg.getText();
			Assert.assertTrue(error_Msg.getText().contains("Please enter a valid 5 digit ZIP code."));
			System.out.println("Error message for zip: " + error_Msg.getText());
			logger.log(LogStatus.PASS, "Verify Search by Age on Basic CTS. Zip = " + zip);
		}

		else {
			// Verify page title
			String pageTitle = driver.getTitle();
			System.out.println("Page title for zipcode search :" + pageTitle);
			Assert.assertTrue(pageTitle.contains("Clinical Trials Search Results"), "Page Title not matched");

			// Verify search criteria in URL
			resultPageUrl = driver.getCurrentUrl();
			System.out.println("basicsearch_Zipcode: Current Page URL: " + resultPageUrl);
			Assert.assertTrue(resultPageUrl.contains("z=" + zip));
			System.out.println("basicsearch_Zipcode: Zipcode: z=" + zip);

			data = verifySearchCriteriaTable();

			Assert.assertTrue(data[1][1].toString().contains(zip), "zip not matched" + zip);
			System.out.println("data[1][1]==== " + data[1][1]);

			driver.findElement(By.linkText("Start Over")).click();
			logger.log(LogStatus.PASS, "Verify Search by zipcode on Basic CTS. Zipcode = " + zip);

		}
	}

	@Test(dataProvider = "Age_ZipCode", groups = { "Smoke" })
	public void searchAgeZip(int age, String zip) {

		// Performing the search using Age and Zipcode parameter
		basicSearch.searchAgeZip(age, zip);

		// Checking the Results page URL for the parameters passed in order to
		// validate correct search results are displayed
		resultPageUrl = driver.getCurrentUrl();
		// Assert.assertTrue(resultPageUrl.contains(zip), "Keyword not found " +
		// zip);
		Assert.assertTrue(resultPageUrl.contains(String.valueOf(age)), "Keyword not found " + age);
		if (zip.equals("99999")) {
			System.out.println("**********zip=99999***********");
			Assert.assertTrue(driver.getPageSource().contains(
					"Sorry, you seem to have entered invalid criteria.  Please check the following, and try your search again:"));

			driver.findElement(By.linkText("Try a new search")).click();
			logger.log(LogStatus.PASS, "Verify Search by invalid zipcode on Basic CTS. Zipcode = " + zip);
		} else {
			// Verify page title
			String pageTitle = driver.getTitle();
			System.out.println("Page title for zipcode search :" + pageTitle);
			Assert.assertTrue(pageTitle.contains("Clinical Trials Search Results"), "Page Title not matched");

			// Verify search criteria in URL
			// String pageURL = driver.getCurrentUrl();
			System.out.println("basicsearch_AgeZipcode: Current Page URL: " + resultPageUrl);
			Assert.assertTrue(resultPageUrl.contains("z=" + zip));
			System.out.println("basicsearch_AgeZipcode: Zipcode: z=" + zip);
		}

		driver.navigate().back();
		System.out.println("Page URL after the search " + driver.getCurrentUrl());
		logger.log(LogStatus.PASS, "Pass => " + "Verify Search for Age and ZipCode on Basic CTS");
	}

	@Test(dataProvider = "CancerType_Age", groups = { "Smoke" })
	public void searchCancerTypeAge(String cancerType, int age) {
		// int age = 65;
		// String cancerType = "Breast Cancer";

		// Performing the search using Age and Zipcode parameter
		basicSearch.searchCancerTypeAge(cancerType, age);

		if (driver.findElement(By.name("printButton")).isDisplayed()) {
			System.out.println("search results page should be displayed");
			resultPageUrl = driver.getCurrentUrl();
			System.out.println("Result page url: " + resultPageUrl);
		}

		// Checking the Results page URL for the parameters passed in order to
		// validate correct search results are displayed
		resultPageUrl = driver.getCurrentUrl();
		String cancerTypeWithoutSpace = cancerType.replace(" ", "+");
		System.out.println("New String: " + cancerTypeWithoutSpace);
		Assert.assertTrue(resultPageUrl.contains(cancerTypeWithoutSpace),
				"Keyword not found " + cancerTypeWithoutSpace);
		Assert.assertTrue(resultPageUrl.contains(Integer.toString(age)), "Keyword not found " + age);
		driver.navigate().back();
		System.out.println("Page URL after the search " + driver.getCurrentUrl());
		logger.log(LogStatus.PASS, "Pass => " + "Verify Search for Cancer Type and Age on Basic CTS");
	}

	@Test(dataProvider = "CancerType_ZipCode", groups = { "Smoke" })
	public void searchCancerTypeZip(String cancerType, String zip) {
		// String cancerType = "Breast Cancer";

		// Performing the search using Age and ZIP code parameter
		basicSearch.searchCancerTypeZip(cancerType, zip);

		if (zip.equals("99999")) {
			System.out.println("**********zip=99999***********");
			Assert.assertTrue(driver.getPageSource().contains(
					"Sorry, you seem to have entered invalid criteria.  Please check the following, and try your search again:"));

			driver.findElement(By.linkText("Try a new search")).click();
			logger.log(LogStatus.PASS, "Verify Search by invalid zipcode on Basic CTS. Zipcode = " + zip);
		} else if (driver.findElement(By.name("printButton")).isDisplayed()) {
			System.out.println("search results page should be displayed");
			// Verify page title
			String pageTitle = driver.getTitle();
			System.out.println("Page title for zipcode search :" + pageTitle);
			Assert.assertTrue(pageTitle.contains("Clinical Trials Search Results"), "Page Title not matched");

			// Checking the Results page URL for the parameters passed in order
			// to
			// validate correct search results are displayed
			resultPageUrl = driver.getCurrentUrl();
			System.out.println("basicsearch_CancerTypeZipcode: Current Page URL: " + resultPageUrl);
			Assert.assertTrue(resultPageUrl.contains("z=" + zip));
			System.out.println("basicsearch_CancerTypeZipcode: Zipcode: z=" + zip);
			String cancerTypeWithoutSpace = cancerType.replace(" ", "+");
			System.out.println("New String: " + cancerTypeWithoutSpace);
			Assert.assertTrue(resultPageUrl.contains(cancerTypeWithoutSpace),
					"Keyword not found " + cancerTypeWithoutSpace);
			Assert.assertTrue(resultPageUrl.contains(zip), "Keyword not found " + zip);
			driver.navigate().back();
			System.out.println("Page URL after the search " + driver.getCurrentUrl());
			logger.log(LogStatus.PASS, "Pass => " + "Verify Search for Cancer Type and ZipCode on Basic CTS");

		}

	}

	@Test(dataProvider = "CancerType_Age_ZipCode", groups = { "Smoke" })
	public void searchCancerTypeAgeZip(String cancerType, int age, String zip) {
		// int age = 65;
		// int zip = 20105;
		// String cancerType = "Breast Cancer";

		// Performing the search using Age and Zipcode parameter
		basicSearch.searchCancerTypeAgeZip(cancerType, age, zip);

		if (driver.findElement(By.name("printButton")).isDisplayed()) {
			System.out.println("search results page should be displayed");
			resultPageUrl = driver.getCurrentUrl();
			System.out.println("Result page url: " + resultPageUrl);
		}

		// Checking the Results page URL for the parameters passed in order to
		// validate correct search results are displayed
		resultPageUrl = driver.getCurrentUrl();
		String cancerTypeWithoutSpace = cancerType.replace(" ", "+");
		System.out.println("New String: " + cancerTypeWithoutSpace);
		Assert.assertTrue(resultPageUrl.contains(cancerTypeWithoutSpace),
				"Keyword not found " + cancerTypeWithoutSpace);
		Assert.assertTrue(resultPageUrl.contains(zip), "Keyword not found " + zip);
		Assert.assertTrue(resultPageUrl.contains(Integer.toString(age)), "Keyword not found " + age);
		driver.navigate().back();
		System.out.println("Page URL after the search " + driver.getCurrentUrl());
		logger.log(LogStatus.PASS, "Pass => " + "Verify Search for Cancer Type, Age and ZipCode on Basic CTS");
	}

	@Test(groups = { "Smoke" })
	public void verifyDelighterLiveHelp() {
		Assert.assertTrue(delighter.getDelighterLiveHelp().isDisplayed());
		String expectedPageUrl = config.getProperty("DelighterLiveHelpURL");
		// Verifying the LiveHelp Delighter
		delighter.verifyDelighterLiveHelp();
		// Checking the end page URL
		String resultPageUrl = driver.getCurrentUrl();
		System.out.println("Page URL of the delighter " + resultPageUrl);
		Assert.assertTrue(resultPageUrl.equals(expectedPageUrl), "Actual URL is not as expected " + resultPageUrl);
		driver.navigate().back();
		System.out.println("Page URL after the delighter check " + driver.getCurrentUrl());
		logger.log(LogStatus.PASS, "Pass => " + "Verify Live Help Delighter on Advance CTS");
	}

	@Test(groups = { "Smoke" })
	public void verifyDelighterWhat() {
		Assert.assertTrue(delighter.getDelighterWhat().isDisplayed());
		String expectedPageUrl = config.getProperty("DelighterWhatURL");
		// Verifying the LiveHelp Delighter
		delighter.verifyDelighterWhat();
		// Checking the end page URL
		String resultPageUrl = driver.getCurrentUrl();
		System.out.println("Page URL of the delighter " + resultPageUrl);
		Assert.assertTrue(resultPageUrl.equals(expectedPageUrl), "Actual URL is not as expected " + resultPageUrl);
		driver.navigate().back();
		System.out.println("Page URL after the delighter check " + driver.getCurrentUrl());
		logger.log(LogStatus.PASS, "Pass => " + "Verify What are Clinical Trials Delighter on Advance CTS");
	}

	@Test(groups = { "Smoke" })
	public void verifyDelighterWhich() {
		Assert.assertTrue(delighter.getDelighterWhich().isDisplayed());
		String expectedPageUrl = config.getProperty("DelighterWhichURL");
		// Verifying the LiveHelp Delighter
		delighter.verifyDelighterWhich();
		// Checking the end page URL
		String resultPageUrl = driver.getCurrentUrl();
		System.out.println("Page URL of the delighter " + resultPageUrl);
		Assert.assertTrue(resultPageUrl.equals(expectedPageUrl), "Actual URL is not as expected " + resultPageUrl);
		driver.navigate().back();
		System.out.println("Page URL after the delighter check " + driver.getCurrentUrl());
		logger.log(LogStatus.PASS, "Pass => " + "Verify Which Trials are Best for You Delighter on Advance CTS");
	}

	@Test(groups = { "Smoke" })
	public void clickAdvSearch() {
		// Click on Advance Search link
		basicSearch.clickAdvSearch();
		// Checking the Results page URL for the parameters passed in order to
		// validate correct search results are displayed
		advSearchPageUrl = driver.getCurrentUrl();
		System.out.println("Advanced Search " + advSearchPageUrl);
		Assert.assertTrue(advSearchPageUrl.contains("advanced"));
		driver.navigate().back();
		System.out.println("Page URL after the coming back to basic search " + driver.getCurrentUrl());
		logger.log(LogStatus.PASS, "Pass => " + "Verify navigation to Advanced CTS on Basic CTS");
	}

	@Test(groups = { "Smoke" }, priority = 1)
	public void verify_bread_crumb() {
		crumb.getBreadCrumb();
		Assert.assertEquals(crumb.getBreadCrumb(), basicSearch.BREAD_CRUMB);
		System.out.println("Breadcrumb is displaying correctly");
		logger.log(LogStatus.PASS, "Pass => " + "Verifying the Breadcrumb of the page");

	}

	@Test(groups = { "Smoke" }, priority = 1)
	public void verifyBanner() {
		Assert.assertTrue(banner.getBanner().isDisplayed());
		Assert.assertEquals(banner.getBanner().getAttribute("alt"), "National Cancer Institute");
		logger.log(LogStatus.PASS, "Verifying the Banner of the page");
	}

	/*****incorporated these tests with the SearchAge and SearchZip methods**********/
	//@Test(groups = { "Smoke" })
	public void verifyApiReference() {
		api.getApiReference();
		Assert.assertTrue(api.getApiReference().isDisplayed());
		Assert.assertEquals(api.getApiReference().getText(), API_REFERENCE_H3);
		Assert.assertTrue(api.getApiReferenceText().isDisplayed());
		api.verifyApiReferenceLink();
		Assert.assertTrue(driver.getCurrentUrl().contains("/syndication/api"));
		driver.navigate().back();
		System.out.println("**********API function executed***********");
		logger.log(LogStatus.PASS, "Verifying the API Reference section on the page");
	}

	/*****
	 * incorporated these tests with the SearchAge and SearchZip methods
	 **********/
	// @Test(groups = { "Smoke" })
	public void SearchInvalidAge() {
		driver.findElement(By.xpath(".//input[@id='a']")).sendKeys("abc");
		basicSearch.searchDefault();
		System.out.println(
				"********Error Message of Age: " + driver.findElement(By.xpath("//div[@class='error-msg']")).getText());
		Assert.assertTrue(driver.findElement(By.xpath("//div[@class='error-msg']")).getText()
				.contains("Please enter a number between 1 and 120."));
		logger.log(LogStatus.PASS, "Pass => " + "Verify error message for invalid Age on Basic CTS");
		driver.findElement(By.xpath(".//input[@id='a']")).clear();
	}

	//@Test(groups = { "Smoke" })
	public void SearchInvalidZip() {
		//// div[@class='error-msg']
		driver.findElement(By.xpath(".//input[@id='z']")).sendKeys("abc");
		basicSearch.getZipCodeTextElement().click();
		System.out.println("********Error Message of Zipcode: "
				+ driver.findElement(By.xpath("//div[@class='error-msg']")).getText());
		Assert.assertTrue(driver.findElement(By.xpath("//div[@class='error-msg']")).getText()
				.contains("Please enter a valid 5 digit ZIP code."));
		logger.log(LogStatus.PASS, "Pass => " + "Verify error message for invalid Zip on Basic CTS");
		driver.findElement(By.xpath(".//input[@id='z']")).clear();
	}

	/******************** Data Providers ****************/

	@DataProvider(name = "CancerType")
	public Iterator<Object[]> readCancerType() {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);

		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();

		for (int rowNum = 2; rowNum <= excelReader.getRowCount(TESTDATA_SHEET_NAME); rowNum++) {

			String cancerType = excelReader.getCellData(TESTDATA_SHEET_NAME, "CancerType", rowNum);
			Object ob[] = { cancerType };

			myObjects.add(ob);

		}
		return myObjects.iterator();
	}

	@DataProvider(name = "Age")
	public Iterator<Object[]> readAge() {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);
		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();
		for (int rowNum = 2; rowNum <= excelReader.getRowCount(TESTDATA_SHEET_NAME); rowNum++) {
			String age = excelReader.getCellData(TESTDATA_SHEET_NAME, "Age", rowNum);
			int age1 = Integer.valueOf(age);
			Object ob[] = { age1 };
			myObjects.add(ob);
		}
		return myObjects.iterator();
	}

	@DataProvider(name = "ZipCode")
	public Iterator<Object[]> readZipCode() {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);
		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();
		for (int rowNum = 2; rowNum <= excelReader.getRowCount(TESTDATA_SHEET_NAME); rowNum++) {
			String zipcode = excelReader.getCellData(TESTDATA_SHEET_NAME, "ZipCode", rowNum);
			String zipcode1 = String.valueOf(zipcode);
			Object ob[] = { zipcode1 };
			myObjects.add(ob);
		}
		return myObjects.iterator();
	}

	@DataProvider(name = "Age_ZipCode")
	public Iterator<Object[]> readAgeZipCode() {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);

		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();

		for (int rowNum = 2; rowNum <= excelReader.getRowCount(TESTDATA_SHEET_NAME); rowNum++) {
			String age = excelReader.getCellData(TESTDATA_SHEET_NAME, "Age", rowNum);
			int age1 = Integer.valueOf(age);
			String zipcode = excelReader.getCellData(TESTDATA_SHEET_NAME, "ZipCode", rowNum);
			String zipcode1 = String.valueOf(zipcode);
			Object ob[] = { age1, zipcode1 };
			myObjects.add(ob);
		}
		return myObjects.iterator();
	}

	@DataProvider(name = "CancerType_Age")
	public Iterator<Object[]> readCancerTypeAge() {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);

		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();

		for (int rowNum = 2; rowNum <= excelReader.getRowCount(TESTDATA_SHEET_NAME); rowNum++) {

			String cancerType = excelReader.getCellData(TESTDATA_SHEET_NAME, "CancerType", rowNum);
			String age = excelReader.getCellData(TESTDATA_SHEET_NAME, "Age", rowNum);
			int age1 = Integer.valueOf(age);

			Object ob[] = { cancerType, age1 };
			myObjects.add(ob);
		}
		return myObjects.iterator();
	}

	@DataProvider(name = "CancerType_ZipCode")
	public Iterator<Object[]> readCancerTypeZip() {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);

		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();

		for (int rowNum = 2; rowNum <= excelReader.getRowCount(TESTDATA_SHEET_NAME); rowNum++) {

			String cancerType = excelReader.getCellData(TESTDATA_SHEET_NAME, "CancerType", rowNum);
			String zipcode = excelReader.getCellData(TESTDATA_SHEET_NAME, "ZipCode", rowNum);
			String zipcode1 = String.valueOf(zipcode);

			Object ob[] = { cancerType, zipcode1 };
			myObjects.add(ob);
		}
		return myObjects.iterator();
	}

	@DataProvider(name = "CancerType_Age_ZipCode")
	public Iterator<Object[]> readCancerTypeAgeZip() {
		ExcelManager excelReader = new ExcelManager(testDataFilePath);

		ArrayList<Object[]> myObjects = new ArrayList<Object[]>();

		for (int rowNum = 2; rowNum <= excelReader.getRowCount(TESTDATA_SHEET_NAME); rowNum++) {

			String cancerType = excelReader.getCellData(TESTDATA_SHEET_NAME, "CancerType", rowNum);
			String age = excelReader.getCellData(TESTDATA_SHEET_NAME, "Age", rowNum);
			int age1 = Integer.valueOf(age);
			String zipcode = excelReader.getCellData(TESTDATA_SHEET_NAME, "ZipCode", rowNum);
			String zipcode1 = String.valueOf(zipcode);

			Object ob[] = { cancerType, age1, zipcode1 };
			myObjects.add(ob);
		}
		return myObjects.iterator();
	}

	/********************** Common Functions **********************/
	// Verify that show search criteria table contains the selected search
	// criteria
	public Object[][] verifySearchCriteriaTable() {
		Object[][] data;

		driver.findElement(By.xpath(".//*[@class='ctscb']")).click();
		// Thread.sleep(300);

		WebElement table_element = driver.findElement(By.xpath(".//table[@class='table no-auto-enlarge']"));

		List<WebElement> tr_collection = table_element.findElements(By.tagName("tr"));
		System.out.println("Verify Table: NUMBER OF ROWS IN THIS TABLE = " + tr_collection.size());
		data = new Object[tr_collection.size()][2];
		int row_num, col_num;
		row_num = 0;

		for (WebElement trElement : tr_collection) {
			List<WebElement> td_collection = trElement.findElements(By.xpath("td"));
			System.out.println("Verify Table: NUMBER OF COLUMNS=" + td_collection.size());

			col_num = 0;
			for (WebElement tdElement : td_collection) {
				System.out.println(
						"Verify Table: row # " + row_num + ", col # " + col_num + "text=" + tdElement.getText());
				data[row_num][col_num] = tdElement.getText();
				System.out.println("Verify Table: DATA=" + data[row_num][col_num]);
				col_num++;
			}
			row_num++;
		}

		return data;
	}
}