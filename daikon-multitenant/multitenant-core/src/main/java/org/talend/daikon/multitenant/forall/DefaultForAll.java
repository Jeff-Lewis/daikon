package org.talend.daikon.multitenant.forall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * A fall back implementation of {@link ForAll} in case code is running with no tenancy enabled.
 */
public class DefaultForAll implements ForAll {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultForAll.class);

    public DefaultForAll() {
        LOGGER.info("ForAll: multi tenancy disabled.");
    }

    public void execute(Supplier<Boolean> condition, Runnable runnable) {
        try {
            if (condition.get()) {
                runnable.run();
            } else {
                LOGGER.debug("Unable to run '{}' (condition disallowed run of it).", runnable);
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to execute run '{}'. Skip execution.", runnable);
            LOGGER.debug("Unable to execute run '{}'. Skip execution error.", e);
        }
    }
}
