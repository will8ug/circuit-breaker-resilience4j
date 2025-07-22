package io.will.circuitbreakerresilience4j;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class HelloService {
    private final AtomicInteger count = new AtomicInteger(0);

    @CircuitBreaker(name = "silenterror", fallbackMethod = "fallbackHello")
    public String hello() {
        int i = count.getAndIncrement();
        System.out.println(i);

        if (i % 2 == 0) {
            throw new RuntimeException("Test Exception for circuit breaker");
        }
        return "Hello";
    }

    public String fallbackHello(Exception e) {
        System.out.println("Coming into fallback method...");
        System.out.printf("%s: %s%n", e.getClass(), e.getMessage());
        return "Please try it out later";
    }
}
