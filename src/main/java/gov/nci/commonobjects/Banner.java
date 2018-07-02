package gov.nci.commonobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class Banner {
	WebDriver driver;

	@FindBy(how = How.XPATH, using = ".//img[@src='/publishedcontent/images/images/design-elements/logos/nci-logo-full.__v1.svg']")

	WebElement banner;

	public Banner(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public WebElement getBanner() {

		return banner;

	}
}