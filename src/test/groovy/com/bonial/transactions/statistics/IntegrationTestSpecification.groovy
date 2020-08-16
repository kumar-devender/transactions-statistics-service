package com.bonial.transactions.statistics

import groovy.transform.CompileStatic
import io.restassured.http.Header
import io.restassured.http.Headers
import io.restassured.specification.RequestSpecification
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

class IntegrationTestSpecification extends Specification {
    @Value('http://localhost')
    protected String host

    @Value('${local.server.port}')
    protected int port

    @CompileStatic
    RequestSpecification getEndpoint() {
        io.restassured.RestAssured.given()
                .baseUri(host)
                .port(port)
    }

    def buildHeaders() {
        new Headers(Arrays.asList(new Header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)))
    }
}
