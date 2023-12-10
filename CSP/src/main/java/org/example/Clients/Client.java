package org.example.Clients;

import org.example.Buffer.DoubleChannel;
import org.jcsp.lang.CSProcess;

public interface Client extends CSProcess {
    void run();

    DoubleChannel getBufferCommunication();
}
