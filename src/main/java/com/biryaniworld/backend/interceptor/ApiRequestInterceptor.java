package com.biryaniworld.backend.interceptor;

import com.biryaniworld.backend.metrics.ApiRequestMetrics;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ApiRequestInterceptor implements HandlerInterceptor {

    private final ApiRequestMetrics metrics;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        metrics.incrementTotalRequests();
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        if (ex != null) {
            metrics.incrementFailedRequests();
        } else {
            metrics.incrementSuccessfulRequests();
        }
    }
} 