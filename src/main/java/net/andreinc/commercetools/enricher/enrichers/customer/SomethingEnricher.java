package net.andreinc.commercetools.enricher.enrichers.customer;

import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.extensions.TriggerType;
import net.andreinc.commercetools.enricher.model.CustomerEnricher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SomethingEnricher extends CustomerEnricher {

    @Override
    public void enrich(BlockingSphereClient client, JsonNode data) {
        System.out.println("here: " + getName());
    }

    @Override
    public List<TriggerType> getTriggerTypes() {
        return Arrays.asList(TriggerType.UPDATE);
    }
}
