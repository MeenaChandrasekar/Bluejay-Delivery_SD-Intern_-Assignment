package WorkEntryApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class WorkEntry {
    String name;
    String position;
    Date startTime;
    Date endTime;
    String duration;

    public WorkEntry(String name, String position, Date startTime, Date endTime, String duration) {
        this.name = name;
        this.position = position;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }
}

public class WorkEntryManager {

    public static void main(String[] args) {
        // Read data from CSV file
    	ArrayList<WorkEntry> workEntries = readCSV("C:\\Users\\meena\\Downloads\\task\\Assignment_Timecard.xlsx - Sheet1.csv");


        // Task a: Find individuals who have worked for 7 consecutive days
        ArrayList<String> consecutiveWorkers = findWorkersWithConsecutiveDays(workEntries);
        System.out.println("Workers who have worked for 7 consecutive days: " + consecutiveWorkers);

        // Task b: Identify individuals who have less than 10 hours of time between shifts but greater than 1 hour
        ArrayList<String> shortBreakWorkers = findWorkersWithShortBreaks(workEntries);
        System.out.println("Workers with less than 10 hours between shifts but greater than 1 hour: " + shortBreakWorkers);

        // Task c: Find individuals who have worked for more than 14 hours in a single shift
        ArrayList<String> longShiftWorkers = findWorkersWithLongShifts(workEntries);
        System.out.println("Workers who have worked for more than 14 hours in a single shift: " + longShiftWorkers);

        // Output results to output.txt
        writeOutputToFile("C:\\Users\\meena\\Downloads\\task\\output.txt", consecutiveWorkers, shortBreakWorkers, longShiftWorkers);
    }

    
        private static ArrayList<WorkEntry> readCSV(String filePath) {
            ArrayList<WorkEntry> workEntries = new ArrayList<>();
            String line;

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                // Skip header line
                br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

                    try {
                        // Check if the date string is not empty
                        if (!data[2].isEmpty() && !data[3].isEmpty()) {
                            Date startTime = dateFormat.parse(data[2]);
                            Date endTime = dateFormat.parse(data[3]);
                            workEntries.add(new WorkEntry(data[0], data[1], startTime, endTime, data[4]));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return workEntries;
        }


    // Implementation for Task a
    private static ArrayList<String> findWorkersWithConsecutiveDays(ArrayList<WorkEntry> workEntries) {
        ArrayList<String> consecutiveWorkers = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < workEntries.size() - 6; i++) {
            Date startDate = workEntries.get(i).endTime;
            Date endDate = workEntries.get(i + 6).startTime;

            long daysBetween = TimeUnit.DAYS.convert(endDate.getTime() - startDate.getTime(), TimeUnit.MILLISECONDS);

            if (daysBetween == 6) {
                // Worker has worked for 7 consecutive days
                consecutiveWorkers.add(workEntries.get(i).name + " - " + workEntries.get(i).position);
            }
        }

        return consecutiveWorkers;
    }

    // Implementation for Task b
    private static ArrayList<String> findWorkersWithShortBreaks(ArrayList<WorkEntry> workEntries) {
        ArrayList<String> shortBreakWorkers = new ArrayList<>();
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");

        for (int i = 0; i < workEntries.size() - 1; i++) {
            Date currentEndTime = workEntries.get(i).endTime;
            Date nextStartTime = workEntries.get(i + 1).startTime;

            long hoursBetween = TimeUnit.HOURS.convert(nextStartTime.getTime() - currentEndTime.getTime(), TimeUnit.MILLISECONDS);

            if (hoursBetween > 1 && hoursBetween < 10) {
                // Worker has a break between shifts less than 10 hours but greater than 1 hour
                shortBreakWorkers.add(workEntries.get(i).name + " - " + workEntries.get(i).position);
            }
        }

        return shortBreakWorkers;
    }

 // Implementation for Task c
    private static ArrayList<String> findWorkersWithLongShifts(ArrayList<WorkEntry> workEntries) {
        ArrayList<String> longShiftWorkers = new ArrayList<>();

        for (WorkEntry entry : workEntries) {
            String[] durationParts = entry.duration.split(":");
            if (durationParts.length == 2) {
                int hours = Integer.parseInt(durationParts[0]);
                int minutes = Integer.parseInt(durationParts[1]);

                double totalHours = hours + (double) minutes / 60;

                if (totalHours > 14) {
                    // Worker has worked for more than 14 hours in a single shift
                    longShiftWorkers.add(entry.name + " - " + entry.position);
                }
            }
        }

        return longShiftWorkers;
    }


    private static void writeOutputToFile(String fileName, ArrayList<String> consecutiveWorkers,
                                          ArrayList<String> shortBreakWorkers, ArrayList<String> longShiftWorkers) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Workers who have worked for 7 consecutive days: " + consecutiveWorkers + "\n");
            writer.write("Workers with less than 10 hours between shifts but greater than 1 hour: " + shortBreakWorkers + "\n");
            writer.write("Workers who have worked for more than 14 hours in a single shift: " + longShiftWorkers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
