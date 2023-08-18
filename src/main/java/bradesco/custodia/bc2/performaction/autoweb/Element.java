package bradesco.custodia.bc2.performaction.autoweb;


import bradesco.custodia.bc2.framework.exception.LocatorValidationException;
import com.google.common.net.MediaType;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.devtools.v85.log.Log;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.http.Route;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import static org.openqa.selenium.remote.http.Contents.utf8String;


public class Element {

    private final WebDriver driver;
    private final TestNGLogs logs = new TestNGLogs();


    public Element(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement getElementFromValue(String locatorType, String locatorValue) {
        WebElement element = null;
        switch (locatorType) {
            case "xpath":
                element = driver.findElement(By.xpath(locatorValue));
                break;
            case "id":
                element = driver.findElement(By.id(locatorValue));
                break;
            case "cssSelector":
                element = driver.findElement(By.cssSelector(locatorValue));
                break;
            case "className":
                element = driver.findElement(By.className(locatorValue));
                break;
            case "name":
                element = driver.findElement(By.name(locatorValue));
                break;
            case "linkText":
                element = driver.findElement(By.linkText(locatorValue));
                break;
            case "partialLinkText":
                element = driver.findElement(By.partialLinkText(locatorValue));
                break;
            case "tag":
                element = driver.findElement(By.tagName(locatorValue));
                break;
            case "accessibilityId":
                element = driver.findElement(new AppiumBy.ByAccessibilityId(locatorValue));
                break;
            default:
                logs.testStep("Incorrect Locator Type");
        }
        return element;
    }

    public WebElement find(String locatorValue) {
        WebElement element = null;
        LocatorReader reader = new LocatorReader();
        Map<String, String> locatorDetails = reader.getLocatorValue(locatorValue);
        element = getElementFromValue(locatorDetails.get("locatorType"),
            locatorDetails.get("locatorValue"));
        return element;
    }

    public WebElement findElementUsingDynamicXpath(String locatorValue,
        Map<String, String> dynamicValue) throws LocatorValidationException {
        WebElement element = null;
        LocatorReader reader = new LocatorReader();

        Map<String, String> locatorDetails = reader.getLocatorValue(locatorValue);

        String finalXpath = "";
        if (locatorDetails.get("locatorType").equalsIgnoreCase("dyn-xpath")) {
            String currentXpath = locatorDetails.get("locatorValue");
            finalXpath = currentXpath;
            if (currentXpath.contains("${")) {
                for (Map.Entry<String, String> entry : dynamicValue.entrySet()) {
                    System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());
                    finalXpath = finalXpath.replace("${" + entry.getKey() + "}", entry.getValue());
                }
            } else {
                throw new LocatorValidationException("No Dynamic Value Found in Locator");
            }
        } else {
            throw new LocatorValidationException(
                "Locator Type is Not a Dynamic Xpath, This Method Only Use for the Dynamic Xpath");
        }
        return driver.findElement(By.xpath(finalXpath));
    }

    public List<WebElement> findMultipleElements(String locatorValue) {
        List<WebElement> elements = null;
        LocatorReader reader = new LocatorReader();
        Map<String, String> locatorDetails = reader.getLocatorValue(locatorValue);

        switch (locatorDetails.get("locatorType")) {
            case "xpath":
                elements = driver.findElements(By.xpath(locatorDetails.get("locatorValue")));
                break;
            case "id":
                elements = driver.findElements(By.id(locatorDetails.get("locatorValue")));
                break;
            case "cssSelector":
                elements = driver.findElements(By.cssSelector(locatorDetails.get("locatorValue")));
                break;
            case "className":
                elements = driver.findElements(By.className(locatorDetails.get("locatorValue")));
                break;
            case "name":
                elements = driver.findElements(By.name(locatorDetails.get("locatorValue")));
                break;
            case "linkText":
                elements = driver.findElements(By.linkText(locatorDetails.get("locatorValue")));
                break;
            case "partialLinkText":
                elements = driver.findElements(
                    By.partialLinkText(locatorDetails.get("locatorValue")));
                break;
            case "tag":
                elements = driver.findElements(By.tagName(locatorDetails.get("locatorValue")));
                break;
            default:
                logs.testStep("Incorrect Locator Type");
        }
        return elements;
    }


