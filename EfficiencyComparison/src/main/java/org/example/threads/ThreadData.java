package org.example.threads;

public record ThreadData(ThreadType threadType, long operationCounter, long nanoTime) {
}
