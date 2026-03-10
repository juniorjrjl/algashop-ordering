package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/{version}/customers")
public class CustomerController {

    private final CustomerManagementApplicationService applicationService;
    private final CustomerQueryService queryService;

    @PostMapping(version = "1")
    @ResponseStatus(CREATED)
    public CustomerOutput create(@RequestBody @Valid final CustomerInput input,
                                 final HttpServletResponse response) {
        final var customerId = applicationService.create(input);
        final var builder = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{customerId}")
                        .buildAndExpand(customerId);
        response.setHeader("Location", builder.toUriString());
        return queryService.findById(customerId);
    }

    @GetMapping(version = "1")
    public PageModel<CustomerSummaryOutput> findAll(final CustomerFilter filter) {
        return PageModel.of(queryService.filter(filter));
    }

    @GetMapping(version = "1", path = "/{id}")
    public CustomerOutput findById(@PathVariable final UUID id) {
        return queryService.findById(id);
    }

}
