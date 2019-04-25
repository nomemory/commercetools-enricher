package net.andreinc.commercetools.enricher.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.client.BlockingSphereClient;

public abstract class EnricherBase implements Enricher<JsonNode> {

    public abstract void enrich(BlockingSphereClient client, JsonNode data);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
