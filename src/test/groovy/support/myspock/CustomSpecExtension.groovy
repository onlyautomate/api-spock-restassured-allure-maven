package support.myspock

import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo
import config.TestExecutionConfig

class CustomSpecExtension implements IAnnotationDrivenExtension<CustomSpec> {

    @Override
    void visitSpecAnnotation(CustomSpec annotation, SpecInfo spec) {
        def specFullName = "${spec.package}.${spec.name}".toString()
        if(annotation.ignoreIfEnvIn()) {
            if((annotation.ignoreIfEnvIn() as List).collect { it.name() }.contains(TestExecutionConfig.ENVIRONMENT)) {
                def skipReason = "Specification '${specFullName}' skipped due to non-applicable environment '${TestExecutionConfig.ENVIRONMENT}'"
                TestExecutionConfig.skippedSpecsWithReasonList.add(skipReason) //to be printed later for notification purposes
                spec.skip(skipReason) //will not get executed anymore
            }
        } else if(annotation.ignoreIfCountryNotIn()) {
            def applicableCountry = TestExecutionConfig.COUNTRIES.any { String country ->
                (annotation.ignoreIfCountryNotIn() as List).collect { TestExecutionConfig.Country country1 ->
                    country1.name()
                }.contains(country)
            }

            if(!applicableCountry) {
                def skipReason = "Specification '${specFullName}' is to be skipped due to non-applicable country(s) '${TestExecutionConfig.COUNTRIES}'"
                TestExecutionConfig.skippedSpecsWithReasonList.add(skipReason)
                spec.skip(skipReason)
            }
        }

        spec.features.each { feature ->
            feature.addInterceptor(new CustomMethodInterceptor())
        }
    }
}