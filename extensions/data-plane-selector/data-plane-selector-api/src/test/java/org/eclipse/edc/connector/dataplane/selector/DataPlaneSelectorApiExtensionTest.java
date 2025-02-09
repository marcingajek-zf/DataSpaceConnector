/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.edc.connector.dataplane.selector;

import org.eclipse.edc.connector.dataplane.selector.api.DataplaneSelectorApiController;
import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelector;
import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelectorService;
import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.connector.dataplane.selector.spi.strategy.SelectionStrategyRegistry;
import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.injection.ObjectFactory;
import org.eclipse.edc.web.spi.WebService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(DependencyInjectionExtension.class)
class DataPlaneSelectorApiExtensionTest {
    private DataPlaneSelectorApiExtension extension;
    private WebService webServiceMock;
    private ServiceExtensionContext context;

    @BeforeEach
    void setUp(ServiceExtensionContext context, ObjectFactory factory) {
        this.context = context;
        webServiceMock = mock(WebService.class);
        context.registerService(WebService.class, webServiceMock);
        context.registerService(DataPlaneSelectorService.class, new DataPlaneSelectorServiceImpl(mock(DataPlaneSelector.class),
                mock(DataPlaneInstanceStore.class), mock(SelectionStrategyRegistry.class)));

        extension = factory.constructInstance(DataPlaneSelectorApiExtension.class);
    }

    @Test
    void initialize() {
        extension.initialize(context);

        verify(webServiceMock).registerResource(eq("dataplane"), isA(DataplaneSelectorApiController.class));
    }
}