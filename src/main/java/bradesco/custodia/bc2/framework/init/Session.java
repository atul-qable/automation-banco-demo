package bradesco.custodia.bc2.framework.init;

import org.openqa.selenium.WebDriver;

public class Session {


    public WebDriver createNewWebSession() {
        WebDriver driver;
        Base base = new Base();
        driver = base.init();
        return driver;
    }

    public WebDriver createNewWebSession(String configName) {
        WebDriver driver;
        Base base = new Base();
        driver = base.init();
        return driver;
    }

    public void closeSession(WebDriver driver) {
        driver.quit();
    }

    public WebDriver createNewMobileSession(String configName) {
        WebDriver driver;
        Base base = new Base();
        driver = base.setupAndroid(configName);
        return driver;
    }

}
