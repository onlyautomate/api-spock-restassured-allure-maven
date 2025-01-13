package apitests.demostorespecs

import apis.DemoStoreApi
import base.BaseSpecification
import groovy.json.JsonBuilder
import org.spockframework.runtime.model.parallel.ExecutionMode
import spock.lang.Execution
import spock.lang.Shared

@Execution(ExecutionMode.SAME_THREAD) //prevent concurrent execution of features due to cross-dependencies between them
class CreateProductSpec extends BaseSpecification {
    @Shared String authToken

    def "authenticate"() {
        when: 'acquire token with relevant credentials'
            authToken = DemoStoreApi.acquireAuthenticationToken()

        then: 'token acquired'
            authToken
    }

    def "create product: #useCase"() {
        when: 'category with no existing product'
            def reqBody = new JsonBuilder([name: productName, description: 'test desc', image: 'testImg.jpg', price: '19.99', categoryId: 7]).toString()
            def demoStoreApi = new DemoStoreApi(DemoStoreApi.ApiEndpoint.POSTCreateProduct)
            demoStoreApi.requestHeaders = [Authorization: "Bearer ${authToken}"]
            def response = demoStoreApi.makeCall(reqBody)

        then: 'returns an empty list of products'
            response.statusCode == statusCode
            with(response.jsonPath().get()) {
                statusCode == 200 ? productName == productName : true
            }

        where:
            productName      | statusCode | useCase
            'bright goggles' | 200        | 'success'
            'Curved Brown'   | 400        | 'failed - duplicate'
            ''               | 400        | 'failed - missing productName'
    }
}