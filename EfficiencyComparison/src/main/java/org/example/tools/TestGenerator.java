package org.example.tools;

import org.example.program.Program;
import org.example.program.ProgramData;
import org.example.program.ProgramFourConditions;
import org.example.program.ProgramLocks;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.example.Main.EXECUTION_TIME_MILLIS;
import static org.example.Main.RANDOM_MAX_VALUE;

public class TestGenerator {
    private final int threadCount;
    private final int bufferSize;
    private final Supplier<IntStream> randomStreamSupplier;
    private final CSVGenerator csvGenerator;

    public TestGenerator(int threadCount, int bufferSize, RandomGeneratorType randomType, CSVGenerator csvGenerator) {
        this.csvGenerator = csvGenerator;
        this.threadCount = threadCount;
        this.bufferSize = bufferSize;
        this.randomStreamSupplier = randomType == RandomGeneratorType.THREAD_LOCAL
                ? () -> ThreadLocalRandom.current().ints(0, RANDOM_MAX_VALUE)
                : () -> new Random().ints(0, RANDOM_MAX_VALUE);
    }


    public void generate() {
        Program locksProgram = new ProgramLocks(threadCount, EXECUTION_TIME_MILLIS, bufferSize, randomStreamSupplier);
        Program fourConditionProgram = new ProgramFourConditions(threadCount, EXECUTION_TIME_MILLIS, bufferSize, randomStreamSupplier);

        Thread t1 = new Thread(() -> run(locksProgram));
        Thread t2 = new Thread(() -> run(fourConditionProgram));
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException ignored) {
        }
    }

    private void run(Program program) {
        Optional<ProgramData> programData = program.getData();
        if (programData.isPresent()) {
            ProgramData data = programData.get();
            csvGenerator.addDataToFile(data, threadCount, bufferSize);
        }
    }
}
