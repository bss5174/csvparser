# CSV Parser for MS3 Coding Challenge

This project parses through a CSV file and inserts records to a SQLite in-memory database. Only correctly formatted records are added to the database, any failed records are added to the bad data CSV file. The numbers for successful, failed, and total records are recorded in a log file at the end.

### Instructions

Maven will handle adding the necessary JAR files needed for this project. Due to the use of Java Streams, you will need Java 8 or later. Once the repository is cloned locally, you can compile and run.

### Approach to solving this challenge

I used Java Streams to efficiently sort through successful and failed records from the CSV file. Successful records are added to an ArrayList, while failed records are printed to the bad data CSV file. Each type of record has a counter for writing to the log file at the end. The completed list of successful records is then sent to the method that handles the database functionality. The SQLite database is created in-memory and a table is created based on the given column headers. The list of records is iterated through, inserting the fields for each record into the database. Finally, the records count are written to a log file.
