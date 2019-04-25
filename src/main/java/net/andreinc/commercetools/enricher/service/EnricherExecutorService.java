package net.andreinc.commercetools.enricher.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.client.BlockingSphereClient;
import net.andreinc.commercetools.enricher.model.Enricher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

@Service
public class EnricherExecutorService {

    Logger logger = LoggerFactory.getLogger(EnricherExecutorService.class.getCanonicalName());

    @Autowired
    Executor enricherExecutor;

    @Autowired
    BlockingSphereClient sphereClient;

    public void execute(List<Enricher> enrichers, JsonNode data) {
        List<CompletableFuture<Void>> cfl =
                enrichers.stream()
                 .map(e -> CompletableFuture.runAsync(() -> {
                        logger.info("Running enricher {} from thread {}", e.getName(), currentThread());
                        e.enrich(sphereClient, data);
                     }
                 , enricherExecutor))
                 .collect(Collectors.toList());

        CompletableFuture.allOf(cfl.toArray(new CompletableFuture[cfl.size()]))
                         .join();
    }
}
