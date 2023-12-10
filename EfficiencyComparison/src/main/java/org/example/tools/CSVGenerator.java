package org.example.tools;

import org.example.program.ProgramData;
import org.example.threads.ThreadData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVGenerator {
    private static final List<String> columns = List.of("programType", "threadsNumber", "BufferSize", "threadType", "OperationCount", "CpuTime");
    private final String filePath;

    public CSVGenerator(String filePath) {
        this.filePath = filePath;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = String.join(",", columns);
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDataToFile(ProgramData data, int threadCount, int bufferSize) {
        for (ThreadData record : data.records()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                List<String> row = List.of(data.programType().toString(), String.valueOf(threadCount), String.valueOf(bufferSize),
                        record.threadType().toString(), String.valueOf(record.operationCounter()), String.valueOf(record.nanoTime()));
                String line = String.join(",", row);
                writer.write(line);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}