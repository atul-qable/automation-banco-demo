package bradesco.custodia.bc2.performaction.autoweb;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Alert {
    WebDriver driver;
    Element element;
    TestNGLogs logs = new TestNGLogs();

    public Alert(WebDriver dri) {
        this.driver = dri;
        element = new Element(driver);
    }

    public String getAlertText() {
        logs.testStep("Get Alert Text");
        org.openqa.selenium.Alert alert = new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.alertIsPresent());
        return alert.getText();
    }

    public void accept() {
        logs.testStep("Accept the Alert");
        org.openqa.selenium.Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    public void dismiss() {
        logs.testStep("Dismiss the Alert");
        org.openqa.selenium.Alert alert = driver.switchTo().alert();
        alert.dismiss();
    }

    public void enterTextInAlert(String text) {
        logs.testStep("Enter " + text + " in the Alert");
        org.openqa.selenium.Alert alert = new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.alertIsPresent());
        alert.sendKeys(text);
    }
}