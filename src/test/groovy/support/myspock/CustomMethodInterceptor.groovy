package support.myspock

import io.qameta.allure.Allure
import io.qameta.allure.model.Label
import io.qameta.allure.model.TestResult
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import support.logs.CustomLog

class CustomMethodInterceptor extends AbstractMethodInterceptor {

    @Override
    void interceptFeatureExecution(IMethodInvocation invocation) {
        //if the feature have where block and data table
        if(invocation.feature.dataProcessorMethod) {
            def customSpecAnnotation = invocation.spec.getAnnotation(CustomSpec.class)
            if(customSpecAnnotation && customSpecAnnotation.onlyRunIterationIndices()) {
                //custom data-driver which will ensure that, only specific iterations, if provided, will be executed
                invocation.feature.setDataDriver(new IterationFilterDataDriver(customSpecAnnotation.onlyRunIterationIndices()))
            }
        }

        invocation.proceed() //continue
    }

    @Override
    void interceptSpecExecution(IMethodInvocation invocation) throws Throwable {
        invocation.spec.addListener(new CustomErrorListener()) //progress traker of failed specifications
        invocation.proceed()
    }

    @Override
    void interceptCleanupMethod(IMethodInvocation invocation) throws Throwable {
        CustomLog.attachLogsToAllure('feature-logs') //add logs of a feature to the allure report
        invocation.proceed()
    }

    @Override
    void interceptIterationExecution(IMethodInvocation invocation) throws Throwable {
        def expSpecParentSuiteName = invocation.spec.package
        Allure.lifecycle.updateTestCase { TestResult testResult ->
            def parentSuiteUpdateNeeded = testResult.getLabels().removeIf { it.name == 'parentSuite' && it.value != expSpecParentSuiteName }
            if(parentSuiteUpdateNeeded) {
                testResult.labels.add(new Label().setName('parentSuite').setValue(expSpecParentSuiteName)) //meaningful parent suite name instead of BaseSpecification
            }
            testResult
        }

        invocation.proceed()
    }
}