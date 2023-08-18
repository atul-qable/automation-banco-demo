package bradesco.custodia.bc2.performaction.autoweb;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Browser {
    WebDriver driver;
    Element element;
    TestNGLogs logs = new TestNGLogs();

    public Browser(WebDriver driver) {
        this.driver = driver;
        element = new Element(driver);

    }

    public void openUrl(String url) {
        driver.get(url);
    }

    public String getCurrentUrl() {
        String currentUrl = driver.getCurrentUrl();
        logs.testStep("Getting current url : " + currentUrl);
        return currentUrl;
    }

    public String getPageSource() {
        String currentPageSource = driver.getPageSource();
        logs.testStep("Getting current page source : ");
        logs.testStep("==========================================================");
        logs.testStep(currentPageSource);
        logs.testStep("==========================================================");

        return driver.getPageSource();
    }

    public String getTitle() {
        String currentPageTitle = driver.getTitle();
        logs.testStep("Getting current page title : " +currentPageTitle);
        return currentPageTitle;
    }

    public void navigateToBack() {
        logs.testStep("Navigate to previous page");
        driver.navigate().back();
    }

    public void navigateToForward() {
        logs.testStep("Navigate to Next page");

        driver.navigate().forward();
    }

    public void refreshPage() {
        logs.testStep("Refresh the Page");
        driver.navigate().refresh();
    }

    public void closeBrowser() {
        logs.testStep("Close browser");
        driver.quit();
    }

    public void takePageScreenshot(String imageName) {

        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            createScreenshotDir();
            File screenshotFile = new File("./screenshot/" + imageName + ".png");
            FileUtils.copyFile(scrFile, screenshotFile);

            logs.testStep("Screenshot saved at  <img href="+screenshotFile.getAbsolutePath()+">" );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createScreenshotDir() throws IOException {
        Path screenShotPath = Paths.get("./" + "screenshot");
        if (!Files.isDirectory(screenShotPath)) {
            Files.createTempDirectory(screenShotPath.toAbsolutePath().toString());
        }
    }

    public void takePageScreenshot(String folderPath, String imageName) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            File screenshotFile = new File(folderPath + "/" + imageName + ".png");
            FileUtils.copyFile(scrFile, screenshotFile);
            logs.testStep("Screenshot saved at  <img href="+screenshotFile.getAbsolutePath()+">" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}