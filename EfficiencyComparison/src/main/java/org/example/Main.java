package org.example;

import org.example.tools.CSVGenerator;
import org.example.tools.RandomGeneratorType;
import org.example.tools.TestGenerator;


/*
STAŁE:
EXECUTION_TIME_MILLIS - czas wykonywania programu, stały, ustawiony na 30sec
RANDOM_MAX_VALUE - górna wartość jaką może przyjąć losowana liczba, domyslnie 10

ZMIENNE:
threadNumbers - liczba wątków - sprawdzamy dla 1, 2, 10, 50, 100
bufferSizes - rozmiar bufora - sprawdzamy dla 20, 50, 100
 */

public class Main {
    public static final long EXECUTION_TIME_MILLIS = 30000L;
    public static final int RANDOM_MAX_VALUE = 10;

    private static final int[] threadNumbers = {1, 2, 10, 50, 100};
    private static final int[] bufferSizes = {20, 50, 100};


    public static void main(String[] args) {
        generateData("test_threadLocal.csv", RandomGeneratorType.THREAD_LOCAL);
        generateData("test_normal.csv", RandomGeneratorType.NORMAL);
    }

    private static void generateData(String fileName, RandomGeneratorType RANDOM_TYPE){
        CSVGenerator csvGenerator = new CSVGenerator( "src/main/resources/" + fileName);

        for (int bufferSize : bufferSizes){
            System.out.println("Start generating tests for buffer size: " + bufferSize + "...");

            for (int threadNumber : threadNumbers){
                TestGenerator generator = new TestGenerator(threadNumber, bufferSize, RANDOM_TYPE, csvGenerator);
                generator.generate();
            }
            System.out.println("Finished for buffer size: " + bufferSize + "!");
        }
        System.out.println("Finished all tests! Wait for your results!");
    }
}