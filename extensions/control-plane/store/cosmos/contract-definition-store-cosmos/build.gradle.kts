/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
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

plugins {
    `java-library`
}

dependencies {
    api(project(":spi:control-plane:contract-spi"))
    api(project(":core:common:util"))
    api(project(":extensions:common:azure:azure-cosmos-core"))

    implementation(libs.azure.cosmos)
    implementation(libs.failsafe.core)

    testImplementation(testFixtures(project(":spi:control-plane:contract-spi")))
    testImplementation(testFixtures(project(":extensions:common:azure:azure-test")))
}


publishing {
    publications {
        create<MavenPublication>("contract-definition-store-cosmos") {
            artifactId = "contract-definition-store-cosmos"
            from(components["java"])
        }
    }
}