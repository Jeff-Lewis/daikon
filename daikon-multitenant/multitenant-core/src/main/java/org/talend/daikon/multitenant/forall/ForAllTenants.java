// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.multitenant.forall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.talend.daikon.logging.event.field.MdcKeys;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Multi-tenant implementation of {@link ForAll} that will query
 * a {@link TenantListProvider} to retrieve a list of tenants
 */
public class ForAllTenants implements ForAll {

    private Logger LOGGER = LoggerFactory.getLogger(ForAllTenants.class);

    private final TenantListProvider tenantListProvider;

    public ForAllTenants(TenantListProvider tenantListProvider) {
        LOGGER.info("ForAll: multi tenancy enabled.");
        this.tenantListProvider = tenantListProvider;
    }

    @Override
    public void execute(Supplier<Boolean> condition, Runnable runnable) {
        List<Tenant> tenants = Collections.emptyList();
        try {
            LOGGER.debug("Running runnable '{}' for all tenants ...", runnable);
            tenants = tenantListProvider.getTenants();
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve all tenants.", runnable, e);
        }

        for (Tenant tenant : tenants) {
            final Tenant previousTenant = TenancyContextHolder.getContext().getTenant();
            try {
                TenancyContextHolder.getContext().setTenant(tenant);
                MDC.put(MdcKeys.ACCOUNT_ID, String.valueOf(tenant.getIdentity()));
                LOGGER.debug("Running for tenant '{}'...", tenant);
                if (condition.get()) {
                    runnable.run();
                } else {
                    LOGGER.warn("Unable to execute runnable '{}' for tenant '{}'", runnable, tenant);
                }
            } catch (Exception e) {
                LOGGER.error("Unable to execute '{}' for tenant '{}'.", runnable, tenant, e);
            } finally {
                TenancyContextHolder.getContext().setTenant(previousTenant);
                MDC.remove(MdcKeys.ACCOUNT_ID);
                LOGGER.debug("Done running '{}' for tenant '{}'.", runnable, tenant);
            }
        }
        LOGGER.debug("Execution for all tenants complete");
    }
}
