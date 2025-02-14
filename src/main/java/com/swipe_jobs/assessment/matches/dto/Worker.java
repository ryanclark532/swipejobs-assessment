package com.swipe_jobs.assessment.matches.dto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class Worker {
    public int rating;
    public boolean isActive;
    public List<String> certificates;
    public List<String> skills;
    public JobSearchAddress jobSearchAddress;
    public String transportation;
    public boolean hasDriversLicense;
    public List<Availability> availability;
    public String phone;
    public String email;
    public Name name;
    public int age;
    public String guid;
    public int userId;

    public static List<Worker> loadPeople() {
        WebClient webClient = WebClient.create("https://test.swipejobs.com/api/");

        Mono<List<Worker>> response = webClient.get()
            .uri("/workers")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<>() {
            });

        return response.block();
    }
}


