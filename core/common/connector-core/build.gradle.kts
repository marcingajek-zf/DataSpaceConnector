/*
 *  Copyright (c) 2021 - 2022 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - initial API and implementation
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":spi:common:core-spi"))
    api(project(":spi:common:policy-engine-spi"))
    api(project(":spi:common:transaction-spi"))
    api(project(":spi:common:transaction-datasource-spi"))

    implementation(project(":core:common:policy-engine"))
    implementation(project(":core:common:util"))

    api(libs.okhttp)
    api(libs.failsafe.core)
    implementation(libs.bouncyCastle.bcpkix)

    testImplementation(project(":core:common:junit"))
    testImplementation(libs.awaitility)
    testImplementation(libs.junit.jupiter.api)
}

publishing {
    publications {
        create<MavenPublication>("connector-core") {
            artifactId = "connector-core"
            from(components["java"])
        }
    }
}
