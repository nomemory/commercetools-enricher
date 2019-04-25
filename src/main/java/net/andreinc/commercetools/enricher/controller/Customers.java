package net.andreinc.commercetools.enricher.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerDraftBuilder;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import net.andreinc.commercetools.enricher.model.CustomerEnricher;
import net.andreinc.commercetools.enricher.model.Enricher;
import net.andreinc.commercetools.enricher.service.EnricherExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static io.sphere.sdk.extensions.TriggerType.CREATE;
import static io.sphere.sdk.extensions.TriggerType.UPDATE;

@RestController
public class Customers {

    Logger logger = LoggerFactory.getLogger(Customers.class.getCanonicalName());

    private final String CREATE_ACTION = "Create";
    private final String UPDATE_ACTION = "Update";

    @Autowired
    EnricherExecutorService enricherExecutorService;

    @Autowired
    BlockingSphereClient sphereClient;

    @Autowired
    List<CustomerEnricher> customerEnrichers;

    List<Enricher> createEnrichers;

    List<Enricher> updateEnrichers;

    @PostConstruct
    public void initCreateEnrichers() {
        this.createEnrichers = customerEnrichers.stream()
                                        .filter(e -> e.getTriggerTypes().contains(CREATE))
                                        .collect(Collectors.toList());
    }

    @PostConstruct
    public void initUpdateEnrichers() {
        this.updateEnrichers = customerEnrichers.stream()
                                        .filter(e -> e.getTriggerTypes().contains(UPDATE))
                                        .collect(Collectors.toList());
    }

    @PostConstruct
    public void logCustomerEnrichers() {
        logger.info("Identified the following Customer Enricher.");
        logger.info("\t 'Create': {}", createEnrichers);
        logger.info("\t 'Update': {}", updateEnrichers);
    }

    @PostMapping(value = "/enrich/customer/", produces = "text/json")
    public ResponseEntity<String> customerCreate(@RequestBody String requestBody) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode node = objectMapper.readTree(requestBody);
        String action = node.get("action").asText();
        JsonNode customer = node.get("resource").get("obj");

        if (CREATE_ACTION.equals(action)) {
            enricherExecutorService.execute(createEnrichers, customer);
        } else if (UPDATE_ACTION.equals(action)) {
            enricherExecutorService.execute(updateEnrichers, customer);
        } else {
            throw new IllegalArgumentException("Invalid action response coming from Commerce Tools. Action=" + action);
        }

        return ResponseEntity.ok("");
    }

    @GetMapping("/test/customer")
    public ResponseEntity testCustomer(@RequestParam("email") String email) {

        logger.info("Create customer request.");

        CustomerDraft customerDraft = CustomerDraftBuilder
                .of(email, "123456")
                .get();

        CustomerSignInResult signIn =
                sphereClient.executeBlocking(CustomerCreateCommand.of(customerDraft));

        return ResponseEntity.ok().body("");
    }
}
