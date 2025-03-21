package support.myspock

import config.TestExecutionConfig
import io.qameta.allure.ConfigurationBuilder
import io.qameta.allure.ReportGenerator
import org.apache.groovy.datetime.extensions.DateTimeExtensions
import org.spockframework.runtime.SpockException
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.extension.ISpockExecution
import org.spockframework.runtime.model.SpecInfo
import java.nio.file.Paths
import java.time.LocalDateTime

class CustomGlobalExtension implements IGlobalExtension {

    @Override
    void executionStart(ISpockExecution spockExecution) {
        TestExecutionConfig.checkTestExecutionConfigParamsValidity()

        if(!TestExecutionConfig.specLevelCountDetailsList) {
            throw new SpockException("No specifications to run, cannot proceed")
        }

        def sb = new StringBuilder()
        sb.append(System.lineSeparator())
        sb.append("Starting test execution...")

        def allFeatureCount = TestExecutionConfig.specLevelCountDetailsList.sum { it.featureCount }
        sb.append(System.lineSeparator())
        sb.append("Count of specifications to be executed in this session: ${TestExecutionConfig.specLevelCountDetailsList.size()}").append(System.lineSeparator())
        sb.append("Count of total features (wihout cosidering data table iterations) to be executed in this session: ${allFeatureCount}").append(System.lineSeparator())

        if(new File('allure-results').deleteDir()) {
            sb.append("Previous allure-results files, if any, are deleted").append(System.lineSeparator()) //delete existing allure- results files since their report was already created previously
        }

        if(TestExecutionConfig.skippedSpecsWithReasonList) {
            sb.append(System.lineSeparator()).append(System.lineSeparator())
            sb.append("Below specifications are intentionally skipped from running:").append(System.lineSeparator())
            TestExecutionConfig.skippedSpecsWithReasonList.unique().each {
                sb.append(it).append(System.lineSeparator())
            }
            sb.append(System.lineSeparator())
        }

        println(sb) //make execution progress visible
    }


    @Override
    void executionStop(ISpockExecution spockExecution) {
        def sb = new StringBuilder()
        sb.append(System.lineSeparator())
        sb.append("Ending test execution...").append(System.lineSeparator())

        //generate allure report at the end, use result files from default allure-results directory location
        def reportGenerator = new ReportGenerator(new ConfigurationBuilder().useDefault().build())
        def timestamp = DateTimeExtensions.format(LocalDateTime.now(), 'yyyy-MM-dd-HHmmss')
        def allureReportDir = "testReports/${timestamp}/allure-report"
        reportGenerator.generateSingleFile(Paths.get(allureReportDir), [Paths.get('allure-results')])

        //show the allure report location at the end
        sb.append("Allure report generated at: ${allureReportDir}")append(System.lineSeparator())
        sb.append(System.lineSeparator())
        println(sb)
    }

    @Override
    void visitSpec(SpecInfo spec) {
        def countAcquired = SpecAnalyzer.fillSpecLevelCountDetails(spec)
        if(TestExecutionConfig.LOCAL_RUN || !countAcquired) {
            spec.addInterceptor(new CustomMethodInterceptor()) //intercept specExecution

            spec.features.each {
                it.addInterceptor(new CustomMethodInterceptor()) //intercept featureExecution
                it.addCleanupInterceptor(new CustomMethodInterceptor()) //intercept cleanupMethod
                it.addIterationInterceptor(new CustomMethodInterceptor()) //intercept iterationExecution
            }
        }
    }
}