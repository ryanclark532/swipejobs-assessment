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


@RestController
public class MatchController {

    record MatchWeights(float location, float certificates, float driversLicense) {}

    final MatchWeights weights = new MatchWeights(1.0f, 1.0f, 1.0f);

    @GetMapping("/matches")
    public List<Job> matches(@RequestParam(value = "workerId") String workerId) {
        var worker = Worker.loadPeople().stream()
                .filter(p -> p.userId == Integer.parseInt(workerId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found"));

        if (!worker.isActive) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Worker is not active");
        }

        var jobs = Job.loadJobs().stream()
                .map(job -> {
                    float score = 0;
                    if (job.checkDriversLicense(worker)) score+= weights.driversLicense;
                    if (job.checkLocations(worker)) score+= weights.location;
                    if (job.hasCertificates(worker)) score+= weights.certificates;

                    int numChecks = MatchWeights.class.getRecordComponents().length;
                    job.score = (score / numChecks) * 100;
                    return job;
                })
                .sorted(Comparator.comparingDouble((Job job) -> job.score).reversed()).toList();

        return jobs.subList(0, Math.min(3, jobs.size()));
    }
}
