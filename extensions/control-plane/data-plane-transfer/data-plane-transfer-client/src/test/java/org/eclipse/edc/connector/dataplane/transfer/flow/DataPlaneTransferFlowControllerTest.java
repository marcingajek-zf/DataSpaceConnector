/*
 *  Copyright (c) 2022 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - add test for properties mapping
 *
 */

package org.eclipse.edc.connector.dataplane.transfer.flow;

import org.eclipse.edc.connector.dataplane.transfer.spi.client.DataPlaneTransferClient;
import org.eclipse.edc.connector.transfer.spi.callback.ControlPlaneApiUrl;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.response.ResponseStatus;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.connector.dataplane.transfer.spi.DataPlaneTransferConstants.HTTP_PROXY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataPlaneTransferFlowControllerTest {


    private DataPlaneTransferClient transferClientMock;

    private DataPlaneTransferFlowController flowController;

    private ControlPlaneApiUrl callbackUrl;

    private static DataAddress testDataAddress() {
        return DataAddress.Builder.newInstance().type(UUID.randomUUID().toString()).build();
    }

    public static DataRequest createDataRequest() {
        return createDataRequest(UUID.randomUUID().toString());
    }

    /**
     * Create a {@link DataRequest} object with provided destination type.
     */
    public static DataRequest createDataRequest(String destinationType) {
        return createDataRequestBuilder(destinationType).build();
    }

    /**
     * Create a {@link DataRequest} object with provided destination type and additional properties.
     */
    public static DataRequest createDataRequestWithProperties(String destinationType, Map<String, String> properties) {
        return createDataRequestBuilder(destinationType)
                .properties(properties)
                .build();
    }

    private static DataRequest.Builder createDataRequestBuilder(String destinationType) {
        return DataRequest.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .protocol(UUID.randomUUID().toString())
                .contractId(UUID.randomUUID().toString())
                .assetId(UUID.randomUUID().toString())
                .connectorAddress("test.connector.address")
                .processId(UUID.randomUUID().toString())
                .destinationType(destinationType);
    }

    @BeforeEach
    public void setUp() throws MalformedURLException {
        transferClientMock = mock(DataPlaneTransferClient.class);
        callbackUrl = mock(ControlPlaneApiUrl.class);
        var url = new URL("http://localhost");
        when(callbackUrl.get()).thenReturn(url);
        flowController = new DataPlaneTransferFlowController(transferClientMock, callbackUrl);
    }

    @Test
    void canHandle() {
        var contentAddress = DataAddress.Builder.newInstance().type(HTTP_PROXY).build();
        assertThat(flowController.canHandle(createDataRequest(), contentAddress)).isTrue();
        assertThat(flowController.canHandle(createDataRequest(HTTP_PROXY), contentAddress)).isFalse();
    }

    @Test
    void transferFailure_shouldReturnFailedTransferResult() {
        var errorMsg = UUID.randomUUID().toString();
        var request = createDataRequest();

        when(transferClientMock.transfer(any())).thenReturn(StatusResult.failure(ResponseStatus.FATAL_ERROR, errorMsg));

        var policy = Policy.Builder.newInstance().build();

        var result = flowController.initiateFlow(request, testDataAddress(), policy);

        verify(transferClientMock, times(1)).transfer(any());

        assertThat(result.failed()).isTrue();
        assertThat(result.getFailureMessages()).allSatisfy(s -> assertThat(s).contains(errorMsg));
    }

    @Test
    void transferSuccess() {
        var request = createDataRequest();
        var source = testDataAddress();

        var dfrCapture = ArgumentCaptor.forClass(DataFlowRequest.class);
        when(transferClientMock.transfer(any())).thenReturn(StatusResult.success());

        var policy = Policy.Builder.newInstance().build();

        var result = flowController.initiateFlow(request, source, policy);

        verify(transferClientMock, times(1)).transfer(dfrCapture.capture());

        assertThat(result.succeeded()).isTrue();
        var dataFlowRequest = dfrCapture.getValue();
        assertThat(dataFlowRequest.isTrackable()).isTrue();
        assertThat(dataFlowRequest.getProcessId()).isEqualTo(request.getProcessId());
        assertThat(dataFlowRequest.getSourceDataAddress().getType()).isEqualTo(source.getType());
        assertThat(dataFlowRequest.getDestinationDataAddress().getType()).isEqualTo(request.getDestinationType());
        assertThat(dataFlowRequest.getProperties()).isEmpty();
        assertThat(dataFlowRequest.getCallbackAddress()).isNotNull();
    }

    @Test
    void transferSuccess_additionalProperties() {
        var properties = Map.of("propertyKey", "propertyValue");
        var request = createDataRequestWithProperties(UUID.randomUUID().toString(), properties);
        var source = testDataAddress();

        var dfrCapture = ArgumentCaptor.forClass(DataFlowRequest.class);
        when(transferClientMock.transfer(any())).thenReturn(StatusResult.success());

        var policy = Policy.Builder.newInstance().build();

        var result = flowController.initiateFlow(request, source, policy);

        verify(transferClientMock, times(1)).transfer(dfrCapture.capture());

        assertThat(result.succeeded()).isTrue();
        var dataFlowRequest = dfrCapture.getValue();
        assertThat(dataFlowRequest.isTrackable()).isTrue();
        assertThat(dataFlowRequest.getProcessId()).isEqualTo(request.getProcessId());
        assertThat(dataFlowRequest.getSourceDataAddress().getType()).isEqualTo(source.getType());
        assertThat(dataFlowRequest.getDestinationDataAddress().getType()).isEqualTo(request.getDestinationType());
        assertThat(dataFlowRequest.getProperties()).containsAllEntriesOf(properties);
    }
}
