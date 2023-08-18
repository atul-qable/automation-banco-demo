package web.objectrepository.banco;

import bradesco.custodia.bc2.performaction.autoweb.Element;
import bradesco.custodia.bc2.performaction.autoweb.Verify;
import bradesco.custodia.bc2.performaction.autoweb.Wait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BancoLocators {
    WebDriver driver = null;
    Element element = null;
    Verify verify = null;
    Wait wait = null;

    public BancoLocators(WebDriver driver) {
        this.driver = driver;
        element = new Element(driver);
        verify = new Verify(driver);
        wait = new Wait(driver);
    }

    public void enterTextAtUserNameField(String textToEnter){
        element.enterText("usernameTextBox",textToEnter);
    }
    public void enterTextAtUserPasswordField(String textToEnter){
        element.enterText("passwordTextBox",textToEnter);
    }
    public void clickOnavançarButton(){
        element.click("avançarButton");
    }
    public void clickOnCustódiaButton(){
        element.click("CustódiaButton");
    }
    public void clickOnRelatóriosDropDownButton(){
        wait = new Wait(driver);
        wait.waitForSeconds(5);
        element.click("RelatóriosDropDownButton");
    }
    public void clickOnPosiçãoDropDownButton(){
        element.click("PosiçãoDropDownButton");
    }
    public void clickOnCarteiraDiáriaButton(){
        element.click("CarteiraDiáriaButton");
    }
    public void enterTextAtCarteiraText(String textToEnter){
        element.enterText("CarteiraText", textToEnter);
        wait = new Wait(driver);
        wait.waitForSeconds(3);
    }
    public void clickOnCarteiraTextLink(){
        element.click("CarteiraTextLink");
    }
    public void clickOnsearchButton(){
        element.click("searchButton");
        wait = new Wait(driver);
        wait.waitForSeconds(6);
    }
    public void clickOnpesquisarButton(){
        element.click("pesquisarButton");
        wait = new Wait(driver);
        wait.waitForSeconds(7);
    }
    public void clickOn8532Button(){
        element.click("8532Button");
        wait = new Wait(driver);
        wait.waitForSeconds(7);
    }
    public void enterTextAtdayTextBox(String textToEnter){
        element.enterText("dayTextBox",textToEnter);
    }
    public void enterTextAtmonthTextBox(String textToEnter){
        element.enterText("monthTextBox",textToEnter);
    }
    public void clickOngerarRelatórioButton(){
        element.click("gerarRelatórioButton");
        wait = new Wait(driver);
        wait.waitForSeconds(5);
    }
    public String clickOnconsultarProtocoloButton(){
        String text = driver.findElement(By.xpath("//span[contains(text(),\"Número de Protocolo: \")]//parent::p//span[2]")).getText();
        System.out.println(text);
        element.click("consultarProtocoloButton");
        wait = new Wait(driver);
        wait.waitForSeconds(8);
        return text;
    }
    public void clickOnConsultarButton(){
        element.click("ConsultarButton");
        wait = new Wait(driver);
        wait.waitForSeconds(4);
    }
    public void clickToDownloadFile1(String fileName){
        String xpathForDownloadingFile = "//a[contains(text(),\""+fileName+"\")]";
        System.out.println(xpathForDownloadingFile);
        driver.findElement(By.xpath("//i[@class=\"ico gradient-red\"]//parent::a//parent::div//input")).sendKeys(fileName);
        try {
            driver.findElement(By.xpath(xpathForDownloadingFile)).click();
        }catch (Exception e){
            clickOnConsultarButton();
        }
        wait.waitForSeconds(20);
    }


}
