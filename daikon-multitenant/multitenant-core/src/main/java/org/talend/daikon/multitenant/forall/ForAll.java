package org.talend.daikon.multitenant.forall;

import java.util.function.Supplier;

/**
 * An interface to abstract repetitive actions (to be performed on all tenants for example).
 */
public interface ForAll {

    /**
     * Execute the provided <code>runnable</code> for all tenants.
     *
     * @param runnable The {@link Runnable} to execute.
     */
    void execute(final Supplier<Boolean> condition, Runnable runnable);

    /**
     * Execute the provided <code>runnable</code> for all tenants.
     *
     * @param runnable The {@link Runnable} to execute.
     */
    default void execute(Runnable runnable) {
        execute(() -> true, runnable);
    }

}
