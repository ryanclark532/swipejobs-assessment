package com.swipe_jobs.assessment.matches;

import com.swipe_jobs.assessment.matches.dto.Job;
import com.swipe_jobs.assessment.matches.dto.Worker;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class MatchController {

    @GetMapping("/matches")
    public List<Job> matches(@RequestParam(value = "workerId") String workerId) {
        Worker worker = Worker.loadPeople().stream()
                .filter(p -> p.userId == Integer.parseInt(workerId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found"));

        if (!worker.isActive) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Worker is not active");
        }

        float maxScore = Arrays.stream(MatchCondition.values())
                .map(MatchCondition::getWeight)
                .reduce(Float::sum)
                .orElse(1.0f);

        return Job.loadJobs().stream()
                .map(job -> {
                    float score = Arrays.stream(MatchCondition.values())
                            .map(condition -> condition.apply(worker, job) * condition.getWeight())
                            .reduce(Float::sum)
                            .orElse(0.0f);


                    job.score = Math.round((score / maxScore) * 100);
                    return job;
                })
                .sorted(Comparator.comparingDouble(job -> -job.score))
                .limit(3)
                .collect(Collectors.toList());
    }
}
