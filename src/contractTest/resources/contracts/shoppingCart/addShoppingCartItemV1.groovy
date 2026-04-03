package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

Contract.make {
    request {
        method POST()
        urlPath("/api/v1/shopping-carts/ad265aa3-c77d-46e9-9782-b70c487c1e17/items")
        headers {
            contentType(APPLICATION_JSON_VALUE)
        }
        body([
                productId: value(
                        test("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
                        stub(anyUuid())
                ),
                quantity: value(
                        test(2),
                        stub(anyPositiveInt())
                )
        ])
    }
    response {
        status NO_CONTENT()
    }
}