package base

import config.TestExecutionConfig
import spock.lang.*
import support.logs.CustomLog
import support.myspock.CustomErrorListener

@Retry(mode = Retry.Mode.SETUP_FEATURE_CLEANUP, count = 1)
@Stepwise
@Unroll
abstract class BaseSpecification extends Specification {

    @Shared def currentSpec = this.specificationContext.currentSpec
    @Shared String specFullName = "${currentSpec.package}.${currentSpec.name}".toString()
    @Shared def specCounter = TestExecutionConfig.specCounter.getAndIncrement()
    @Shared def specsCount = TestExecutionConfig.specLevelCountDetailsList.size()

    def setupSpec() {
        CustomLog.info("${System.lineSeparator()}Started specification # ${specCounter} out of total ${specsCount} - ${specFullName}")
        CustomLog.attachLogsToAllure('global-setupSpec-logs')
    }

    def cleanupSpec() {
        //custom error listener to report its status
        def errorListener = currentSpec.listeners.find { it instanceof CustomErrorListener }
        def status = errorListener && (errorListener as CustomErrorListener).errorInfo ? 'FAILED' : 'PASSED'
        CustomLog.info("${System.lineSeparator()}Ended (status - ${status}) specification # ${specCounter} out of total ${specsCount} - ${specFullName}")
        CustomLog.attachLogsToAllure('global-cleanupSpec-logs')
    }
}