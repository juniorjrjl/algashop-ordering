package com.algaworks.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class IdGenerator {

    private static TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator
            = Generators.timeBasedEpochRandomGenerator();

    public static UUID generateUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }

}
