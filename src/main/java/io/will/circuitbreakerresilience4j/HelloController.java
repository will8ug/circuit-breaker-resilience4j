package io.will.circuitbreakerresilience4j;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HelloController {
    @Autowired
    private HelloService helloService;

    private final AtomicInteger count = new AtomicInteger(0);

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void health() {
        System.out.println("Service is running");
    }

    @GetMapping(path = "/hello-no-error", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> helloNoError() {
        return new ResponseEntity<>(helloService.hello(), HttpStatus.OK);
    }

    @CircuitBreaker(name = "translateerror")
    @GetMapping(path = "/hello-translated-error", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> helloTranslatedError() {
        count.incrementAndGet();
        System.out.printf("GET /hello-translated-error: %d%n", count.get());
        alwaysExpectAnException();

        return new ResponseEntity<>("Don't expect to return this", HttpStatus.OK);
    }

    private void alwaysExpectAnException() {
        if (count.get() > 0) {
            throw new RuntimeException("Test error for circuit breaker");
        }
    }
}
