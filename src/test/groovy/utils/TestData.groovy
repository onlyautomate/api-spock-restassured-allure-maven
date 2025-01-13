package utils

import config.TestExecutionConfig

class TestData {
    int number1 = 1
    int number2 = 2

    TestData() {
        envBasedOverride()
    }

    private void envBasedOverride() {
        switch (TestExecutionConfig.ENVIRONMENT) {
            case TestExecutionConfig.Environment.dev:
                number1 = 3
                number2 = 4
                break
        }
    }
}