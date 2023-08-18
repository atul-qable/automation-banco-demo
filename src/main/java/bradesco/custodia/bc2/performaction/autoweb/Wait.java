package bradesco.custodia.bc2.performaction.autoweb;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Wait {

    private WebDriver driver;
    private Element element;

    public Wait(WebDriver driver) {
        this.driver = driver;
        this.element = new Element(driver);
    }

    public void waitUntilElementIsClickable(String locatorValue) {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(element.find(locatorValue)));
    }

    public void waitUntilElementIsVisible(String locatorValue) {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.visibilityOf(element.find(locatorValue)));
    }

    public void waitUntilElementIsDisappear(String locatorValue) {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.invisibilityOf(element.find(locatorValue)));
    }

    public void waitUntilAllElementsAreVisible(String locatorValue) {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.visibilityOfAllElements(element.find(locatorValue)));
    }

    public void waitUntilAllElementsAreDisappear(String locatorValue) {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.invisibilityOfAllElements(element.find(locatorValue)));
    }

    public void waitForAlertIsPresent() {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.alertIsPresent());
    }

    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep((long) seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void implicitWaitForPage() {
        driver.manage().timeouts().implicitlyWait(7, SECONDS);
    }

    public void fluentWaitForElement(String locatorValue, int timeoutTime, int pollingTime) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutTime))
                .pollingEvery(Duration.ofSeconds(pollingTime))
                .ignoring(Exception.class);

        WebElement element = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                WebElement element = Wait.this.element.find(locatorValue);
                return element;
            }
        });
        element.click();
    }
}
