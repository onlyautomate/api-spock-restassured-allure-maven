package apis

import base.BaseApi
import groovy.json.JsonBuilder
import io.restassured.http.Method

class DemoStoreApi extends BaseApi {

    enum ApiEndpoint {
        GETGetProduct,
        PUTUpdateProduct,
        GETGetAllProducts,
        POSTCreateProduct,
        POSTGetAuthToken
    }

    DemoStoreApi(ApiEndpoint apiEndpoint) {
        this.baseUrl = "http://demostore.gatling.io"
        switch (apiEndpoint) {
            case ApiEndpoint.GETGetProduct:
                this.method = Method.GET
                this.basePath = '/api/product/{id}'
                break
            case ApiEndpoint.PUTUpdateProduct:
                this.method = Method.PUT
                this.basePath = '/api/product/{id}'
                break
            case ApiEndpoint.GETGetAllProducts:
                this.method = Method.GET
                this.basePath = '/api/product'
                break
            case ApiEndpoint.POSTCreateProduct:
                this.method = Method.POST
                this.basePath = '/api/product'
                break
            case ApiEndpoint.POSTGetAuthToken:
                this.method = Method.POST
                this.basePath = '/api/authenticate'
                this.basicAuthUserName = 'admin'
                this.basicAuthPassword = 'admin'
                break
        }
    }

    static String acquireAuthenticationToken() {
        def demoStoreApi = new DemoStoreApi(ApiEndpoint.POSTGetAuthToken)
        def reqBody = new JsonBuilder([username: demoStoreApi.basicAuthUserName, password: demoStoreApi.basicAuthPassword]).toString()
        def response = demoStoreApi.makeCall(reqBody)
        def jsonPath = response.then().statusCode(200).extract().response().jsonPath()
        jsonPath.get('token')
    }
}