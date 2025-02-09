/*
 *  Copyright (c) 2021 Microsoft Corporation
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
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

dependencies {
    api(project(":spi:common:core-spi"))
    api(project(":spi:common:web-spi"))
    api(project(":spi:federated-catalog:federated-catalog-spi"))

    implementation(project(":core:common:util"))
    implementation(project(":core:common:connector-core"))

    implementation(libs.okhttp)

    implementation(libs.jakarta.rsApi)
    implementation(libs.failsafe.core)

    // required for integration test
    testImplementation(project(":core:common:junit"))
    testImplementation(project(":extensions:common:http"))
    testImplementation(project(":data-protocols:ids:ids-spi"))
    testImplementation(libs.awaitility)

}

publishing {
    publications {
        create<MavenPublication>("federated-catalog-core") {
            artifactId = "federated-catalog-core"
            from(components["java"])
        }
    }
}
