package org.example.program;

import org.example.threads.ThreadData;

import java.util.List;

public record ProgramData(
        ProgramType programType,
        List<ThreadData> records
) {}
