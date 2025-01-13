package support.myrestassured

import config.TestExecutionConfig
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import io.restassured.filter.Filter
import io.restassured.filter.FilterContext
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import support.logs.CustomLog

class CustomFilter implements Filter {

    @Override
    Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec)
        logRequestDetails(requestSpec, response, TestExecutionConfig.DEBUG_MODE || !isSuccessfulResponse(response))
        response
    }

    private static Boolean isSuccessfulResponse(Response response) {
        def statusCode = response.statusCode.toString()
        statusCode.startsWith('2') || statusCode.startsWith('3')
    }

    private static Object printRequestBody(FilterableRequestSpecification requestSpec) {
        try {
            return JsonOutput.prettyPrint(requestSpec.body.toString())
        } catch (Exception ignored) {
            return requestSpec.body
        }
    }

    private static void logRequestDetails(FilterableRequestSpecification requestSpec, Response response, Boolean detailed) {
        CustomLog.info("---------------------------------------------------")
        CustomLog.info("Details of the request made:")
        CustomLog.info("Method: ${requestSpec.method}")
        CustomLog.info("URI: ${requestSpec.URI}")
        CustomLog.info("Status code: ${response.statusCode}")
        if(detailed) {
            CustomLog.info("Status line: ${response.statusLine}")
            CustomLog.info("Request body: ${printRequestBody(requestSpec)}")
            CustomLog.info("Request headers: ${new JsonBuilder(requestSpec.headers).toPrettyString()}")
            CustomLog.info("Request cookies: ${new JsonBuilder(requestSpec.cookies).toPrettyString()}")
            CustomLog.info("Response body: ${response.body.asPrettyString()}")
            CustomLog.info("Response headers: ${new JsonBuilder(response.headers).toPrettyString()}")
            CustomLog.info("Response cookies: ${new JsonBuilder(response.cookies).toPrettyString()}")
        }
        CustomLog.info("---------------------------------------------------")
    }
}