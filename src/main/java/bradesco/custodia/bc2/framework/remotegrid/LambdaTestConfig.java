package bradesco.custodia.bc2.framework.remotegrid;

import bradesco.custodia.bc2.framework.readers.JsonFileReader;
import bradesco.custodia.bc2.framework.runner.TestRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class LambdaTestConfig {

    WebDriver driver;

    public LambdaTestConfig(WebDriver driver) {

        this.driver = driver;
    }


    public void addTestName(String TestName) {

        JsonFileReader config = new JsonFileReader();

        if (config.getPlatform(TestRunner.currentConfig).equalsIgnoreCase("web")) {
            if (config.getGridPlatform(TestRunner.currentConfig).equalsIgnoreCase("lambdatest")) {

                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("lambda-name=" + TestName + "");

            }

        }

        if (config.getPlatform(TestRunner.currentConfig).equalsIgnoreCase("android") || config.getPlatform(TestRunner.currentConfig).equalsIgnoreCase("ios")) {
            if (config.getAppiumPlatform(TestRunner.currentConfig).equalsIgnoreCase("lambdatest")) {

                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("lambda-name=" + TestName + "");

            }

        }


    }

    public void markTestPassed() {

        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("lambda-status=passed");
    }

    public void markTestFailed() {

        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("lambda-status=failed");
    }
}


