package net.andreinc.commercetools.enricher;

import net.andreinc.commercetools.enricher.service.ExtensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class WhenApplicationReady {

    Logger logger = LoggerFactory.getLogger(WhenApplicationReady.class.getCanonicalName());

    @Autowired
    ExtensionService extensionService;

    @Value("${customer.extension.name}")
    String customerExtensionName;

    @Value("${customer.extension.url}")
    String customerExtensionUrl;


    @EventListener(ApplicationReadyEvent.class)
    public void enrich() {

        // Remove extensions
        logger.info("Removing previous API Extension called '{}'.", customerExtensionName);
        extensionService.removeCustomerExtension();

        // Add extensions
        logger.info("Adding new API Extension '{}' pointing to '{}'.", customerExtensionName, customerExtensionUrl);
        extensionService.addCustomerExtension();
    }
}