    public List<WebElement> findMultipleElementsFromElement(String mainElement,
        String elementToFind) {
        WebElement main = find(mainElement);
        List<WebElement> elements = null;
        LocatorReader reader = new LocatorReader();
        Map<String, String> locatorDetails = reader.getLocatorValue(elementToFind);

        switch (locatorDetails.get("locatorType")) {
            case "xpath":
                elements = main.findElements(By.xpath(locatorDetails.get("locatorValue")));
                break;
            case "id":
                elements = main.findElements(By.id(locatorDetails.get("locatorValue")));
                break;
            case "cssSelector":
                elements = main.findElements(By.cssSelector(locatorDetails.get("locatorValue")));
                break;
            case "className":
                elements = main.findElements(By.className(locatorDetails.get("locatorValue")));
                break;
            case "name":
                elements = main.findElements(By.name(locatorDetails.get("locatorValue")));
                break;
            case "linkText":
                elements = main.findElements(By.linkText(locatorDetails.get("locatorValue")));
                break;
            case "partialLinkText":
                elements = main.findElements(
                    By.partialLinkText(locatorDetails.get("locatorValue")));
                break;
            case "tag":
                elements = main.findElements(By.tagName(locatorDetails.get("locatorValue")));
                break;
            default:
                logs.testStep("Incorrect Locator Type");
        }

        return elements;
    }

    public WebElement findElementFromElement(String mainElement, String elementToFind) {
        WebElement main = find(mainElement);
        WebElement element = null;
        String[] locatorToFind = elementToFind.split(":");

        switch (locatorToFind[0]) {
            case "xpath":
                element = main.findElement(By.xpath(locatorToFind[1]));
                break;
            case "id":
                element = main.findElement(By.id(locatorToFind[1]));
                break;
            case "cssSelector":
                element = main.findElement(By.cssSelector(locatorToFind[1]));
                break;
            case "className":
                element = main.findElement(By.className(locatorToFind[1]));
                break;
            case "name":
                element = main.findElement(By.name(locatorToFind[1]));
                break;
            case "linkText":
                element = main.findElement(By.linkText(locatorToFind[1]));
                break;
            case "partialLinkText":
                element = main.findElement(By.partialLinkText(locatorToFind[1]));
                break;
            case "tag":
                element = main.findElement(By.tagName(locatorToFind[1]));
                break;
            default:
                logs.testStep("Incorrect Locator Type");
        }

        return element;
    }

    public WebElement getActiveElement() {

        return driver.switchTo().activeElement();
    }

    public String getElementTag(String locator_value) {
        return find(locator_value).getTagName();
    }

    public String getCssValue(String locatorValue, String css) {

        return find(locatorValue).getCssValue(css);
    }

    public String getElementText(String locatorValue) {
        return find(locatorValue).getText();
    }

    public String getMatchingText(String elementLocator, String targetValue) {
        List<WebElement> textElements = findMultipleElements(elementLocator);

        for (WebElement textElement : textElements) {
            String text = textElement.getText().trim();
            if (text.equals(targetValue)) {
                return text;
            }
        }

        return null;  // or throw an exception if needed
    }


    public void enterText(String locatorValue, String textToEnter) {
        logs.testStep("Enter text " + textToEnter + " at locator " + locatorValue);
        find(locatorValue).clear();
        find(locatorValue).sendKeys(textToEnter);
    }

    public void clearTextField(String locatorValue) {
        logs.testStep("Clear value from " + locatorValue + " text field");
        Verify verify = new Verify(driver);
        if(verify.isWindows()) {
            find(locatorValue).sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
        }else{
            find(locatorValue).sendKeys(Keys.COMMAND + "A", Keys.DELETE);
        }
    }

    public void clearAndEnterInTextField(String locatorValue, String textToEnter) {
        logs.testStep("Clear value from " + locatorValue + " text field and enter text " + textToEnter);
        Verify verify = new Verify(driver);
        if(verify.isWindows()) {
            find(locatorValue).sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
            find(locatorValue).sendKeys(textToEnter);
        }else {
            find(locatorValue).sendKeys(Keys.COMMAND + "A", Keys.DELETE);
            find(locatorValue).sendKeys(textToEnter);
        }
    }

