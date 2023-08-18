package web.tests.banco;

import bradesco.custodia.bc2.framework.init.Base;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import web.objectrepository.banco.BancoLocators;
import org.testng.annotations.Test;


public class Banco extends Base {
    BancoLocators banco;

    @Test
    public void bancoTestScenarioOne(){
        banco = new BancoLocators(driver);
        driver.manage().window().maximize();
        banco.enterTextAtUserNameField("EZI26798");
        banco.enterTextAtUserPasswordField("PHwC*U*8");
        banco.clickOnavançarButton();
        banco.clickOnCustódiaButton();
        WebElement mainFrame = driver.findElement(By.xpath("//iframe[@id=\"paginaCentral\"]"));

        //Switching Frame
        WebElement iFrame = driver.findElement(By.xpath("//iframe[@id=\"paginaCentral\"]"));
        driver.switchTo().frame(iFrame);
//        driver.findElement(By.xpath("//h3[contains(text(),\"Relatórios\")]")).click();
        banco.clickOnRelatóriosDropDownButton();
        banco.clickOnPosiçãoDropDownButton();
        banco.clickOnCarteiraDiáriaButton();
        banco.clickOnsearchButton();
        driver.switchTo().defaultContent();


        //Switching Frame
        WebElement iFramePopUp = driver.findElement(By.xpath("//iframe[@id=\"modal_infra_estrutura\"]"));
        driver.switchTo().frame(iFramePopUp);
        banco.clickOnpesquisarButton();
        banco.clickOn8532Button();

        driver.switchTo().defaultContent();
        driver.switchTo().frame(iFrame);

 /*       banco.enterTextAtCarteiraText("cash");
        banco.clickOnCarteiraTextLink();*/
        banco.enterTextAtdayTextBox("31");
        banco.enterTextAtmonthTextBox("07");
        banco.clickOngerarRelatórioButton();
        String fileName = banco.clickOnconsultarProtocoloButton();
        banco.clickOnConsultarButton();
        banco.clickToDownloadFile1(fileName);
    }

}
