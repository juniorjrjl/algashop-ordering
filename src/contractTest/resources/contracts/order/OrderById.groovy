package contracts.order

import org.springframework.cloud.contract.spec.Contract
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

Contract.make {
    request {
        method GET()
        headers {
            accept APPLICATION_JSON_VALUE
        }
        url("/api/v1/orders/01226N0640J7Q")
    }
    response {
        status OK()
        body([
                id: "01226N0640J7Q",
                customer: [
                        id: anyUuid(),
                        firstName: anyNonBlankString(),
                        lastName: anyNonBlankString(),
                        document: anyNonBlankString(),
                        email: anyNonBlankString(),
                        phone: anyNonBlankString()
                ],
                totalItems: anyInteger(),
                totalAmount: anyDouble(),
                placedAt: anyIso8601WithOffset(),
                canceledAt: anyIso8601WithOffset(),
                paidAt: anyIso8601WithOffset(),
                readyAt: anyIso8601WithOffset(),
                orderStatus: anyNonBlankString(),
                paymentMethod: anyNonBlankString(),
                shipping: [
                        cost: anyDouble(),
                        expectedDate: anyDate(),
                        recipient: [
                                firstName: anyNonBlankString(),
                                lastName: anyNonBlankString(),
                                document: anyNonBlankString(),
                                phone: anyNonBlankString()
                        ],
                        address: [
                                street: anyNonBlankString(),
                                number: anyNonBlankString(),
                                complement: anyNonBlankString(),
                                neighborhood: anyNonBlankString(),
                                city: anyNonBlankString(),
                                state: anyNonBlankString(),
                                zipCode: anyNonBlankString()
                        ]
                ],
                billing: [
                        firstName: anyNonBlankString(),
                        lastName: anyNonBlankString(),
                        document: anyNonBlankString(),
                        phone: anyNonBlankString(),
                        address: [
                                street: anyNonBlankString(),
                                number: anyNonBlankString(),
                                complement: anyNonBlankString(),
                                neighborhood: anyNonBlankString(),
                                city: anyNonBlankString(),
                                state: anyNonBlankString(),
                                zipCode: anyNonBlankString()
                        ]
                ],
                items: [
                        [
                                id: anyNonBlankString(),
                                productId: anyUuid(),
                                orderId: anyNonBlankString(),
                                price: anyDouble(),
                                productName: anyNonBlankString(),
                                quantity: anyInteger(),
                                totalAmount: anyDouble()
                        ]
                ]
        ])
    }
}
