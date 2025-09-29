package courtscraper.flows.retirevedockets;

import courtscraper.FlowStart;
import courtscraper.datamanagement.json.JSONGrabbers;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RetrieveDocketsMain extends FlowStart {
    public static void retrieveDocketFlow() throws IOException, InterruptedException {
        courtLinkLogin(); //login process
        RetrieveDocketsSearchConfig.retrieveDocketsTermInputs();
        Thread.sleep(1000);
    }


    private static void courtLinkLogin() throws FileNotFoundException, InterruptedException {
        driver.get("https://signin.lexisnexis.com/lnaccess/app/signin?back=https%3A%2F%2Fadvance.lexis.com%3A443%2Fcourtlinkhome&aci=la");
        String[] credentials = new JSONGrabbers().loginGrabber("Courtlink");

        driver.findElement(By.xpath("//*[@id=\"userid\"]")).sendKeys(credentials[0]);
        driver.findElement(By.xpath("//*[@id=\"signInSbmtBtn\"]")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        // Wait for the element to be present in the DOM
        WebElement passwordInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='password']"))
        );
        // Now interact with it
        passwordInput.sendKeys(credentials[1]);
        driver.findElement(By.xpath("//*[@id=\"next\"]")).click();
    }
}
