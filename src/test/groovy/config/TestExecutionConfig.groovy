package config

import support.myspock.SpecAnalyzer
import java.util.concurrent.atomic.AtomicInteger

class TestExecutionConfig {
    static List<SpecAnalyzer.SpecLevelCountDetails> specLevelCountDetailsList = []
    public static AtomicInteger specCounter = new AtomicInteger(1)
    static List<String> skippedSpecsWithReasonList = []
    static final Boolean LOCAL_RUN = !System.getProperty("skipTests")
    static final Boolean DEBUG_MODE = System.properties['debugLog'] != null && System.properties['debugLog'].toString().toLowerCase().trim() == 'true'
    static final List<String> COUNTRIES = setTestExecutionConfigParams('countries', true)
    static final String ENVIRONMENT = setTestExecutionConfigParams('env', false).first()

    private static List<String> setTestExecutionConfigParams(String systemPropertyName, Boolean commaSeparated) {
        def specifiedValue = System.properties[systemPropertyName]
        if (!specifiedValue) {
            switch (systemPropertyName) {
                case "env":
                    specifiedValue = Environment.preprod.name()
                    //specifiedValue = TestRunConfig.Environment.dev.name()
                    break
                case "countries":
                    specifiedValue = [Country.us.name()]
                    //specifiedValue = [Country.us.name(), Country.uk.name()]
                    break
            }
        } else {
            specifiedValue = specifiedValue.toString().toLowerCase().trim()
            if (commaSeparated) {
                specifiedValue = specifiedValue.toString().toLowerCase().trim().split(',').collect { it.trim() }
            }
        }

        if (specifiedValue instanceof String) {
            specifiedValue = [specifiedValue] // Convert single value to list
        }

        specifiedValue
    }

    static void checkTestExecutionConfigParamsValidity() {
        def enumBasedParamValidityCheck = { keyName, tEnum, val, isList = true ->
            def cond = isList ? tEnum.values().collect { it.name() }.sort().containsAll(val.sort()) : tEnum.values().collect { it.name() }.contains(val)
            if(!cond) {
                throw new IllegalArgumentException("Unsupported ${keyName}: ${val}")
            }
        }

        enumBasedParamValidityCheck.call('environment', Environment, ENVIRONMENT, false)
        enumBasedParamValidityCheck.call('countries', Country, COUNTRIES)
    }

    enum Environment {
        dev,
        preprod,
        prod
    }

    enum Country {
        us('en-us', "United States"),
        uk('en-gb', "United Kingdom"),

        public final String locale
        public final String countryName

        Country(String locale, String countryName) {
            this.locale = locale
            this.countryName = countryName
        }
    }
}