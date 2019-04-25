package net.andreinc.commercetools.enricher.config;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.extensions.Destination;
import io.sphere.sdk.extensions.HttpDestinationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.*;

@Configuration
public class Config {

    @Value("${customer.extension.url}")
    String customerUrl;

    @Bean
    public BlockingSphereClient sphereClient(
            @Value("${admin.api.projectKey}") final String projectKey,
            @Value("${admin.api.clientId}") final String clientId,
            @Value("${admin.api.clientSecret}") final String clientSecret,
            @Value("${admin.api.authUrl}") final String authUrl,
            @Value("${admin.api.apiUrl}") final String apiUrl
    ) throws IOException {
        final SphereClientConfig config = SphereClientConfig.of(projectKey, clientId, clientSecret, authUrl, apiUrl);
        final SphereClient asyncClient = SphereClientFactory.of().createClient(config);
        return BlockingSphereClient.of(asyncClient, 20, TimeUnit.SECONDS);
    }

    @Bean
    public Destination customerDestination() {
        return HttpDestinationBuilder.of(customerUrl, null)
                                     .get();
    }

    @Bean
    public Executor enricherExecutor(
            @Value("${executor.blocking.queue.size}") int blockingQueueSize,
            @Value("${executor.core.pool.size}") int corePoolSize,
            @Value("${executor.max.pool.size}") int maxPoolSize,
            @Value("${executor.keep.alive.seconds}") int keepAliveSeconds
    ) {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(blockingQueueSize);
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, blockingQueue);
        return threadPoolExecutor;
    }
}
