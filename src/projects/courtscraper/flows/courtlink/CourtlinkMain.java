package courtscraper.flows.courtlink;

import courtscraper.FlowStart;
import courtscraper.datamanagement.json.JSONGrabbers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;

import static courtscraper.flows.courtlink.courtlinkscraper.CourtlinkScrapeMain.courtLinkScrape;
import static courtscraper.flows.courtlink.courtlinksearchconfig.CourtlinkSearchConfigMain.courtLinkTermInputs;

public class CourtlinkMain extends FlowStart {

    //main hub for all courtlink processes from login, to search, to scrape

    public static void courtLinkFlow() throws IOException, InterruptedException {
        courtLinkLogin(); //login process

        Thread.sleep(1000);

        courtLinkTermInputs(); //inputs terms/dates into input boxes

        Thread.sleep(1000);

        courtLinkScrape(); //scrapes courtlink for casenumbers


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
