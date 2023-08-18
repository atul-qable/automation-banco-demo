package bradesco.custodia.bc2.performaction.autoweb;

import org.testng.Reporter;
import org.tinylog.Logger;

public class TestNGLogs {

    public void testStep(String message) {
        Logger.info("Step: " + message);
        Reporter.log(message);
    }

    public void testResult(boolean bool) {
        if (bool) {
            Logger.info("Test Passed");
            Reporter.log("<h1 style=\"color:green;font-size:15px;\">Test Passed</h1> ");
        } else {
            Logger.info("Test Failed");
            Reporter.log("<h1 style=\"color:red;font-size:15px;\">Test Failed</h1>   ");
        }
    }
    public void info(String message) {
        Logger.info(message);
        Reporter.log(message);
    }
}
