package support.myspock

import config.TestExecutionConfig
import org.spockframework.runtime.extension.ExtensionAnnotation
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@ExtensionAnnotation(CustomSpecExtension) //annotation logic
@Target([ElementType.TYPE]) //use on Specification
@interface CustomSpec {
    int[] onlyRunIterationIndices() default []
    TestExecutionConfig.Country[] ignoreIfCountryNotIn() default []
    TestExecutionConfig.Environment[] ignoreIfEnvIn() default []
}