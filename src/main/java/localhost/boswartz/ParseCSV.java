package localhost.boswartz;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;


public class ParseCSV {

    public static void main(String[] args) throws IOException {

        // list to hold successful records to be added to database
        List<CSVRecord> recordsList = new ArrayList<>();
        int recordsSuccessful = 0;
        int recordsFailed = 0;
        int recordsReceived;

        // create CSV file to contain failed records
        String fileName = "bad-data-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv";
        File badData = new File(fileName);
        CSVFormat format = CSVFormat.DEFAULT;
        FileWriter writer = new FileWriter(badData);
        CSVPrinter printer = new CSVPrinter(writer, format);

        // setup CSV file to be parsed using CSVParser
        File csvData = new File("ms3Interview.csv");
        CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(),
                CSVFormat.DEFAULT.withFirstRecordAsHeader());

        // parse through data, handling successful and failed records
        for (CSVRecord csvRecord : parser) {
            if (Stream.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J").anyMatch(s -> csvRecord.get(s).equals(""))) {
                recordsFailed++;
                printer.printRecord(csvRecord); // write failed record to bad data CSV file
            } else {
                recordsSuccessful++;
                recordsList.add(csvRecord); // add successful records to list
            }
        }

        recordsReceived = recordsFailed + recordsSuccessful;

        // write results to log file
        File file = new File("parse results.log");
        FileUtils.writeStringToFile(file, "records received: " + recordsReceived + "\n" + "records successful: "
                + recordsSuccessful + "\n" + "records failed: " + recordsFailed, StandardCharsets.UTF_8);

        CSVtoDB(recordsList); // create database with list of successful records
    }

    // insert successful records into SQLite in-memory database
    private static void CSVtoDB(List<CSVRecord> recordsList) {
        try {
            // create in-memory database connection
            Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            Statement statement = connection.createStatement();

            // create a new table
            statement.executeUpdate("create table records (A string, B string, C string, " +
                    "D string, E string, F string, G string, H string, I string, J string)");

            PreparedStatement insertStatement = connection.prepareStatement("insert into records values(?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?)");

            // iterate through the records and insert to table using PreparedStatement
            for (CSVRecord currentRecord : recordsList) {
                insertStatement.setString(1, currentRecord.get("A"));
                insertStatement.setString(2, currentRecord.get("B"));
                insertStatement.setString(3, currentRecord.get("C"));
                insertStatement.setString(4, currentRecord.get("D"));
                insertStatement.setString(5, currentRecord.get("E"));
                insertStatement.setString(6, currentRecord.get("F"));
                insertStatement.setString(7, currentRecord.get("G"));
                insertStatement.setString(8, currentRecord.get("H"));
                insertStatement.setString(9, currentRecord.get("I"));
                insertStatement.setString(10, currentRecord.get("J"));
                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}