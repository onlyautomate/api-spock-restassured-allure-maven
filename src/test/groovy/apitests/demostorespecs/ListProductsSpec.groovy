package apitests.demostorespecs

import apis.DemoStoreApi
import base.BaseSpecification
import org.spockframework.runtime.model.parallel.ExecutionMode
import spock.lang.Execution

@Execution(ExecutionMode.CONCURRENT) //allow concurrent execution of features since no cross-dependencies between them
class ListProductsSpec extends BaseSpecification {

    def "test feature"() {
        given:
            true
        expect:
            true
    }

    def "get current products: #useCase"() {
        given:
            def demoStoreApi = new DemoStoreApi(DemoStoreApi.ApiEndpoint.GETGetAllProducts).tap {
                if(authenticated) requestHeaders = [Authorization: "Bearer ${acquireAuthenticationToken()}"]
                this
            }
            def products = demoStoreApi.makeCall([category: categoryId]).then().statusCode(200).extract().jsonPath().get()

        expect:
            products instanceof List
            ((products as List).size() > 0) == nonZeroProducts

        where:
            categoryId | authenticated | nonZeroProducts | useCase
            7          | false         | true            | 'unauthenticated, success'
            7897       | false         | false           | 'unauthenticated, non-existing category'
            7          | true          | true            | 'authenticated, success'
            9999       | true          | false           | 'authenticated, non-existing category'
    }
}