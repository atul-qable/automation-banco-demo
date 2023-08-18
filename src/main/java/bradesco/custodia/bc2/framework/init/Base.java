package bradesco.custodia.bc2.framework.init;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;

import bradesco.custodia.bc2.framework.data.TestData;
import bradesco.custodia.bc2.framework.readers.JsonFileReader;
import bradesco.custodia.bc2.framework.remotegrid.LambdaTestConfig;
import bradesco.custodia.bc2.performaction.autoweb.Browser;
import bradesco.custodia.bc2.performaction.autoweb.TestNGLogs;
import bradesco.custodia.bc2.framework.runner.TestRunner;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.tinylog.Logger;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import kong.unirest.Unirest;

public class Base {

    private String env = "";
    private String platform = "";
    private String browserName = "";
    private String executionOn = "";
    public Browser browser;

    public WebDriver driver;
    private JsonFileReader config = new JsonFileReader();
    private TestNGLogs logs = new TestNGLogs();

    public static String buildName;

    @BeforeSuite
    public void beforeSuiteWorks() {
        buildName = "Build_" + TestData.getTodayDateInFormat("dd-MMM-yyyy");
    }

    @BeforeMethod
    public WebDriver init() {

        try {
            if (TestRunner.currentConfig.equals("")) {
                TestRunner.currentConfig = config.getRunConfig();
            }
            System.out.println("Current Config"+TestRunner.currentConfig);
            platform = config.getPlatform(TestRunner.currentConfig);

            Logger.info("Base config to run: " + TestRunner.currentConfig);

            if (platform.equalsIgnoreCase("web")) {
                if (config.isGrid(TestRunner.currentConfig)) {
                    setupBrowserForGrid(TestRunner.currentConfig);
                } else {
                    setupBrowser(TestRunner.currentConfig);
                }
                env = config.getEnv(TestRunner.currentConfig);
                browser = new Browser(driver);
                browser.openUrl(env);

                if (config.isAPITestConfigEnable(TestRunner.currentConfig)) {
                    Unirest.config().defaultBaseUrl(config.getAPIEnv(TestRunner.currentConfig));
                }
            } else if (platform.equalsIgnoreCase("android")) {
                System.out.println("Inside android");
                setupAndroid(TestRunner.currentConfig);
            } else if (platform.equalsIgnoreCase("iOS")) {
                System.out.println("Inside iOS");
                setup_IOS(TestRunner.currentConfig);

            } else if (platform.equalsIgnoreCase("api")) {
                Unirest.config().defaultBaseUrl(config.getAPIEnvDirect(TestRunner.currentConfig));
            } else {
                Logger.info("Platform type you entered is not supported");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driver;
    }

    private WebDriver setupBrowser(String configName) {
        System.out.println("Setting up browser");
        browserName = config.getBrowser(configName);
        System.out.println("Browser name: " + browserName);
        if (browserName.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", TestRunner.pathForConfigurationFile);
            options = new ChromeOptions();
            options.setExperimentalOption("prefs", chromePrefs);
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(options);
            System.out.println("Inside Chrome");
        } else if (browserName.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
            System.out.println("Inside Firefox");
        } else if (browserName.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
            System.out.println("Inside Edge");
        }
        else if (browserName.equalsIgnoreCase("chrome-headless")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            driver = new ChromeDriver(options);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        return driver;
    }

    public WebDriver setupBrowserForGrid(String configName) {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        JSONObject object = config.getCapabilities(configName);
        Iterator<String> keys = object.keys();
        capabilities.setBrowserName(config.getBrowser(configName));

        if (config.getGridPlatform(configName).equalsIgnoreCase("selenium")) {
            while (keys.hasNext()) {
                String key = keys.next();
                capabilities.setCapability(key, object.get(key));
            }

        } else if (config.getGridPlatform(configName).equalsIgnoreCase("browserstack")) {

            JSONObject browserStackOptionObject = config.getBrowserStackOption(configName);

            while (keys.hasNext()) {
                String key = keys.next();
                capabilities.setCapability(key, object.get(key));
            }
            Iterator<String> browserStackOptionKey = browserStackOptionObject.keys();
            HashMap<String, Object> browserStackOptions = new HashMap<>();

            while (browserStackOptionKey.hasNext()) {
                String key = browserStackOptionKey.next();
                browserStackOptions.put(key, object.get(key));
            }

            capabilities.setCapability("bstack:options", browserStackOptions);

        } else if (config.getGridPlatform(configName).equalsIgnoreCase("saucelab")) {

            JSONObject sauceLabOption = config.getSauceLabOption(configName);

            while (keys.hasNext()) {
                String key = keys.next();
                capabilities.setCapability(key, object.get(key));
            }
            Iterator<String> sauceLabOptionKey = sauceLabOption.keys();
            HashMap<String, Object> sauceLabOptions = new HashMap<>();

            while (sauceLabOptionKey.hasNext()) {
                String key = sauceLabOptionKey.next();
                sauceLabOptions.put(key, object.get(key));
            }

            capabilities.setCapability("sauce:options", sauceLabOptions);

        } else if (config.getGridPlatform(configName).equalsIgnoreCase("lambdatest")) {

            JSONObject lambdaTestOption = config.getLambdaTestOption(configName);

            while (keys.hasNext()) {
                String key = keys.next();
                capabilities.setCapability(key, object.get(key));
            }
            Iterator<String> lambdaTestOptionKey = lambdaTestOption.keys();
            HashMap<String, Object> lambdaOptions = new HashMap<>();

            while (lambdaTestOptionKey.hasNext()) {
                String key = lambdaTestOptionKey.next();
                lambdaOptions.put(key, lambdaTestOption.get(key));
            }

            capabilities.setCapability("build", buildName);
            capabilities.setCapability("LT:options", lambdaOptions);
        }

        try {
            driver = new RemoteWebDriver(new URL(config.getGridUrl(configName)), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));

        return driver;
    }


    public AndroidDriver setupAndroid(String configName) {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        JSONObject capabilityList = config.getCapabilities(configName);

        Iterator<String> itr = capabilityList.keySet().iterator();

        while (itr.hasNext()) {
            String key = itr.next();
            capabilities.setCapability(key, capabilityList.get(key));
        }

        if (config.getAppiumPlatform(configName).equalsIgnoreCase("lambdaTest")) {

            JSONObject lambdaTestOption = config.getLambdaTestOption(configName);

            Iterator<String> lambdaTestOptionKey = lambdaTestOption.keys();
            HashMap<String, Object> lambdaTestOptionsMap = new HashMap<>();

            while (lambdaTestOptionKey.hasNext()) {
                String key = lambdaTestOptionKey.next();

                lambdaTestOptionsMap.put(key, lambdaTestOption.get(key));
            }

            capabilities.setCapability("build", buildName);

            capabilities.setCapability("LT:options", lambdaTestOptionsMap);

        } else {
            capabilities.setCapability("appium:app", config.getFinalAppPath(configName));
        }

        try {
            driver = new AndroidDriver(new URL(config.getAppiumUrl(configName)), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));

        return (AndroidDriver) driver;
    }


    public IOSDriver setup_IOS(String configName) {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        JSONObject capabilityList = config.getCapabilities(configName);

        Iterator itr = capabilityList.keySet().iterator();

        while (itr.hasNext()) {
            String key = (String) itr.next();
            capabilities.setCapability(key, capabilityList.get(key));
        }
        capabilities.setCapability("app", config.getFinalAppPath(configName));
        try {
            driver = new IOSDriver(new URL(config.getAppiumUrl(configName)), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        return (IOSDriver) driver;
    }

    File file = null;

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            try {
                if (!platform.equalsIgnoreCase("api")) {
                    TakesScreenshot screenshot = (TakesScreenshot) driver;
                    File src = screenshot.getScreenshotAs(OutputType.FILE);
                    file = new File("src/test/resources/failedTestScreenshot/" + result.getName() + "_" + TestData.generateRandomAlphaNumericString(5) + ".png");
                    FileUtils.copyFile(src, file);
                    logs.testResult(false);
                    logs.testStep("<img src=\"" + file.getAbsolutePath() + "\" alt=\"test\" width=\"1024\" height=\"640\">");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String currentPlatform = config.getPlatform(TestRunner.currentConfig);
        if (currentPlatform.equalsIgnoreCase("web")) {
            String gridPlatform = config.getGridPlatform(TestRunner.currentConfig);
            if (gridPlatform.equalsIgnoreCase("lambdatest")) {
                LambdaTestConfig lambdaTestConfig = new LambdaTestConfig(driver);
                if (ITestResult.FAILURE == result.getStatus()) {
                    lambdaTestConfig.markTestFailed();
                } else {
                    lambdaTestConfig.markTestPassed();
                }
            }
        }

        if (currentPlatform.equalsIgnoreCase("android") || currentPlatform.equalsIgnoreCase("ios")) {
            String appiumPlatform = config.getAppiumPlatform(TestRunner.currentConfig);
            if (appiumPlatform.equalsIgnoreCase("lambdatest")) {
                LambdaTestConfig lambdaTestConfig = new LambdaTestConfig(driver);
                if (ITestResult.FAILURE == result.getStatus()) {
                    lambdaTestConfig.markTestFailed();
                } else {
                    lambdaTestConfig.markTestPassed();
                }
            }
        }

        if (!platform.equalsIgnoreCase("api")) {
            driver.quit();
        }
    }


    public void suiteTearDown() {
        if (platform.equalsIgnoreCase("api")) {
            File f = new File("./src/test/java/api/data/temp");

            try {
                FileUtils.cleanDirectory(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}