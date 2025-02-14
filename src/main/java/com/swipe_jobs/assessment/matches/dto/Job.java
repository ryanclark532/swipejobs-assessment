package com.swipe_jobs.assessment.matches.dto;

import com.swipe_jobs.assessment.matches.DistanceCalculator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

public class Job {
    public boolean driverLicenseRequired;
    public List<String> requiredCertificates;
    public Location location;
    public String billRate;
    public int workersRequired;
    public String startDate;
    public String about;
    public String jobTitle;
    public String company;
    public String guid;
    public int jobId;

    public float score = 0;

    public boolean hasCertificates(Worker worker) {
        return new HashSet<>(worker.certificates).containsAll(this.requiredCertificates);
    }

    public boolean checkDriversLicense(Worker worker) {
        if (!this.driverLicenseRequired) {
            return true;
        }
        return worker.hasDriversLicense;
    }

    public boolean checkLocations(Worker worker) {
        double distance = DistanceCalculator.calculateDistance(worker.jobSearchAddress.latitude, worker.jobSearchAddress.longitude, this.location.latitude, this.location.longitude);
        return distance <= worker.jobSearchAddress.maxJobDistance;
    }

    public static List<Job> loadJobs() {
        WebClient webClient = WebClient.create("https://test.swipejobs.com/api/");

        Mono<List<Job>> response = webClient.get()
                .uri("/jobs")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });

        return response.block();
    }

}


