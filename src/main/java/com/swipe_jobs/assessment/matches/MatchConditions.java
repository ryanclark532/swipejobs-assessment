package com.swipe_jobs.assessment.matches;

import com.swipe_jobs.assessment.matches.dto.Job;
import com.swipe_jobs.assessment.matches.dto.Worker;

public class MatchConditions {

    public static float driversLicenseMatch(Worker worker, Job job) {
        return job.checkDriversLicense(worker) ? 1f : 0f;
    }

    public static float locationMatch(Worker worker, Job job) {
        double distance = calculateDistance(
                worker.jobSearchAddress.latitude, worker.jobSearchAddress.longitude,
                job.location.latitude, job.location.longitude
        );
        return (distance <= worker.jobSearchAddress.maxJobDistance) ?
                (1 - (float) distance / worker.jobSearchAddress.maxJobDistance) : 0f;
    }

    public static float certificatesMatch(Worker worker, Job job) {
        return job.hasCertificates(worker) ? 1f : 0f;
    }

    public static float payRateMatch(Worker worker, Job job) {
        float payRate = Float.parseFloat(job.billRate.replace("$", ""));
        return payRate / 100;
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.asin(Math.sqrt(a));
    }
}
