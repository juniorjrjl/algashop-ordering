package com.algaworks.algashop.ordering.domain.model;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import io.hypersistence.tsid.TSID;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class IdGenerator {

    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator
            = Generators.timeBasedEpochRandomGenerator();

    private static final TSID.Factory tsidFactory = TSID.Factory.INSTANCE;


    public static UUID generateUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }

    /*
    *
    * TSID_NODE
    * TSID_NODE_COUNT
    *
    * */
    public static TSID generateTSID() {
        return tsidFactory.generate();
    }

}
