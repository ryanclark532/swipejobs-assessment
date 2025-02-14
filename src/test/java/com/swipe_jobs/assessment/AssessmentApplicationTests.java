package com.swipe_jobs.assessment;

import com.swipe_jobs.assessment.matches.MatchController;
import com.swipe_jobs.assessment.matches.dto.Job;
import com.swipe_jobs.assessment.matches.dto.JobSearchAddress;
import com.swipe_jobs.assessment.matches.dto.Location;
import com.swipe_jobs.assessment.matches.dto.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@WebMvcTest(MatchController.class)
class AssessmentApplicationTests {

	@Test
	void shouldThrowForInactive(){
		try (MockedStatic<Worker> mockedWorker = Mockito.mockStatic(Worker.class)) {
			var w = new Worker();
			w.userId = 0;
			w.isActive = false;
			mockedWorker.when(Worker::loadPeople).thenReturn(Collections.singletonList(w));

			var controller = new MatchController();

			ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.matches("0"));

			assertEquals("Worker is not active", exception.getReason());
		}
	}

	@Test
	void shouldThrowForNotFound() {
		try (MockedStatic<Job> mockedJob = Mockito.mockStatic(Job.class)) {
			mockedJob.when(Job::loadJobs).thenReturn(Collections.singletonList(new Job()));

			var controller = new MatchController();

			ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.matches("999"));

			assertEquals("Worker not found", exception.getReason());
		}
	}

	@Test
	void shouldScoreProperly() {
		var controller = new MatchController();
		var job = new Job();
		var worker = new Worker();
		worker.userId = 0; worker.isActive = true;
		var mockedJob = Mockito.mockStatic(Job.class);
		var mockedWorker = Mockito.mockStatic(Worker.class);
		mockedJob.when(Job::loadJobs).thenReturn(Collections.singletonList(job));
		mockedWorker.when(Worker::loadPeople).thenReturn(Collections.singletonList(worker));

		job.driverLicenseRequired = true;
		worker.hasDriversLicense = false;

		job.requiredCertificates = List.of("cert1");
		worker.certificates = List.of("cert2");

		job.location = new Location(0, 0);
		worker.jobSearchAddress = new JobSearchAddress("kms", 10, 100, 100);

		//no checks pass
		var res = controller.matches("0");
		assertEquals(0.0f, res.getFirst().score);

		//some checks pass
		worker.hasDriversLicense = true;
		worker.certificates = List.of("cert1");
		res = controller.matches("0");
		assertEquals(66.66667f, res.getFirst().score);

		//all checks pass
		worker.jobSearchAddress = new JobSearchAddress("kms", 10, 0, 0);
		res = controller.matches("0");
		assertEquals(100f, res.getFirst().score);
	}
}
