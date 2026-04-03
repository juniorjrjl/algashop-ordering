package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE

Contract.make {
    request {
        method GET()
        urlPath("/api/v1/shopping-carts/e2103964-5353-4910-81ee-212a40a2ca70")
    }
    response {
        status 404
        headers {
            contentType APPLICATION_PROBLEM_JSON_VALUE
        }
        body([
                instance: fromRequest().path(),
                type    : "/errors/not-found",
                title   : "Not found"
        ])
    }
}