    public void enterDetailsOnCheckBoxAndPressEnterButton(String locatorValue, String textToEnter){
        logs.testStep("Clear value from " + locatorValue + " text field and enter text " + textToEnter);
        Verify verify = new Verify(driver);
        if(verify.isWindows()) {
            find(locatorValue).sendKeys(Keys.CONTROL + "A", Keys.BACK_SPACE);
            find(locatorValue).sendKeys(textToEnter);
            find(locatorValue).sendKeys(Keys.ENTER);
        }else {
            find(locatorValue).sendKeys(Keys.COMMAND + "A", Keys.DELETE);
            find(locatorValue).sendKeys(textToEnter);
            find(locatorValue).sendKeys(Keys.RETURN);
        }
    }


    public void click(String locatorValue) {
        logs.testStep("Click on " + locatorValue);
        try {
            WebElement element = find(locatorValue);
            element.click();
            logs.info("INFO: Clicked on " + locatorValue);
        } catch (NoSuchElementException e) {
            logs.testStep("ERROR: Element " + locatorValue + " not found.");
        } catch (TimeoutException e) {
            logs.testStep("ERROR: Element " + locatorValue
                + " is not clickable within the specified timeout.");
        } catch (Exception e) {
            logs.testStep("ERROR: An error occurred while clicking on " + locatorValue);
        }
    }


    /**
     * @param elementName
     * @param elementTextForClick
     * @apiNote Click on the first element from the list
     */
    public void clickOnElementWithTextFromList(String elementName, String elementTextForClick) {
        logs.testStep("Click on " + elementTextForClick + " from list ");
        List<WebElement> elementsList = findMultipleElements(elementName);
        Boolean foundElement = false;

        for (WebElement element : elementsList) {
            logs.testStep("Getting text from element:" + element.getText());
            if (element.getText().trim().equals(elementTextForClick)) {
                element.click();
                foundElement = true;
                break;
            }
        }

        if (!foundElement) {
            logs.testStep("Unable to find element with text " + elementTextForClick);
        }
    }

    public void selectSpecificTime(String elementName, String selectValue) {
        List<WebElement> webElements = findMultipleElements(elementName);
        for (WebElement element : webElements) {
            scrollIntoView(element);
            String text = element.getText();
            logs.testStep(text);
            if (text.contentEquals(selectValue)) {
                element.click();
                break;
            } else {
                logs.testStep("INFO:Selected Value is not displayed: " + text);
            }
        }
    }

