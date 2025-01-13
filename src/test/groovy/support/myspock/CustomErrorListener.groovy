package support.myspock

import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.model.ErrorInfo

//usage at specification level, to capture and show the status of the specification after it was run
class CustomErrorListener extends AbstractRunListener {
    ErrorInfo errorInfo

    @Override
    void error(ErrorInfo errorInfo) {
        super.error(errorInfo)
        if(!this.errorInfo) {
            this.errorInfo = errorInfo
        }
    }
}