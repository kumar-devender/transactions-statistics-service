package com.bonial.transactions.statistics

import com.bonial.transactions.statistics.dto.TransactionDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import java.time.Instant

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.HttpStatus.*

@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = "integration-test")
class TransactionControllerIntegrationTest extends IntegrationTestSpecification {
    private static final String RESOURCE_BASE_PATH = '/v1/transactions'

    def 'create transaction with correct time stamp'() {
        //@formatter:off
        given:
        def transaction = buildTransactionDTO(Instant.now())
        when:
        def response = this.getEndpoint()
            .headers(buildHeaders())
            .body(transaction)
            .post(RESOURCE_BASE_PATH)
            .prettyPeek()
        then:
        response.then()
            .statusCode(CREATED.value())
        //@formatter:on
    }

    def 'create transaction with expired correct time stamp'() {
        //@formatter:off
        given:
        Instant instant = Instant.now().minusSeconds(120L)
        def transaction = buildTransactionDTO(instant)
        when:
        def response = this.getEndpoint()
            .headers(buildHeaders())
            .body(transaction)
            .post(RESOURCE_BASE_PATH)
            .prettyPeek()
        then:
        response.then()
            .statusCode(NO_CONTENT.value())
        //@formatter:on
    }

    def 'create transaction with null amount'() {
        //@formatter:off
        given:
        Instant instant = Instant.now()
        def transaction = buildTransactionDTO(instant)
        transaction.setAmount(null)
        when:
        def response = this.getEndpoint()
            .headers(buildHeaders())
            .body(transaction)
            .post(RESOURCE_BASE_PATH)
            .prettyPeek()
        then:
        response.then()
            .statusCode(BAD_REQUEST.value())
        //@formatter:on
    }

    def buildTransactionDTO(Instant instant) {
        println " request time stamp " + instant.toEpochMilli()
        return TransactionDTO.builder()
                .amount(new BigDecimal(555.50))
                .timestamp(instant.toEpochMilli())
                .build()
    }
}