    public void scrollIntoView(WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void clickOnEditOrDeleteIconFromTheList(String selectValue, String selectTableValue,
        String elementName) {
        List<WebElement> elements = driver.findElements(
            By.xpath("//div[@data-field='" + selectTableValue + "']//p"));
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getText().equalsIgnoreCase(selectValue)) {
                int newValue = i + 1;
                driver.findElement(
                        By.xpath("(//*[@data-testid='" + elementName + "'])[" + newValue + "]"))
                    .click();
            } else {
                logs.testStep("INFO:" + selectValue + " " + "is not present on Ui");
            }
        }
    }

    public void clickUsingJS(String element) throws Exception {
        try {
            logs.testStep("Click on " + element);
            if (find(element).isEnabled() && find(element).isDisplayed()) {
                logs.testStep("Clicking on element with using JavaScript click");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } else {
                logs.testStep("Unable to click on element");
            }
        } catch (StaleElementReferenceException e) {
            logs.testStep("Element is not attached to the page document " + e.getStackTrace());
        } catch (NoSuchElementException e) {
            logs.testStep("Element was not found in DOM " + e.getStackTrace());
        } catch (Exception e) {
            logs.testStep("Unable to click on element " + e.getStackTrace());
        }
    }


    public String getAttributeValue(String locatorValue, String attributeName) {
        return find(locatorValue).getAttribute(attributeName);
    }


    public void takeElementScreenshot(WebElement element, String imageName) {
        File scrFile = element.getScreenshotAs(OutputType.FILE);
        try {
            File screenshotFile = new File("./" + imageName + ".png");
            FileUtils.copyFile(scrFile, screenshotFile);
            logs.testStep(
                "Screenshot saved at <img href=" + screenshotFile.getAbsolutePath() + ">");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void networkInterceptionMethod(WebDriver driver) {
        /* 1. If you want to capture network events coming into the browser
           2. and you want to manipulate them you are able to do it with the following examples.*/

        try (NetworkInterceptor interceptor = new NetworkInterceptor(
            driver,
            Route.matching(req -> true)
                .to(() -> req -> new HttpResponse()
                    .setStatus(200)
                    .addHeader("Content-Type", MediaType.HTML_UTF_8.toString())
                    .setStatus(200)
                    .setContent(utf8String("Creamy, delicious cheese!"))));) {
            logs.testStep("INFO: Network Interceptor is executed..");
        } catch (Exception e) {
            logs.testStep("INFO: " + e.getStackTrace());
        }

    }

    public void jsExceptionMethod(ChromeDriver driver, String locatorValue) {
        // Usage of this method:
        // Listen to the JS Exceptions and register callbacks to process the exception details.

        DevTools devTools = driver.getDevTools();
        devTools.createSession();

        ((ChromeDriver) driver).getDevTools().createSession();

        List<JavascriptException> jsExceptionsList = new ArrayList<>();
        Consumer<JavascriptException> addEntry = jsExceptionsList::add;
        devTools.getDomains().events().addJavascriptExceptionListener(addEntry);

        WebElement linkToClick = find(locatorValue);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].setAttribute(arguments[1], arguments[2]);",
            linkToClick, "onclick", "throw new Error('Hello, world!')");
        linkToClick.click();

        for (JavascriptException jsException : jsExceptionsList) {
            logs.testStep("JS exception message: " + jsException.getMessage());
            logs.testStep("JS exception system information: " + jsException.getSystemInformation());
            logs.testStep("JS exception Get cause: " + jsException.getCause());
            logs.testStep(
                "JS exception get Build Information: " + jsException.getBuildInformation());
            logs.testStep("JS exception Get full stack trace: " + jsException.fillInStackTrace());
            logs.testStep("JS exception get raw Message: " + jsException.getRawMessage());

            jsException.printStackTrace();
        }
    }

    public void consoleLogMethod(ChromeDriver driver) {
        DevTools devTools = driver.getDevTools();
        devTools.createSession();
        devTools.getCdpSession();
        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(), logEntry -> {
            logs.info("INFO : log      : " + logEntry.getText());
            logs.info("INFO : level    : " + logEntry.getLevel());
            logs.info("INFO : Time     : " + logEntry.getTimestamp());
            logs.info("INFO : URL      : " + logEntry.getUrl());
            logs.info("INFO : WorkerID : " + logEntry.getWorkerId());
        });
    }

    public void selectSingleOptionFromDropdown(String locatorValue, String value) {
        Select dropdown = new Select(find(locatorValue));
        List<WebElement> options = dropdown.getOptions();
        for (WebElement option : options) {
            if (option.getText().equals(value)) {
                option.click();
                logs.info("INFO : " + value + " is selected.");
                break;
            }
        }
    }

    public void selectAllOptionsFromDropdown(String locatorValue) {
        Select dropdown = new Select(find(locatorValue));
        boolean isMultipleSelectedDropdown = dropdown.isMultiple();
        List<WebElement> options = dropdown.getOptions();
        if (isMultipleSelectedDropdown) {
            for (WebElement option : options) {
                option.click();
            }
            logs.info("INFO : All options are selected.");
        } else {
            logs.info("INFO : This dropdown is not a multi-selected dropdown.");
        }
    }

    public void selectValueFromDropdown(String elementName, String elementTextForClick) {
        logs.info("Click on " + elementTextForClick + " from list");
        List<WebElement> elementsList = findMultipleElements(elementName);
        boolean elementFound = false;

        for (WebElement element : elementsList) {
            try {
                String elementText = element.getText().trim();
                logs.testStep("Getting Text from Element: " + elementText);
                if (elementText.equalsIgnoreCase(elementTextForClick)) {
                    new WebDriverWait(driver, Duration.ofSeconds(60))
                        .until(ExpectedConditions.elementToBeClickable(element)).click();
                    logs.info("INFO: Selected " + elementTextForClick + " from dropdown");
                    elementFound = true;
                    break;
                }
            } catch (StaleElementReferenceException e) {
                logs.info("INFO: Stale element reference exception occurred. Trying again...");
            }
        }

        if (!elementFound) {
            logs.info("INFO: " + elementTextForClick + " option is not present in the dropdown");
        }
    }


    public void editAndDeleteIconButton(String elementText, String iconValue) {
        List<WebElement> list = driver.findElements(
            By.xpath("//div[@data-field='STATUS']//span[text()='" + elementText + "']"));
        List<WebElement> elementsList1 = driver.findElements(By.xpath(
            "//span[text()='" + elementText + "']/../../following-sibling::div//*[@data-testid='"
                + iconValue + "']"));
        String text = null;
        for (WebElement ele : list) {
            text = ele.getText();
            break;
        }
        if (text.contentEquals(elementText)) {
            for (int j = 0; j < elementsList1.size(); j++) {
                elementsList1.get(j).click();
                break;
            }
        } else {
            logs.testStep("INFO:VACANT status is not present on list");
        }
    }

    public void selectOptionsFromDropdownByValue(String locatorValue, String value) {
        Select drp = new Select(find(locatorValue));
        drp.selectByValue(value);
        logs.testStep("INFO : Select " + value + " From Dropdown");
    }

    public void selectOptionsFromDropdownByIndex(String locatorValue, int index) {
        Select drp = new Select(find(locatorValue));
        drp.selectByIndex(index);
        logs.testStep("INFO : Select " + index + " Index From Dropdown");
    }

    public void selectOptionsFromDropdownByVisibleText(String locatorValue, String visibleText) {
        Select drp = new Select(find(locatorValue));
        drp.selectByVisibleText(visibleText);
        logs.testStep("INFO : Select " + visibleText + " From Dropdown");
    }

    public void deselectAllOptionsFromDropdown(String locatorValue) {
        Select drp = new Select(find(locatorValue));
        boolean multipleSelectedDropDown = drp.isMultiple();
        if (multipleSelectedDropDown) {
            drp.deselectAll();
            logs.testStep("INFO : All options are DeSelected..");
        } else {
            logs.testStep("INFO : This Dropdown is not a multiSelected DropDown.");
        }

    }

    public void deselectOptionsFromDropdownUsingIndex(String locatorValue, int index) {
        Select drp = new Select(find(locatorValue));
        drp.deselectByIndex(index);
        logs.testStep("INFO : De-Select " + index + " From Dropdown");
    }

    public void deselectOptionsFromDropdownUsingValue(String locatorValue, String value) {
        Select drp = new Select(find(locatorValue));
        drp.deselectByValue(value);
        logs.testStep("INFO : De-Select " + value + " From Dropdown");
    }

    public void deselectOptionsFromDropdownUsingVisibleText(String locatorValue, String text) {
        Select drp = new Select(find(locatorValue));
        drp.deselectByVisibleText(text);
        logs.testStep("INFO : De-Select " + text + " From Dropdown");
    }

    public void getAllSelectedOptionsFromDropdown(String locatorValue) {
        Select drp = new Select(find(locatorValue));
        List<WebElement> allOptions = drp.getAllSelectedOptions();
        for (WebElement option : allOptions) {
            logs.testStep("INFO : Selected Options are : " + option.getText());
        }
    }

    public void performScrollToElement(String locatorValue) {
        new Actions(driver)
            .scrollToElement(find(locatorValue))
            .perform();
    }

    public void performHorizontalScrollToElement(String locatorValue, int scrollAmount) {
        int deltaYCoordinate = find(locatorValue).getRect().y;
        new Actions(driver)
            .scrollByAmount(scrollAmount, deltaYCoordinate)
            .perform();
    }

    public void performScrollByAmount(int x, int y) {
        new Actions(driver)
            .scrollByAmount(x, y)
            .perform();
    }

    public void performScrollFromElementByAmount(String locatorValue, int x, int y) {
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement(
            find(locatorValue));
        new Actions(driver)
            .scrollFromOrigin(scrollOrigin, x, y)
            .perform();
    }

    public void selectSingleValueFromMultipleElement(String element) {
        List<WebElement> elementList = findMultipleElements(element);
        for (WebElement ele : elementList) {
            ele.click();
            break;
        }
    }

    public void clickOnEscapeButton() {
        Actions action = new Actions(driver);
        action.sendKeys(Keys.ESCAPE).perform();
    }

    public void uploadFileUsingSendKeys(String element, String fileLocation) {
        WebElement locator = find(element);
        locator.sendKeys(fileLocation);
    }

    public String selectDate(String selectDateType, String dateFormat) throws ParseException {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        String date = null;
        SimpleDateFormat targetDateFormat = new SimpleDateFormat(dateFormat);
        Date formattedTargetDate;

        switch (selectDateType) {
            case "Current date":
                date = dateObj.format(formatter);
                logs.testStep("Original date: " + date);
                targetDateFormat.setLenient(false);
                formattedTargetDate = targetDateFormat.parse(date);
                break;
            case "Random date":
                LocalDate newDateObj = dateObj.plusDays(5);
                String newDate = newDateObj.format(formatter);
                logs.testStep("Updated date: " + newDate);
                date = newDate;
                formattedTargetDate = targetDateFormat.parse(date);
                break;
            case "Future date":
                LocalDate futureDate = dateObj.plusDays(7);
                String newFutureDate = futureDate.format(formatter);
                logs.testStep("Updated date: " + newFutureDate);
                date = newFutureDate;
                formattedTargetDate = targetDateFormat.parse(date);
                break;
            default:
                // Handle invalid selectDateType
                return null;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formattedTargetDate);

            int targetDay = calendar.get(Calendar.DAY_OF_MONTH);
            int targetMonth = calendar.get(Calendar.MONTH) + 1;
            int targetYear = calendar.get(Calendar.YEAR);

            logs.testStep("INFO: Targeted Date is: " + targetDay);
            logs.testStep("INFO: Targeted Month is: " + targetMonth);
            logs.testStep("INFO: Targeted Year is: " + targetYear);

            String actualMonthAndYear = find("yearAndMonthTextInCalendar").getText();
            SimpleDateFormat actualMonthAndYearFormat = new SimpleDateFormat("MMM yyyy");
            calendar.setTime(actualMonthAndYearFormat.parse(actualMonthAndYear));

            int actualMonth = calendar.get(Calendar.MONTH) + 1;
            int actualYear = calendar.get(Calendar.YEAR);

            while (targetMonth < actualMonth || targetYear < actualYear) {
                find("previousButtonOnCalendar").click();
                actualMonthAndYear = find("yearAndMonthTextInCalendar").getText();
                calendar.setTime(actualMonthAndYearFormat.parse(actualMonthAndYear));
                actualMonth = calendar.get(Calendar.MONTH) + 1;
                actualYear = calendar.get(Calendar.YEAR);
            }

            while (targetMonth > actualMonth || targetYear > actualYear) {
                find("nextButtonOnCalendar").click();
                actualMonthAndYear = find("yearAndMonthTextInCalendar").getText();
                calendar.setTime(actualMonthAndYearFormat.parse(actualMonthAndYear));
                actualMonth = calendar.get(Calendar.MONTH) + 1;
                actualYear = calendar.get(Calendar.YEAR);
            }

            String targetDayString = String.valueOf(targetDay);
            String xpath =
                "//div[@class='react-datepicker__month']/div/div[text()='" + targetDayString + "']";
            driver.findElement(By.xpath(xpath)).click();

        } catch (ParseException e) {
            logs.testStep("Selected date is invalid or not selectable");
            e.printStackTrace();
        }

        return date;
    }


    public void selectNumberOfGuest(String selectGuest, int selectAdultValue, int selectChildValue,
        String clickOnPlusIcon) {
        By adultPlusIconLocator = By.xpath(
            "(//div[contains(@class,'MuiPaper-root')]/div/div/p/../div/../div/../../div/../div/../div//div//..//div//*[@data-testid='"
                + clickOnPlusIcon + "'])[1]");
        By childPlusIconLocator = By.xpath(
            "(//div[contains(@class,'MuiPaper-root')]/div/div/p/../div/../div/../../div/../div/../div//div//..//div//*[@data-testid='"
                + clickOnPlusIcon + "'])[2]");

        switch (selectGuest) {
            case "Adult":
                clickMultipleTimes(adultPlusIconLocator, selectAdultValue);
                break;
            case "Child":
                clickMultipleTimes(childPlusIconLocator, selectChildValue);
                break;
            case "Adult and Child":
                clickMultipleTimes(adultPlusIconLocator, selectAdultValue);
                clickMultipleTimes(childPlusIconLocator, selectChildValue);
                break;
        }
    }

    public void clickMultipleTimes(By locator, int targetCount) {
        int currentCount = 0;

        while (currentCount < targetCount) {
            List<WebElement> elements = driver.findElements(locator);
            int remainingClicks = targetCount - currentCount;
            int clicks = Math.min(elements.size(), remainingClicks);

            for (int i = 0; i < clicks; i++) {
                elements.get(i).click();
                currentCount++;
            }
        }
    }


    public List<String> getRoomAvailability() {
        List<String> availabilityList = new ArrayList<>();
        try {
            List<WebElement> elements = findMultipleElements("listOfRoomAvailability");
            for (WebElement element : elements) {
                String availability = element.getText();
                availabilityList.add(availability);
                logs.testStep(availability);

            }
            logs.testStep(availabilityList.toString());
        } catch (NoSuchElementException e) {
            logs.testStep("ERROR: Element not found. Unable to retrieve room availability.");
            e.printStackTrace();
        } catch (Exception e) {
            logs.testStep(
                "ERROR: An unexpected error occurred while retrieving room availability.");
            e.printStackTrace();
        }

        return availabilityList;
    }

    public int generateRandomNumber() {
        Random rand = new Random();
        int randomNum = 0;

        for (int i = 1; i <= 100; i++) {
            randomNum = rand.nextInt((999 - 100) + 1) + 100;
        }

        return randomNum;
    }

    public String getCurrentDate() throws ParseException {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = dateObj.format(formatter);
        logs.testStep("Original date: " + date);
        return date;
    }

    public String getFutureDate(String selectDateType) throws ParseException {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        switch (selectDateType) {
            case "Random date":
                LocalDate newDateObj = dateObj.plusDays(5);
                String newDate = newDateObj.format(formatter);
                logs.testStep("Updated date: " + newDate);
                return newDate;
            case "Future date":
                LocalDate futureDate = dateObj.plusDays(7);
                String newFutureDate = futureDate.format(formatter);
                logs.testStep("Updated date: " + newFutureDate);
                return newFutureDate;
            default:
                throw new IllegalArgumentException("Invalid selectDateType: " + selectDateType);
        }

    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public static void scrollUntilElementVisible(WebDriver driver, Direction direction, String targetedElement) {
        int maxScrollAttempts = 15;  // Maximum number of scroll attempts
        int scrollAttempt = 0;  // Current scroll attempt count
        Wait wait = new Wait(driver);
        wait.implicitWaitForPage();
        while (driver.findElements(By.xpath("//*[@text='" + targetedElement + "']")).size() == 0
            && scrollAttempt < maxScrollAttempts) {
            swipeScreen(driver, direction);
            scrollAttempt++;
        }
    }

    public static void swipeScreen(WebDriver driver, Direction direction) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = size.height / 2;
        int endX = startX;
        int endY = startY;
        switch (direction) {
            case UP:
                endY = (int) (size.height * 0.25);
                break;
            case DOWN:
                endY = (int) (size.height * 0.75);
                break;
            case LEFT:
                startX = (int) (size.width * 0.75);
                endX = (int) (size.width * 0.25);
                break;
            case RIGHT:
                endX = (int) (size.width * 0.75);
                startX = (int) (size.width * 0.25);
                break;
        }
        swipe(driver, startX, startY, endX, endY, 300);
    }

    public static void swipe(WebDriver driver, int startX, int startY, int endX, int endY, int duration) {
        TouchAction touchAction = new TouchAction((PerformsTouchActions) driver);
        touchAction.press(PointOption.point(startX, startY))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
            .moveTo(PointOption.point(endX, endY))
            .release()
            .perform();
    }

    public boolean isKeyboardDisplayed(AppiumDriver driver) {
        if (driver instanceof AndroidDriver) {
            return ((AndroidDriver) driver).isKeyboardShown();
        } else if (driver instanceof IOSDriver) {
            return ((IOSDriver) driver).isKeyboardShown();
        } else {
            throw new UnsupportedOperationException(
                "Keyboard verification is not supported for the current platform");
        }
    }

    public boolean closeKeyboard(AppiumDriver driver) {
        // Close the keyboard using the Appium-specific keyboard capability
        if (driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) driver;
            try {
                androidDriver.hideKeyboard();
                return true; // Keyboard successfully closed
            } catch (Exception e) {
                return false; // Failed to close the keyboard
            }
        }
        return false; // Keyboard closure not supported for the driver type
    }

}
