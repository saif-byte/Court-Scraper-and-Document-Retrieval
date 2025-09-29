package courtscraper.helpers.guiinputprocessors;

import static courtscraper.setups.gui.retrievedocketspanelelements.RetrieveDocketsPanel.*;

/**
 * This Helper class is for grabbing the inputs from the Retrieve Dockets panel
 * and processing them into usable variables throughout the code.
 */
public class ProcessRetrieveDocketsInputs {

    private String startDate = "";
    private String endDate = "";

    public String[] grabRetrieveDocketsInputs() {

        getDateBox(); // runs getDateBox() to get the dates inputted into the fixed variables above

        String[] inputs = {
                getSearchBox(),
                getDocketSearchBox(),
                startDate,
                endDate,
                getCounty()
        };

        return inputs;
    }

    private String getSearchBox() {
        return searchTerm.getText();
    }

    private String getDocketSearchBox() {
        return docketSearchTerm.getText();
    }

    private void getDateBox() {
        String from = dateFrom.getText().replaceAll("[^0-9/]", "").trim();
        String to = dateTo.getText().replaceAll("[^0-9/]", "").trim();

        if (!from.isEmpty()) {
            startDate = from;
        }
        if (!to.isEmpty()) {
            endDate = to;
        }
    }

    private String getCounty() {
        return (String) countiesDropdown.getSelectedItem();
    }
}
