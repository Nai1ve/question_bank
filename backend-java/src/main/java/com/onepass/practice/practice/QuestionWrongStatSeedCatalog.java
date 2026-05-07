package com.onepass.practice.practice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.onepass.practice.common.JsonResourceReader;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuestionWrongStatSeedCatalog {

    private static final Logger log = LoggerFactory.getLogger(QuestionWrongStatSeedCatalog.class);

    private final List<QuestionWrongStatSeed> seeds;

    public QuestionWrongStatSeedCatalog(JsonResourceReader jsonResourceReader) {
        this.seeds = List.copyOf(
                jsonResourceReader.read(
                        "mock-data/question-wrong-stats.json",
                        new TypeReference<List<QuestionWrongStatSeed>>() {
                        }
                )
        );
        log.info("Loaded wrong stat resource seeds count={} (used for mock fallback only)", seeds.size());
    }

    public List<QuestionWrongStatSeed> listAll() {
        return seeds;
    }
}
