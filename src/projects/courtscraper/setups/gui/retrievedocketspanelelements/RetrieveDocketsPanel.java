package courtscraper.setups.gui.retrievedocketspanelelements;

import courtscraper.FlowStart;
import courtscraper.setups.gui.Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class RetrieveDocketsPanel extends Panels {

  // Panel elements
  public static JComboBox<String> countiesDropdown;
  public static JTextField searchTerm;
  public static JTextField docketSearchTerm;
  public static JTextField dateFrom;
  public static JTextField dateTo;
  public static JButton startButton;

  // Selected values
  public static String selectedCounty = "";
  public static String enteredSearchTerm = "";
  public static String enteredDocketSearch = "";
  public static String enteredDateFrom = "";
  public static String enteredDateTo = "";

  public static JPanel retrieveDocketsPanel = new JPanel();
  public static GridBagConstraints gbcRetrieve = new GridBagConstraints();

  public static void buildRetrieveDocketsPanel() {
    retrieveDocketsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
    retrieveDocketsPanel.setLayout(new GridBagLayout());
    gbcRetrieve.fill = GridBagConstraints.BOTH;
    gbcRetrieve.insets = new Insets(5, 5, 5, 5);

    int row = 0;

    // County Dropdown
    JLabel countyLabel = new JLabel("Select County:");
    gbcRetrieve.gridx = 0;
    gbcRetrieve.gridy = row;
    retrieveDocketsPanel.add(countyLabel, gbcRetrieve);

    String[] counties = {"Los Angeles, CA", "Sacramento, CA", "Riverside, CA"};
    countiesDropdown = new JComboBox<>(counties);
    gbcRetrieve.gridx = 1;
    retrieveDocketsPanel.add(countiesDropdown, gbcRetrieve);

    countiesDropdown.addItemListener(
        new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              selectedCounty = (String) countiesDropdown.getSelectedItem();
            }
          }
        });

    row++;

    // Search Term
    JLabel searchLabel = new JLabel("Search Term:");
    gbcRetrieve.gridx = 0;
    gbcRetrieve.gridy = row;
    retrieveDocketsPanel.add(searchLabel, gbcRetrieve);

    searchTerm = new JTextField(15);
    gbcRetrieve.gridx = 1;
    retrieveDocketsPanel.add(searchTerm, gbcRetrieve);

    row++;

    // Docket Search Term
    JLabel docketLabel = new JLabel("Docket Search Term:");
    gbcRetrieve.gridx = 0;
    gbcRetrieve.gridy = row;
    retrieveDocketsPanel.add(docketLabel, gbcRetrieve);

    docketSearchTerm = new JTextField(15);
    gbcRetrieve.gridx = 1;
    retrieveDocketsPanel.add(docketSearchTerm, gbcRetrieve);

    row++;

    // Date From
    JLabel dateFromLabel = new JLabel("Date From (MM/DD/YYYY):");
    gbcRetrieve.gridx = 0;
    gbcRetrieve.gridy = row;
    retrieveDocketsPanel.add(dateFromLabel, gbcRetrieve);

    dateFrom = new JTextField(10);
    gbcRetrieve.gridx = 1;
    retrieveDocketsPanel.add(dateFrom, gbcRetrieve);

    row++;

    // Date To
    JLabel dateToLabel = new JLabel("Date To (MM/DD/YYYY):");
    gbcRetrieve.gridx = 0;
    gbcRetrieve.gridy = row;
    retrieveDocketsPanel.add(dateToLabel, gbcRetrieve);

    dateTo = new JTextField(10);
    gbcRetrieve.gridx = 1;
    retrieveDocketsPanel.add(dateTo, gbcRetrieve);

    row++;

    // Start Button
    startButton = new JButton("Start");
    gbcRetrieve.gridx = 1;
    gbcRetrieve.gridy = row;
    retrieveDocketsPanel.add(startButton, gbcRetrieve);

    startButton.addActionListener(
        e -> {
          SwingWorker<Void, Void> startLooper =
              new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                  startButton.setEnabled(false);
                  startButton.setText("Started");

                  // Start Retrieve Dockets flow
                  FlowStart.StartRetrieveDocketsFlow();

                  startButton.setEnabled(true);
                  startButton.setText("Start");
                  return null;
                }
              };
          startLooper.execute();
        });
  }
}