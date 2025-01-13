package support.logs

import config.TestExecutionConfig
import io.qameta.allure.Allure

class CustomLog {

    private static final ThreadLocal<List<String>> threadLogBuffer = ThreadLocal.withInitial { [] }

    static void info(Object message) {
        threadLogBuffer.get().add(message.toString())
    }

    static void attachLogsToAllure(String attachmentName) {
        List<String> threadLogs = threadLogBuffer.get()
        if (!threadLogs.isEmpty()) {
            def content = threadLogs.join(System.lineSeparator())
            //for allure report
            Allure.addAttachment(attachmentName, 'text/plain', content, '.txt')
            def localPrint = TestExecutionConfig.LOCAL_RUN || attachmentName.contains('global')
            if(localPrint) {
                println content
            }
            threadLogBuffer.get().clear() //clear after attaching
        }
    }
}