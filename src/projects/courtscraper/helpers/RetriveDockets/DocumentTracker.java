package courtscraper.helpers.RetriveDockets;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DocumentTracker {
    private static final String DOWNLOADED_CSV = "already_downloaded_docs.csv";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    // Load all existing entries into a Set for quick lookup
    public static Set<String> loadDownloadedEntries() {
        Set<String> entries = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DOWNLOADED_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                entries.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("No existing downloaded file yet.");
        }
        return entries;
    }

    // Append all new entries to file at once
    public static void saveNewEntries(List<String> newEntries) {
        if (newEntries.isEmpty()) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DOWNLOADED_CSV, true))) {
            for (String entry : newEntries) {
                bw.write(entry);
                bw.newLine();
            }
            System.out.println("✅ Saved " + newEntries.size() + " new entries to file.");
        } catch (IOException e) {
            System.out.println("⚠️ Error saving new entries: " + e.getMessage());
        }
    }

    // Helper to check if a date is within the last 3 days
    public static boolean isWithinLast3Days(String dateStr) {
        try {
            LocalDate docDate = LocalDate.parse(dateStr, DATE_FORMAT);
            return !docDate.isBefore(LocalDate.now().minusDays(3));
        } catch (Exception e) {
            System.out.println("⚠️ Invalid date format: " + dateStr);
            return false;
        }
    }
}
