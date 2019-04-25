package net.andreinc.commercetools.enricher.model;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.extensions.TriggerType;


import java.util.Arrays;
import java.util.List;

public interface Enricher<T> {

    void enrich(BlockingSphereClient client, T data);

    default List<TriggerType> getTriggerTypes() {
        return Arrays.asList(TriggerType.CREATE, TriggerType.UPDATE);
    }

    String getName();
}

