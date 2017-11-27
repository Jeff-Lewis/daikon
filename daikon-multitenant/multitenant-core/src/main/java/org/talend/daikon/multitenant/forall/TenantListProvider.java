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

import org.talend.daikon.multitenant.core.Tenant;

import java.util.List;

/**
 * Provides a list of tenants
 */
public interface TenantListProvider {

    /**
     * @return a list of tenants
     */
    List<Tenant> getTenants();

}
