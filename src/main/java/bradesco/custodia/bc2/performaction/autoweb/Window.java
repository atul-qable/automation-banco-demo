package bradesco.custodia.bc2.performaction.autoweb;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

public class Window {

    private WebDriver driver;
    private Element element;

    public Window(WebDriver driver) {
        this.driver = driver;
        this.element = new Element(driver);
    }

    public String getCurrentWindowHandle(String elementName) {
        return driver.getWindowHandle();
    }

    public boolean findNewWindowAndSwitch(String oldWindow) {
        boolean switched = false;
        for (String windowHandle : driver.getWindowHandles()) {
            if (!oldWindow.equals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                switched = true;
                break;
            }
        }
        return switched;
    }

    public void createNewAndSwitch() {
        driver.switchTo().newWindow(WindowType.WINDOW);
    }

    public void createNewTabAndSwitch() {
        driver.switchTo().newWindow(WindowType.TAB);
    }

    public void closeWindow() {
        driver.close();
    }

    public void switchToWindowAndClose(String originalWindow) {
        driver.switchTo().window(originalWindow);
    }

    public void switchToIframe(WebElement element) {
        driver.switchTo().frame(element);
    }

    public void switchToIframe(String elementName) {
        driver.switchTo().frame(elementName);
    }

    public void switchToIframe(int elementIndex) {
        driver.switchTo().frame(elementIndex);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    public int getWindowWidth() {
        return driver.manage().window().getSize().getWidth();
    }

    public int getWindowHeight() {
        return driver.manage().window().getSize().getHeight();
    }

    public void setWindowSize(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    public void maximizeWindow() {
        driver.manage().window().maximize();
    }

    public void minimizeWindow() {
        driver.manage().window().minimize();
    }

    public void fullScreenWindow() {
        driver.manage().window().fullscreen();
    }
}
