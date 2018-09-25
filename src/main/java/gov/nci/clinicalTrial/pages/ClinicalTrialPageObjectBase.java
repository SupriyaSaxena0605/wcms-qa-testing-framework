package gov.nci.clinicalTrial.pages;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import gov.nci.Utilities.ScrollUtil;
import gov.nci.framework.PageObjectBase;

/**
 * This is the base class for all Clinical Trial page objects. This class
 * implements the Decorator pattern (https://en.wikipedia.org/wiki/Decorator_pattern)
 * to separate behaviors (e.g. suppress vs. allowing the pro-active chat prompt) into
 * behavior-specific classes, separate from the page objects.
 */
public class ClinicalTrialPageObjectBase extends PageObjectBase {

	// Reference to the decorator object.
	private ClinicalTrialPageObjectBase decorator;
	private WebDriver browser;

	public ClinicalTrialPageObjectBase(WebDriver browser, ClinicalTrialPageObjectBase decorator) throws MalformedURLException, UnsupportedEncodingException {
		super(browser);
		this.decorator = decorator;
		this.browser = browser;
	}

	/**
	 * Click a given input element based on its selector.
	 * 
	 * @param selector
	 * @return
	 */
	public WebElement getSelectedField(String selector) {
		WebElement element = browser.findElement(By.cssSelector(selector));
		return element;
	}

	/**
	 * Click a given input element based on its selector.
	 * 
	 * @param selector
	 */
	public void clickSelectedField(String selector) {
		WebElement element = getSelectedField(selector);
		ScrollUtil.scrollIntoview(browser, element);
		element.click();
	}

	/**
	 * Complete a given field based on its selector.
	 * 
	 * @param selector
	 * @param value
	 */
	public void setSelectedField(String selector, String value) {
		WebElement element = getSelectedField(selector);
		clickSelectedField(selector);
		element.sendKeys(value);
	}

	/**
	 * Submit a page after filling out an element.
	 * 
	 * @param selector
	 * @param value
	 */
	public void submitFromSelectedField(String selector, String value) {
		WebElement element = getSelectedField(selector);
		setSelectedField(selector, value);
		element.sendKeys(Keys.ENTER);
	}
	
}