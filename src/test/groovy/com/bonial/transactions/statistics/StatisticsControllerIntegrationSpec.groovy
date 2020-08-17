package com.bonial.transactions.statistics

import com.bonial.transactions.statistics.cache.StatisticsStore
import com.bonial.transactions.statistics.dto.TransactionDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import java.time.Instant

import static org.hamcrest.CoreMatchers.equalTo
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.HttpStatus.OK

@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = "integration-test")
class StatisticsControllerIntegrationSpec extends IntegrationTestSpecification {
    private static final String RESOURCE_BASE_PATH = '/v1/statistics'
    @Autowired
    private StatisticsStore statisticsStore

    def cleanup() {
        statisticsStore.invalidateCache()
    }

    def 'get statistics of last minute'() {
        //@formatter:off
        given:
        storeTransaction()
        when:
        def response = this.getEndpoint()
                .headers(buildHeaders())
                .get(RESOURCE_BASE_PATH)
                .prettyPeek()
        then:
        response.then()
                .statusCode(OK.value())
                .body('sum', equalTo(11310.0f))
                .body('avg', equalTo(565.5f))
                .body('max', equalTo(575.0f))
                .body('min', equalTo(556.0f))
                .body('count', equalTo(20))
        //@formatter:on
    }

    def 'get statistics should ignore transaction older than one minute'() {
        //@formatter:off
        given:
        'store valid transaction'
        storeTransaction()
        and: 'store older transaction'
        Instant instant = Instant.now().minusSeconds(120)
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .amount(new BigDecimal(500))
                .timestamp(instant.toEpochMilli())
                .build()
        statisticsStore.store(transactionDTO)
        when:
        def response = this.getEndpoint()
                .headers(buildHeaders())
                .get(RESOURCE_BASE_PATH)
                .prettyPeek()
        then:
        response.then()
                .statusCode(OK.value())
                .body('sum', equalTo(11310.0f))
                .body('avg', equalTo(565.5f))
                .body('max', equalTo(575.0f))
                .body('min', equalTo(556.0f))
                .body('count', equalTo(20))
        //@formatter:on
    }

    def 'get statistics should return empty when no transaction exist'() {
        //@formatter:off
        when:
        def response = this.getEndpoint()
                .headers(buildHeaders())
                .get(RESOURCE_BASE_PATH)
                .prettyPeek()
        then:
        response.then()
                .statusCode(OK.value())
                .body('sum', equalTo(0.0f))
                .body('avg', equalTo(0.0f))
                .body('max', equalTo(0.0f))
                .body('min', equalTo(0.0f))
                .body('count', equalTo(0))
        //@formatter:on
    }

    private void storeTransaction() {
        (1..20).each {
            def transaction = buildTransaction(it)
            statisticsStore.store(transaction)
        }
    }

    def buildTransaction(int index) {
        Instant instant = Instant.now()
        def amount = index + 555
        return TransactionDTO.builder()
                .amount(new BigDecimal(amount))
                .timestamp(instant.toEpochMilli())
                .build()
    }
}
