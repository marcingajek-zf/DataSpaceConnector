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
    `java-test-fixtures`
}

dependencies {
    api(project(":spi:common:core-spi"))
    api(project(":core:common:util"))

    implementation(libs.azure.cosmos)
    implementation(libs.failsafe.core)


    testImplementation(testFixtures(project(":extensions:common:azure:azure-test")))

    testFixturesImplementation(libs.azure.cosmos)
}


publishing {
    publications {
        create<MavenPublication>("azure-cosmos-core") {
            artifactId = "azure-cosmos-core"
            from(components["java"])
        }
    }
}