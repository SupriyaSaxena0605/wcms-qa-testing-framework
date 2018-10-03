package gov.nci.commonobjects;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import gov.nci.Utilities.ScrollUtil;
import gov.nci.framework.PageObjectBase;

public class MegaMenu extends PageObjectBase {

	// TODO: fix issues with element visibility on By.linktext()

	// Local driver object and actions
	private WebDriver driver;
	private Actions action;
	private WebDriverWait wait;

	// Constructor to initialize the page object
	public MegaMenu(WebDriver driver, String environment) throws MalformedURLException, UnsupportedEncodingException {
		super(driver);
		this.driver = driver;
		action = new Actions(driver);
		wait = new WebDriverWait(driver, 5);

		PageFactory.initElements(driver, this);
	}

	/**
	 * Web elements -- These are the elements that make up our page object
	 */
	@FindBy(css = "#mega-nav .nav-item-title a")
	WebElement mm_bar_link;
	@FindBy(css = "#mega-nav .sub-nav-group a")
	WebElement mm_subnav_header;
	@FindBy(css = "#mega-nav .sub-nav-group ul li a")
	WebElement mm_subnav_li;
	@FindBy(linkText = "What Is Cancer")
	WebElement mm_subnav_li_text;
	@FindBy(css = ".mobile-menu-bar button.menu-btn")
	WebElement mm_reveal_mobile;
	@FindBy(css = "#mega-nav .mega-menu-scroll.open")
	WebElement mm_reveal_desktop;

	/**
	 * Browser actions -- all of the proxy browser 'actions' go in here. These are
	 * not tests, but things that we do to fire off analytics events. These actions
	 * will populate our list of har objects, which will then be tested.
	 */
	public void clickMegaMenuBar() {
		mm_bar_link.click();
	}

	public void clickMegaMenuBar(String text) {
		WebElement element = getMegaMenuElementByText(text);
		ScrollUtil.scrollIntoview(driver, element);
		element.click();
	}

	public void clickMegaMenuSubNavHeader() {
		action.moveToElement(mm_bar_link).perform();
		wait.until(ExpectedConditions.visibilityOf(mm_subnav_header));
		mm_subnav_header.click();
	}

	public void clickMegaMenuListItem() {
		action.moveToElement(mm_bar_link).perform();
		wait.until(ExpectedConditions.visibilityOf(mm_subnav_li_text));
		mm_subnav_li_text.click();
	}

	public void clickMegaMenuListItem(String text) {
		// WebElement element = mm_subnav_li_text;
		// WebElement element = getMegaMenuElementByText(text);
		// JavascriptExecutor js = (JavascriptExecutor) driver;
		// js.executeScript("arguments[0].click();", element);

		action.moveToElement(mm_bar_link).perform();
		wait.until(ExpectedConditions.visibilityOf(mm_subnav_li));
		mm_subnav_li.click();
	}

	public void revealMegaMenuDesktop() {
		action.moveToElement(mm_bar_link).perform();
		action.pause(2000).perform();
		wait.until(ExpectedConditions.visibilityOf(mm_subnav_li_text));
	}

	public void revealMegaMenuMobile() {
		driver.manage().window().setSize(Resize.small);
		mm_reveal_mobile.click();
	}

	/**
	 * Using XPATH, return an element based on the contained text.
	 * 
	 * @param text
	 * @return
	 */
	// TODO: make this a reusable utility function
	private WebElement getMegaMenuElementByText(String text) {
		WebElement element = driver.findElement(By.xpath("//*[contains(text(), '" + text + "')]"));

		return element;
	}

}
