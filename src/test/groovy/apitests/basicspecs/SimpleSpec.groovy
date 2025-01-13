package apitests.basicspecs

import base.BaseSpecification
import config.TestExecutionConfig
import spock.lang.Shared
import support.logs.CustomLog
import support.myspock.CustomSpec
import utils.TestData

@CustomSpec(onlyRunIterationIndices = [0,2], ignoreIfCountryNotIn = [TestExecutionConfig.Country.us])
class SimpleSpec extends BaseSpecification {
    @Shared TestData testData = new TestData()

    def "feature 1"() {
        given:
            CustomLog.info("inside feature 1:  number1: ${testData.number1}, number2: ${testData.number2}")
        expect:
            true
    }

    def "data driven feature"() {
        when:
            CustomLog.info("inside data-driven feature:  col1: ${col1}, col2: ${col2} at ${new Date()}")

        then:
            col1 + col2 == col3

        where:
            col1 | col2 | col3
            1    | 2    | 3
            2    | 3    | 5
            3    | 4    | 7
            4    | 5    | 9
            5    | 6    | 11
    }
}