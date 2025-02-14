package com.swipe_jobs.assessment.matches;

import com.swipe_jobs.assessment.matches.dto.Job;
import com.swipe_jobs.assessment.matches.dto.Worker;

import java.util.function.BiFunction;

public enum MatchCondition {
    DRIVERS_LICENSE(1.0f, MatchConditions::driversLicenseMatch),
    LOCATION(1.0f, MatchConditions::locationMatch),
    CERTIFICATES(1.0f, MatchConditions::certificatesMatch),
    PAY_RATE(1.0f, MatchConditions::payRateMatch);

    private final float weight;
    private final BiFunction<Worker, Job, Float> matcher;

    MatchCondition(float weight, BiFunction<Worker, Job, Float> matcher) {
        this.weight = weight;
        this.matcher = matcher;
    }

    public float getWeight() {
        return weight;
    }

    public float apply(Worker worker, Job job) {
        return matcher.apply(worker, job);
    }
}
