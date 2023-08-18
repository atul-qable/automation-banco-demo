package bradesco.custodia.bc2.performaction.autoweb;

import bradesco.custodia.bc2.framework.exception.ListSizeNotMatchingException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Verify {
    private final WebDriver driver;
    private final Element element;
    private final TestNGLogs logs = new TestNGLogs();

    private final Wait wait;



    public Verify(WebDriver driver) {
        this.driver = driver;
        element = new Element(driver);
        wait = new Wait(driver);
    }

    public void elementIsPresent(String elementName) {
        wait.implicitWaitForPage();
        boolean bool = false;
        logs.testStep("Verify " + elementName + " is displayed.");
        try {
            if (element.find(elementName).isDisplayed()) {
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(bool).isTrue();
        logs.testResult(bool);
    }

    public boolean checkElementIsPresent(String elementName) {
        wait.implicitWaitForPage();
        boolean bool = false;
        try {
            if (element.find(elementName).isDisplayed()) {
                bool = true;
            }
        } catch (Exception ignored) {
        }

        return bool;
    }

    public void elementIsEnabled(String elementName) {
        logs.testStep("Verify " + elementName + " is enabled.");
        boolean bool = false;
        try {
            if (element.find(elementName).isEnabled()) {
                bool = true;
            }
        } catch (Exception ignored) {
        }
        assertThat(bool).isTrue();
        logs.testResult(bool);
    }

    public void elementIsSelected(String elementName) {
        logs.testStep("Verify " + elementName + " is selected.");
        boolean bool = false;
        try {
            if (element.find(elementName).isSelected()) {
                bool = true;
            }
        } catch (Exception ignored) {
        }
        assertThat(bool).isTrue();
        logs.testResult(bool);
    }

    public void currentTitleIsEqualTo(String title) {
        logs.testStep("Verify current page title is " + title);
        assertThat(driver.getTitle()).isEqualTo(title);
        logs.testResult(true);
    }

    public void elementTextIsEqualTo(String elementName, String textToVerify) {
        logs.testStep("Verify element " + elementName + " text is equal to " + textToVerify);
        logs.testStep("Get element Text is"+element.find(elementName).getText().trim());
        assertThat(element.find(elementName).getText().trim()).isEqualTo(textToVerify);
        logs.testResult(true);
    }

    /**
     *
     * @param elementName name of the element from json file
     * @param attributeName attribute that you wanted to compare
     * @param expectedAttributeValue attribute value that you wanted to compare
     */
    public void verifyElementAttributeIsEqualTo(String elementName, String attributeName, String expectedAttributeValue) {
        logs.testStep("Verify element " + elementName + " attribute " + attributeName + " text is equal " + expectedAttributeValue);
        assertThat(element.find(elementName).getAttribute(attributeName)).isEqualTo(expectedAttributeValue);
        logs.testResult(true);
    }

    public void verifyElementTextIsAvailableInList(String elementName, String textToVerify) {
        wait.implicitWaitForPage();
        logs.testStep("Verify text " + textToVerify + " is available in " + elementName);
        List<WebElement> elementsList = element.findMultipleElements(elementName);
        boolean isTextAvailable = false;

        for (WebElement element : elementsList) {
            if (element.getText().contains(textToVerify)) {
                isTextAvailable = true;
            }
        }

        assertThat(isTextAvailable).isTrue();
        logs.testResult(true);
    }

    public ArrayList<String> convertWebElementToStringList(List<WebElement> elementsList) {
        ArrayList<String> actualList = new ArrayList<>();

        for (WebElement element : elementsList) {
            actualList.add(element.getText());
        }

        return actualList;
    }

    public void verifyElementTextIsInAlphabeticalOrder(String elementName, ArrayList<String> expectedList) throws ListSizeNotMatchingException {
        List<WebElement> elementsList = element.findMultipleElements(elementName);
        ArrayList<String> actualList = convertWebElementToStringList(elementsList);

        boolean isListInAlphabeticalOrder = false;

        Collections.sort(actualList);

        if (actualList.size() == expectedList.size()) {
            for (int i = 0; i < actualList.size(); i++) {
                if (actualList.get(i).equalsIgnoreCase(expectedList.get(i))) {
                    isListInAlphabeticalOrder = true;
                } else {
                    isListInAlphabeticalOrder = false;
                    break;
                }
            }
        } else {
            throw new ListSizeNotMatchingException("Element List on page is not matching with the Given List");
        }

        assertThat(isListInAlphabeticalOrder).isTrue();
    }

    public boolean isElementActive(String elementName) {
        wait.implicitWaitForPage();
        WebElement ele = element.find(elementName);
        boolean bool = false;
        if (ele.equals(driver.switchTo().activeElement())) {
            bool = true;
            logs.testStep("Selected element is focused");
            assertThat(bool).isTrue();
            logs.testResult(bool);
        } else {
            bool = false;
            logs.testStep("Selected element is not focused");
            logs.testResult(bool);
        }
        return bool;
    }

    public boolean isButtonClickable(String buttonLocator) {
        try {
            wait.implicitWaitForPage();
            WebElement button = element.find(buttonLocator);
            logs.testStep("Selected"  + buttonLocator + " is clickable");
            return button.isEnabled() && button.isDisplayed();
        } catch (Exception e) {
            logs.testStep("Selected " + buttonLocator + " is not clickable");
            return false;
        }
    }


    public void verifyExistingValueAndDeleteOnTheTableList(String firstValue, String secondValue,String selectTableValue){
        List<WebElement>elements=driver.findElements(By.xpath("//div[@data-field='"+selectTableValue+"']//p"));
        for (int i=0;i<elements.size();i++){
            if(elements.get(i).getText().equalsIgnoreCase(firstValue)||elements.get(i).getText().equalsIgnoreCase(secondValue)){
                int newValue =i+1;
                driver.findElement(By.xpath("(//*[@data-testid='DeleteOutlineIcon'])[" +newValue+ "]")).click();
                if(isDisplayed("checkBoxMessageOnDeleteBlockPopup")==true){
                    element.click("checkBoxOnDeleteRoomTypePopup");
                }
                element.click("deleteButton");
            }else{
                logs.testStep("INFO:" + firstValue + " " + "is not present on Ui");
                logs.testStep("INFO:" + secondValue + " " + "is not present on Ui");
            }
        }
    }
    public boolean isDisplayed(String elementName) {
        try {
            logs.testStep("Step:: Test");
            wait.implicitWaitForPage();
            WebElement ele =element.find(elementName);
            return ele.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean verifyExistingRoomAndDelete(String firstRoomName, String secondRoomName, String roomsNameElement) {
        try {
            wait.implicitWaitForPage();
            List<WebElement> getRoomList = element.findMultipleElements(roomsNameElement);
            for (WebElement room : getRoomList) {
                String roomName = room.getText();
                logs.testStep(roomName);
                if (roomName.equalsIgnoreCase(firstRoomName) || roomName.equalsIgnoreCase(secondRoomName)) {
                    WebElement kebabMenuIcon = driver.findElement(By.xpath("//p[text()='" + roomName + "']/../../..//button[@id='type-card-action-button']"));
                    kebabMenuIcon.click();
                    boolean isDisplayed = room.isDisplayed();
                    Assert.assertTrue(isDisplayed);
                    return true;
                }
            }
            logs.testStep("On UI, " + firstRoomName + " and " + secondRoomName + " are not present");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifyFilterValueIsDisplayed(String elementName, String elementText) {
        wait.implicitWaitForPage();
        List<WebElement> elementsList = element.findMultipleElements(elementName);
        boolean isDisplayed = false;

        for (int i = 0; i < elementsList.size(); i++) {
            if (elementsList.get(i).getText().equalsIgnoreCase(elementText)) {
                logs.info(elementsList.get(i).getText());
                isDisplayed = elementsList.get(i).isDisplayed();
                Assert.assertTrue(isDisplayed);
                logs.info("INFO: " + elementText + " is displayed on UI");
                return true;
            }
        }

        logs.info("INFO: " + elementText + " is not displayed on UI");
        if (elementsList.isEmpty()) {
            logs.info("INFO: No results found");
            driver.findElement(By.xpath("(//div[@class='css-wuw9o9']//p)[1]")).isDisplayed();
        }

        return false;
    }


    public boolean checkTextIsPresent(String elementName, String enterText) {
        wait.implicitWaitForPage();
        boolean bool = false;
        try {
            if (element.find(elementName).getText().equals(enterText)) {
                bool = true;
            }
        } catch (Exception e) {
            System.err.println("An exception occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return bool;
    }

    public boolean isButtonDisabled(String buttonLocator) {
        wait.implicitWaitForPage();
        WebElement button =  element.find(buttonLocator);
        return !button.isEnabled();
    }

    public boolean isSelected(String locatorName) {
        wait.implicitWaitForPage();
        WebElement locator = element.find(locatorName);
        boolean checkboxIsSelected = locator.isSelected() || "true".equals(locator.getAttribute("value"));

        if (checkboxIsSelected) {
            logs.testStep("Element " + locatorName + " is selected");
        } else {
            logs.testStep("Element " + locatorName + " is not selected");
        }

        return checkboxIsSelected;
    }

    public  boolean isElementFocusable(String locatorName) {
        wait.implicitWaitForPage();
        String focusableValue = element.getAttributeValue(locatorName,"focusable");
        return focusableValue != null && focusableValue.equals("true");
    }

    public boolean isElementClickable(String locatorName) {
        wait.implicitWaitForPage();
        String clickableValue = element.getAttributeValue(locatorName, "clickable");
        return clickableValue != null && clickableValue.equals("true");
    }

    public boolean isWindows(){
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

}
