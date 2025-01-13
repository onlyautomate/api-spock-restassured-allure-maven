package apitests.demostorespecs

import apis.DemoStoreApi
import base.BaseSpecification
import groovy.json.JsonBuilder
import io.restassured.path.json.JsonPath
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Tag
import org.spockframework.runtime.model.parallel.ExecutionMode
import spock.lang.Execution
import spock.lang.Shared

@Tag("smoke") //tag from junit-jupiter api, use -Dgroups property to include and -DexcludedGroups to exclude in maven run config
@Execution(ExecutionMode.SAME_THREAD) //prevent concurrent execution due to cross-dependencies between features
class UpdateProductSpec extends BaseSpecification {

    @Shared String authToken

    def "acquire authorization token"() {
        given: 'acquire auth token with given credentials'
            authToken = DemoStoreApi.acquireAuthenticationToken()
        expect: 'token acquired'
            authToken
    }

    def "update existing product id '#productId' for '#propertyToUpdate'"() {
        given: 'get existing product by id'
            def response = new DemoStoreApi(DemoStoreApi.ApiEndpoint.GETGetProduct).tap {
                requestHeaders = [Authorization: "Bearer ${authToken}"]
            }.makeCall([productId])

        expect: 'product acquired'
            response.statusCode() == 200

        when: "update product for given property"
            def jsonPath = response.jsonPath()
            Map updatedMap = [
                    name: jsonPath.get('name'),
                    description: jsonPath.get('description'),
                    image: jsonPath.get('image'),
                    price: jsonPath.get('price'),
                    categoryId: jsonPath.get('categoryId')
            ]
            updatedMap[propertyToUpdate] += RandomStringUtils.randomAlphanumeric(8)

            def updatedReqBody = new JsonBuilder(updatedMap).toString()
            def updatedResponse = new DemoStoreApi(DemoStoreApi.ApiEndpoint.PUTUpdateProduct).tap {
                requestHeaders = [Authorization: "Bearer ${authToken}"]
            }.makeCall([productId], updatedReqBody)

        then: "product updated"
            updatedResponse.statusCode() == 200
            with(updatedResponse.jsonPath()) { JsonPath jp ->
                jp.get(propertyToUpdate) == updatedMap[propertyToUpdate]
            }

        where: "vary by product id and property to update"
            productId | propertyToUpdate
            33        | 'name'
            34        | 'description'
    }
}