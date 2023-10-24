package org.example;

public class RandomNumber {
    final private java.util.Random random = new java.util.Random();
    private final int min;
    private final int max;

    public RandomNumber(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int get() {
        return random.nextInt(this.max + 1 - this.min) + this.min;
    }
}

