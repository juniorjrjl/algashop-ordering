package contracts.order

import org.springframework.cloud.contract.spec.Contract

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

Contract.make {
    request {
        method GET()
        headers {
            accept APPLICATION_JSON_VALUE
        }
        url("/api/v1/orders/01226N0693HDH")
    }
    response{
        status NOT_FOUND()
        body([
                instance: fromRequest().path(),
                type: "/errors/not-found",
                title: "Not found"
        ])
    }
}