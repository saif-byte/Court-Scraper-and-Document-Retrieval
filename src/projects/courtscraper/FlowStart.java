package courtscraper;

import courtscraper.helpers.JobsManager;
import courtscraper.setups.browser.Firefox;
import courtscraper.setups.gui.mainpanelelements.MainButtons;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

import static courtscraper.flows.courtlink.CourtlinkMain.courtLinkFlow;
import static courtscraper.flows.retirevedockets.RetrieveDocketsMain.retrieveDocketFlow;
import static courtscraper.flows.states.StateParser.stateRetrievalFlow;
import static courtscraper.helpers.TabManagement.closeAllTabs;
import static courtscraper.setups.gui.jobspanelelements.JobsTableBox.updateJobsTable;
import static courtscraper.setups.gui.mainpanelelements.MainComboBoxes.selectedFlowType;


public class FlowStart extends MainButtons {

    //this is the flow start for the entire process, it can be divided up into specific flow configuration based on users input

    public static WebDriver driver;
    private static JobsManager jobsManager;
    private static String runStatus;

    public static void StartMainFlowButton() throws IOException, InterruptedException {
        jobsManager = new JobsManager();
        runStatus = runLogger.updateInfo("runStatus","Finished");

        try {
            new Firefox().FirefoxLaunch();

            switch (selectedFlowType) {
                case "Scrape and Retrieve":
                    courtLinkFlow();
                    closeAllTabs();
                    stateRetrievalFlow();
                    break;
                case "Scrape Only":
                    courtLinkFlow();
                    break;
                case "Retrieve Only":
                    stateRetrievalFlow();
                    break;
            }
        } catch(Exception e) {
            runStatus = runLogger.updateInfo("runStatus", "Failed");
            runLogger.logError(e);
            e.printStackTrace();
        }

        jobsManager.addJob(runStatus);
        updateJobsTable();


    }

    public static void StartRetrieveDocketsFlow() throws IOException, InterruptedException {
        jobsManager = new JobsManager();
        runStatus = runLogger.updateInfo("runStatus","Finished");

        try {
            new Firefox().FirefoxLaunch();

            System.out.println("Retrieve Dockets flow started...");

            retrieveDocketFlow();

        } catch(Exception e) {
            runStatus = runLogger.updateInfo("runStatus", "Failed");
            runLogger.logError(e);
            e.printStackTrace();
        }

        jobsManager.addJob(runStatus);
        updateJobsTable();
    }

}
