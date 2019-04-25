package net.andreinc.commercetools.enricher.enrichers.customer;


import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.client.BlockingSphereClient;
import net.andreinc.commercetools.enricher.model.CustomerEnricher;
import org.springframework.stereotype.Component;

@Component
public class NameValidatorEnricher extends CustomerEnricher {

    @Override
    public void enrich(BlockingSphereClient client, JsonNode data) {
        System.out.println("here: " + getName());
    }
}
