package com.biryaniworld.backend.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ApiRequestMetrics {

    private final Counter totalRequestsCounter;
    private final Counter successfulRequestsCounter;
    private final Counter failedRequestsCounter;

    public ApiRequestMetrics(MeterRegistry registry) {
        this.totalRequestsCounter = Counter.builder("api.requests.total")
                .description("Total number of API requests")
                .register(registry);
        this.successfulRequestsCounter = Counter.builder("api.requests.successful")
                .description("Number of successful API requests")
                .register(registry);
        this.failedRequestsCounter = Counter.builder("api.requests.failed")
                .description("Number of failed API requests")
                .register(registry);
    }

    public void incrementTotalRequests() {
        totalRequestsCounter.increment();
    }

    public void incrementSuccessfulRequests() {
        successfulRequestsCounter.increment();
    }

    public void incrementFailedRequests() {
        failedRequestsCounter.increment();
    }
} 