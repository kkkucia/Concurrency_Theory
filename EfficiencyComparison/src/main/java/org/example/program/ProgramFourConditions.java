package org.example.program;

import org.example.monitors.Monitor;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ProgramFourConditions extends ProgramWrapper{

    public ProgramFourConditions(int threadCount, long executionTimeMillis, int bufferSize, Supplier<IntStream> randomStreamSupplier) {
        super(ProgramType.FOUR_CONDITIONS,  new Monitor(bufferSize), threadCount, executionTimeMillis, randomStreamSupplier);
    }
}
