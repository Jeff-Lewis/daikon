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
package org.talend.daikon.multitenant.spring.forall;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.multitenant.forall.DefaultForAll;
import org.talend.daikon.multitenant.forall.ForAll;
import org.talend.daikon.multitenant.forall.ForAllTenants;
import org.talend.daikon.multitenant.forall.TenantListProvider;

@Configuration
public class ForAllConfiguration {

    @ConditionalOnBean(TenantListProvider.class)
    @Bean
    public ForAll forAllTenants(TenantListProvider tenantProvider) {
        return new ForAllTenants(tenantProvider);
    }

    @ConditionalOnMissingBean(ForAll.class)
    @Bean
    public ForAll defaultForAll() {
        return new DefaultForAll();
    }

}
