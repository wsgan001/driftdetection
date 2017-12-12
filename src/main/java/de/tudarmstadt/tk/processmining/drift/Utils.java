package de.tudarmstadt.tk.processmining.drift;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Database;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Alexander Seeliger on 12.12.2017.
 */
public class Utils {

    /**
     * Stores a transaction database to disk.
     *
     * @param database
     * @param fileName
     */
    public static void saveDatabase(Database database, String fileName) {
        try (FileWriter fw = new FileWriter(fileName)) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {

                for (Transaction transaction : database.getTransactions()) {
                    StringBuilder sb = new StringBuilder();
                    for (Integer item : transaction.getItems()) {
                        sb.append(item);
                        sb.append(" ");
                    }

                    bw.append(sb.toString());
                    bw.newLine();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a date string into a LocalDate.
     *
     * @param date
     * @return
     */
    public static LocalDate parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", new Locale("en", "US"));
        return LocalDate.parse(date, formatter);
    }

}
