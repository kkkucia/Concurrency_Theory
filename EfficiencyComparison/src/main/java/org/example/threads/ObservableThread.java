package org.example.threads;

public interface ObservableThread {
    ThreadData getThreadData();

    void setShouldExit(boolean shouldExit);
}
