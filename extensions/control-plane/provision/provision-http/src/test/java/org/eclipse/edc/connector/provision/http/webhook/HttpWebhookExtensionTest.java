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

package org.eclipse.edc.connector.provision.http.webhook;

import org.eclipse.edc.api.auth.spi.AuthenticationRequestFilter;
import org.eclipse.edc.api.auth.spi.AuthenticationService;
import org.eclipse.edc.connector.provision.http.HttpWebhookExtension;
import org.eclipse.edc.connector.transfer.spi.TransferProcessManager;
import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.Hostname;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.eclipse.edc.spi.system.injection.ObjectFactory;
import org.eclipse.edc.web.spi.WebServer;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.WebServiceConfigurer;
import org.eclipse.edc.web.spi.configuration.WebServiceConfigurerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(DependencyInjectionExtension.class)
class HttpWebhookExtensionTest {

    private HttpWebhookExtension extension;
    private ServiceExtensionContext context;
    private WebServer mockWebServer;
    private WebService mockWebService;

    @BeforeEach
    void setup(ServiceExtensionContext context, ObjectFactory factory) {

        mockWebServer = mock(WebServer.class);
        mockWebService = mock(WebService.class);
        context.registerService(WebServer.class, mockWebServer);
        context.registerService(WebService.class, mockWebService);
        context.registerService(Hostname.class, () -> "localhost");
        context.registerService(TransferProcessManager.class, mock(TransferProcessManager.class));
        context.registerService(AuthenticationService.class, mock(AuthenticationService.class));
        context.registerService(WebServiceConfigurer.class, new WebServiceConfigurerImpl());

        this.context = spy(context); //used to inject the config
        when(this.context.getMonitor()).thenReturn(mock(Monitor.class));

        extension = factory.constructInstance(HttpWebhookExtension.class);
    }

    @Test
    void initialize_withDefault() {
        when(context.getConfig("web.http.provisioner")).thenReturn(ConfigFactory.fromMap(Map.of()));

        extension.initialize(context);

        verify(mockWebServer).addPortMapping(eq("provisioner"), eq(8383), eq("/api/v1/provisioner"));
        verify(mockWebService).registerResource(eq("provisioner"), isA(HttpProvisionerWebhookApiController.class));
        verify(mockWebService).registerResource(eq("provisioner"), isA(AuthenticationRequestFilter.class));
        verifyNoMoreInteractions(mockWebServer, mockWebService);
    }

    @Test
    void initialize_withCustomSettings() {
        when(context.getConfig("web.http.provisioner")).thenReturn(ConfigFactory.fromMap(Map.of(
                "port", "4242",
                "path", "/api/v1/someotherpath/webhook"
        )));

        extension.initialize(context);

        verify(mockWebServer, never()).addPortMapping(anyString(), anyInt(), anyString());
        verify(mockWebService).registerResource(eq("provisioner"), isA(HttpProvisionerWebhookApiController.class));
        verify(mockWebService).registerResource(eq("provisioner"), isA(AuthenticationRequestFilter.class));
        verifyNoMoreInteractions(mockWebServer, mockWebService);
    }

    @Test
    void initialize_onlyWithPort() {
        when(context.getConfig("web.http.provisioner")).thenReturn(ConfigFactory.fromMap(Map.of(
                "port", "4242"
        )));

        extension.initialize(context);

        verify(mockWebServer, never()).addPortMapping(anyString(), anyInt(), anyString());
        verify(mockWebService).registerResource(eq("provisioner"), isA(HttpProvisionerWebhookApiController.class));
        verify(mockWebService).registerResource(eq("provisioner"), isA(AuthenticationRequestFilter.class));
        verifyNoMoreInteractions(mockWebServer, mockWebService);
    }

    @Test
    void initialize_verifyHostname() {
        when(context.getConfig("web.http.provisioner")).thenReturn(ConfigFactory.fromMap(Map.of(
                "port", "4242"
        )));

        extension.initialize(context);

        verify(mockWebServer, never()).addPortMapping(anyString(), anyInt(), anyString());
        verify(mockWebService).registerResource(eq("provisioner"), isA(HttpProvisionerWebhookApiController.class));
        verify(mockWebService).registerResource(eq("provisioner"), isA(AuthenticationRequestFilter.class));
        verifyNoMoreInteractions(mockWebServer, mockWebService);
    }

    @Test
    void initialize_onlyWithPath() {
        when(context.getConfig("web.http.provisioner")).thenReturn(ConfigFactory.fromMap(Map.of(
                "path", "/api/v1/someotherpath/webhook"
        )));

        extension.initialize(context);

        verify(mockWebServer, never()).addPortMapping(anyString(), anyInt(), anyString());
        verify(mockWebService).registerResource(eq("provisioner"), isA(HttpProvisionerWebhookApiController.class));
        verify(mockWebService).registerResource(eq("provisioner"), isA(AuthenticationRequestFilter.class));
        verifyNoMoreInteractions(mockWebServer, mockWebService);
    }
}
