package courtscraper.flows.retirevedockets;

import courtscraper.helpers.guiinputprocessors.ProcessRetrieveDocketsInputs;

import java.io.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class RetrieveDocketsSearchConfig extends RetrieveDocketsMain {
    static String[] retrieveDocketsProcessedInputs;
    public static void retrieveDocketsTermInputs() throws InterruptedException {

        Thread.sleep(1000);

        retrieveDocketsProcessedInputs = new ProcessRetrieveDocketsInputs().grabRetrieveDocketsInputs();

        retrieveDocketsSearchInput(); //enter search terms

        retrieveDocketsSelectState(); //select state
        driver.findElement(By.xpath("//button[@id='triggersearch']")).click();

        enterDate(retrieveDocketsProcessedInputs[2] , retrieveDocketsProcessedInputs[3]);
        openByLink();

    }
    private static void openByLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String mainWindow = driver.getWindowHandle(); // Store main/original tab handle

        // --- Load already processed titles ---
        Set<String> processedTitles = loadOpenedTitles(OPENED_DOCUMENT_CSV_PATH);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> allLis = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("div.wrapper ol li.usview")));
        System.out.println("Total li.usview found: " + allLis.size());

        for (int i = 0; i < allLis.size(); i++) {
            try {
                // Re-fetch elements to avoid stale element issue
                List<WebElement> refreshedLis = driver.findElements(By.cssSelector("div.wrapper ol li.usview"));
                WebElement li = refreshedLis.get(i);

                WebElement link = li.findElement(By.cssSelector("h2.doc-title a"));
                String linkTitle = link.getText().trim();

                // ✅ Skip if already processed before
                if (processedTitles.contains(linkTitle)) {
                    System.out.println("Skipping already processed: " + linkTitle);
                    continue;
                }

                System.out.println("Opening: " + linkTitle);

                // Scroll into view
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", link);

                // Open in new tab using CTRL + Click
                Actions actions = new Actions(driver);
                actions.keyDown(Keys.CONTROL).click(link).keyUp(Keys.CONTROL).build().perform();

                // Wait for new tab
                Thread.sleep(1500);

                // Scrape the tab and check if successful
                boolean success = scrapeAndCloseTab(mainWindow);

                // ✅ Only mark as processed if scrape was successful
                if (success) {
                    appendTitleToCSV(OPENED_DOCUMENT_CSV_PATH, linkTitle);
                    processedTitles.add(linkTitle);
                    System.out.println("✔ Successfully processed: " + linkTitle);
                } else {
                    System.out.println("❌ Skipping CSV entry — scrape failed for: " + linkTitle);
                }

            } catch (Exception e) {
                System.out.println("Skipping li index " + i + " -> " + e.getMessage());
            }
        }
    }


    /**
     * Switch to the latest opened tab, scrape data, close it, and return to main tab.
     */
    private static boolean scrapeAndCloseTab(String mainWindow) {
        boolean success = false;
        try {
            // Find new tab handle
            Set<String> handles = driver.getWindowHandles();
            for (String handle : handles) {
                if (!handle.equals(mainWindow)) {
                    driver.switchTo().window(handle);

                    // Perform scraping logic
                    int clickedCount = clickCheckboxesForProceedingText("Rejection");
                    if (clickedCount > 0) {
                        clickRetrieveDocumentButton();
                        Thread.sleep(500);
                        clickGetDocumentsButton();
                    }
                    success = true; // ✅ Only mark as successful if buttons were clicked
                    System.out.println("Scraping Title: " + driver.getTitle());
                    String bodyText = driver.findElement(By.tagName("body")).getText();
                    System.out.println("Page length: " + bodyText.length());

                    // Close new tab and switch back
                    driver.close();
                    driver.switchTo().window(mainWindow);
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error during scraping: " + e.getMessage());
        }

        return success;
    }


    private static void retrieveDocketsSearchInput() {
    // Create WebDriverWait object (adjust timeout as needed, e.g., 10 seconds)
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    // Wait until the element is visible and interactable
    WebElement keywordInput =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='keywords']")));

    // Now send keys
    keywordInput.sendKeys(retrieveDocketsProcessedInputs[0]);
  }


    private static void retrieveDocketsSelectState () throws InterruptedException {
        driver.findElement(By.xpath("/html/body/main/div/ln-courtlinksearchform/div/searchform/div[1]/div[1]/courtlist/div/courtlistselector/div/div[1]/span[1]/div")).click();
        searchState(retrieveDocketsProcessedInputs[4]);
        Thread.sleep(1000);
    }

    private static void searchState(String stateName){

        driver.findElement(By.xpath("//*[@id=\"courtsdropdown\"]")).sendKeys(stateName);
        driver.findElement(By.xpath("/html/body/main/div/ln-courtlinksearchform/div/searchform/div[1]/div[1]/courtlist/div/courtlistselector/div/div[2]/div/div[2]/div/div/div/div/label/div")).click();

    }

    private static void enterDate(String minDate, String maxDate) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    if (!minDate.isEmpty()) {
      // Locate input inside min-picker
      WebElement minDateInput =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".min-picker input")));
      minDateInput.sendKeys(Keys.CONTROL + "a"); // Select all
      minDateInput.sendKeys(Keys.DELETE);
      minDateInput.sendKeys(minDate);
    }
    if (!maxDate.isEmpty()) {
      // Locate input inside max-picker
      WebElement maxDateInput =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".max-picker input")));
      maxDateInput.sendKeys(Keys.CONTROL + "a"); // Select all
      maxDateInput.sendKeys(Keys.DELETE);
      maxDateInput.sendKeys(maxDate);
    }
    if (!minDate.isEmpty() || !maxDate.isEmpty()) {
      driver.findElement(By.className("save")).click();
    }
    }
    public static int clickCheckboxesForProceedingText(String keyword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Wait for and scroll to proceedings div
        WebElement proceedingsDiv = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("Proceedings")));
        js.executeScript("arguments[0].scrollIntoView(true);", proceedingsDiv);

        // Brief pause for scroll completion
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Get all table rows
        List<WebElement> allRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#Proceedings tbody tr")));

        System.out.println("Total rows found: " + allRows.size());

        int clickedCount = 0;

        // Iterate through each row and check for keyword
        for (WebElement row : allRows) {
            try {
                // Find the proceeding text cell in current row
                WebElement proceedingTextCell = row.findElement(
                        By.cssSelector("td[data-proceedingentryname='proceedingtext']"));

                String cellText = proceedingTextCell.getText().trim();
                System.out.println("Checking row text: " + cellText);

                // Check if text contains the keyword (case-insensitive)
                if (cellText.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.println("Match found: " + cellText);

                    // Find and click the checkbox in this row
                    WebElement checkbox = row.findElement(
                            By.cssSelector("input[type='checkbox'].SS_ProceedingLink"));

                    if (!checkbox.isSelected()) {
                        checkbox.click();
                        clickedCount++;
                        System.out.println("Clicked checkbox for: " + cellText);
                    }
                }
            } catch (Exception e) {
                // Skip rows that don't have the expected structure
                System.out.println("Skipping row due to missing elements: " + e.getMessage());
                continue;
            }

        }

        return clickedCount;
    }
    public static void clickRetrieveDocumentButton() {
        try {
            // Create WebDriverWait with 15 seconds timeout
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Wait for the element to be clickable using multiple locator strategies
            WebElement retrieveButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[data-buttontype='retrievedocument'][data-action='retrieveproceedings']")
            ));

            // Click the element
            retrieveButton.click();

            System.out.println("Successfully clicked Retrieve Document(s) button");

        } catch (Exception e) {
            System.err.println("Failed to click Retrieve Document(s) button: " + e.getMessage());
            throw e;
        }
    }
    public static void clickGetDocumentsButton() {
        try {
            // Create WebDriverWait with 15 seconds timeout
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Wait for the button to be clickable using XPath with exact text match
            WebElement getDocumentsButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Get Documents']")
            ));

            // Click the button
            getDocumentsButton.click();

            System.out.println("Successfully clicked Get Documents button");

        } catch (Exception e) {
            System.err.println("Failed to click Get Documents button: " + e.getMessage());
            throw e;
        }
    }
    private static Set<String> loadOpenedTitles(String filePath) {
        Set<String> titles = new HashSet<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return titles; // empty set if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                titles.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading opened tabs CSV: " + e.getMessage());
        }

        return titles;
    }

    private static void appendTitleToCSV(String filePath, String title) {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(title);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to opened tabs CSV: " + e.getMessage());
        }
    }

    static String OPENED_DOCUMENT_CSV_PATH = "retrieve_dockets_opened_tabs.csv";
}
