package net.andreinc.commercetools.enricher.enrichers.customer;

import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.client.BlockingSphereClient;
import net.andreinc.commercetools.enricher.model.CustomerEnricher;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CustomerEmailEnricher extends CustomerEnricher {

    static Logger logger = LoggerFactory.getLogger(CustomerEmailEnricher.class.getCanonicalName());

    EmailValidator emailValidator = EmailValidator.getInstance(true);

    @Override
    public void enrich(BlockingSphereClient client, JsonNode data) {

        System.out.println(data);
        String email = data.get("email").textValue();
        logger.info("Validating client email: {}", email);

        if (!emailValidator.isValid(email)) {
            throw new IllegalArgumentException("Invalid email");
        }
    }

}
