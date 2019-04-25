package net.andreinc.commercetools.enricher.service;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.extensions.*;
import io.sphere.sdk.extensions.commands.ExtensionCreateCommand;
import io.sphere.sdk.extensions.commands.ExtensionDeleteCommand;
import io.sphere.sdk.extensions.queries.ExtensionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.sphere.sdk.extensions.ExtensionResourceType.CUSTOMER;
import static io.sphere.sdk.extensions.TriggerType.CREATE;
import static io.sphere.sdk.extensions.TriggerType.UPDATE;
import static java.util.Arrays.asList;

@Service
public class ExtensionService {

    Logger logger = LoggerFactory.getLogger(ExtensionService.class.getCanonicalName());

    @Autowired
    Destination customerDestination;

    @Autowired
    BlockingSphereClient sphereClient;

    @Value("${customer.extension.url}")
    String customerExtensionUrl;

    @Value("${customer.extension.name}")
    String customerExtensionName;

    public void addCustomerExtension() {
        addExtension(customerExtensionName, customerExtensionUrl);
    }

    public void removeCustomerExtension() {
      removeExtension(customerExtensionName);

    }

    public void addExtension(String extensionName, String extensionURL) {
        final String name = extensionName;  final Destination destination = HttpDestinationBuilder.of(extensionURL, null).get();

        final List<TriggerType> triggerTypes = asList(UPDATE, CREATE);
        final Trigger trigger = TriggerBuilder.of(CUSTOMER, triggerTypes).get();
        final List<Trigger> triggers = asList(trigger);
        ExtensionDraft extensionDraft = ExtensionDraftBuilder.of(name, destination, triggers).get();
        sphereClient.executeBlocking(ExtensionCreateCommand.of(extensionDraft));
    }

    public void removeExtension(String extensionName) {
        ExtensionQuery q = ExtensionQuery.of().withPredicates(extensionQueryModel -> extensionQueryModel.key().is(extensionName));
        List<Extension> extensions = sphereClient.executeBlocking(q).getResults();
        extensions.forEach(extension -> {
            final long version = extension.getVersion();
            sphereClient.executeBlocking(ExtensionDeleteCommand.ofKey(extensionName, version));
        });
    }
}
