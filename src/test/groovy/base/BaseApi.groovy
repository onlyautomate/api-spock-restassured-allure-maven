package base

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.http.Method
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import support.myrestassured.CustomFilter

abstract class BaseApi {

    String baseUrl
    String basePath = '/'
    ContentType requestContentType = ContentType.JSON
    ContentType responseContentType
    Method method
    Map requestHeaders
    String basicAuthUserName
    String basicAuthPassword
    private Map defaultRequestHeaders = [:]

    @SuppressWarnings('unused')
    Response makeCall() {
        makeCall(null, null, null)
    }

    Response makeCall(List pathParams) {
        makeCall(pathParams, null, null)
    }

    Response makeCall(Map<String, Object> queryParams) {
        makeCall(null, queryParams, null)
    }

    @SuppressWarnings('unused')
    Response makeCall(List pathParams, Map<String, Object> queryParams) {
        makeCall(pathParams, queryParams, null)
    }

    Response makeCall(List pathParams, String requestBody) {
        makeCall(pathParams, null, requestBody)
    }

    Response makeCall(String requestBody) {
        makeCall(null, null, requestBody)
    }

    Response makeCall(List pathParams, Map<String, Object> queryParams, String requestBody) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
            .setBaseUri(this.baseUrl)
            .addHeaders(this.defaultRequestHeaders)
            .setContentType(this.requestContentType)

        if(this.responseContentType) {
            requestSpecBuilder.setAccept(responseContentType)
        }

        if(this.requestHeaders) {
            requestSpecBuilder.addHeaders(requestHeaders)
        }

        if(queryParams) {
            requestSpecBuilder.addQueryParams(queryParams)
        }

        if(requestBody) {
            requestSpecBuilder.setBody(requestBody)
        }

        RequestSpecification requestSpecification = requestSpecBuilder.build()
        if(this.basicAuthUserName && this.basicAuthPassword) {
            requestSpecification.auth().basic(this.basicAuthUserName, this.basicAuthPassword)
        }

        if(pathParams) {
            RestAssured.given(requestSpecification)
                .filter(new CustomFilter()) //customize execution of api call
                .request(this.method, this.basePath, pathParams.toArray()) //unnamed api path parameters
        } else {
            requestSpecification.basePath(this.basePath)
            RestAssured.given(requestSpecification)
                .filter(new CustomFilter()) //customize execution of api call
                .request(this.method)
        }
    }
}