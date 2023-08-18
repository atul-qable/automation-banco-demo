package bradesco.custodia.bc2.performaction.automob;

import bradesco.custodia.bc2.framework.readers.JsonFileReader;
import bradesco.custodia.bc2.framework.runner.TestRunner;
import bradesco.custodia.bc2.performaction.autoweb.Element;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public class Device {
    private AppiumDriver driver;
    private Element element;
    private JsonFileReader reader = new JsonFileReader();
    private Duration stepDuration = Duration.ofMillis(1);
    private Duration noTime = Duration.ofMillis(0);
    private PointerInput.Origin view = PointerInput.Origin.viewport();

    public Device(AppiumDriver driver) {
        this.driver = driver;
        element = new Element(driver);
    }

    public void switchToContext(String contextName) {
        if (reader.getPlatform(TestRunner.currentConfig).equalsIgnoreCase("android")) {
            ((AndroidDriver) driver).context(contextName);
        }
        if (reader.getPlatform(TestRunner.currentConfig).equalsIgnoreCase("iOS")) {
            ((IOSDriver) driver).context(contextName);
        }
    }
    public void scrollDown() {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        int startY = (int) (driver.manage().window().getSize().getHeight() * 0.80);
        int endY = (int) (driver.manage().window().getSize().getHeight() * 0.20);
        int startX = (int) (driver.manage().window().getSize().getWidth() * 0.50);
        Sequence circle = new Sequence(finger, 0);
        circle.addAction(finger.createPointerMove(noTime, view, startX, startY));
        circle.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        for (int i = 1; i < 300 + 1; i++) {
            circle.addAction(finger.createPointerMove(stepDuration, view, startX, startY - (i + 10)));
        }
        driver.perform(Arrays.asList(circle));
    }

    public void slider(String elementName) {
        WebElement slider = element.find(elementName);
        Point source = slider.getLocation();
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);
        sequence.addAction(finger.createPointerMove(ofMillis(0),
                PointerInput.Origin.viewport(), source.x, source.y));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));
        sequence.addAction(new Pause(finger, ofMillis(600)));
        sequence.addAction(finger.createPointerMove(ofMillis(600),
                PointerInput.Origin.viewport(), source.x + 400, source.y));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
        driver.perform(singletonList(sequence));
    }

}
