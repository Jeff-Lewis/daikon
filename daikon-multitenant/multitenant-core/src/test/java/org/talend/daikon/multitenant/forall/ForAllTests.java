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

import org.junit.Test;
import org.slf4j.MDC;
import org.talend.daikon.logging.event.field.MdcKeys;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.provider.DefaultTenant;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class ForAllTests {

    @Test
    public void testDefaultForAll() {
        ObservableRunnable runnable = new ObservableRunnable();
        DefaultForAll forAll = new DefaultForAll();
        forAll.execute(() -> true, runnable);
        assertEquals(1, runnable.getExecutionCounts());

        forAll.execute(() -> false, runnable);
        assertEquals(1, runnable.getExecutionCounts());
    }

    @Test
    public void testForAllTenants() {
        ObservableRunnable runnable = new ObservableRunnable();
        runnable.check = (i) -> {
            String tenant = "Tenant " + i;
            assertEquals(tenant, TenancyContextHolder.getContext().getTenant().getIdentity());
            assertEquals(tenant, MDC.get(MdcKeys.ACCOUNT_ID));
        };

        int nbTenants = 10;
        TenantListProvider provider = () -> IntStream.range(0, nbTenants).mapToObj(i -> "Tenant " + i)
                .map(s -> new DefaultTenant(s, null)).collect(Collectors.toList());

        ForAllTenants forAll = new ForAllTenants(provider);

        forAll.execute(() -> false, runnable);

        assertEquals(0, runnable.getExecutionCounts());

        forAll.execute(() -> true, runnable);
    }

    private static class ObservableRunnable implements Runnable {

        private int executionCounts = 0;

        public Consumer<Integer> check = i -> {
        };

        @Override
        public void run() {
            check.accept(executionCounts);
            executionCounts++;
        }

        public int getExecutionCounts() {
            return executionCounts;
        }
    }
}
