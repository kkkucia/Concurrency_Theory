package org.example.program;

import org.example.monitors.MonitorLocks;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ProgramLocks extends ProgramWrapper{

    public ProgramLocks(int threadCount, long executionTimeMillis, int bufferSize, Supplier<IntStream> randomStreamSupplier) {
        super(ProgramType.LOCKS,  new MonitorLocks(bufferSize), threadCount, executionTimeMillis, randomStreamSupplier);
    }
}
