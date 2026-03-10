package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerEmailInUseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                            final HttpHeaders headers,
                                                                            final HttpStatusCode status,
                                                                            final WebRequest request) {
        final var problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Invalid fields");
        problemDetail.setDetail("One or more fields are invalid");
        problemDetail.setType(URI.create("/errors/invalid-fields"));
        final var fieldErrors = ex.getBindingResult().getAllErrors().stream().collect(Collectors.toMap(
                e -> ((FieldError) e).getField(),
                e -> messageSource.getMessage(e, LocaleContextHolder.getLocale())
        ));
        problemDetail.setProperty("fields", fieldErrors);
        log.error(ex.getMessage(), ex);
        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(DomainEntityNotFoundException.class)
    public ProblemDetail handleDomainEntityNotFoundException(final DomainEntityNotFoundException ex,
                                                             final WebRequest request) {
        final var problemDetail = ProblemDetail.forStatus(NOT_FOUND);
        problemDetail.setTitle("Not found");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/not-found"));
        log.error(ex.getMessage(), ex);
        return problemDetail;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(final DomainException ex,
                                               final WebRequest request) {
        final var problemDetail = ProblemDetail.forStatus(UNPROCESSABLE_CONTENT);
        problemDetail.setTitle("Unprocessable Entity");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/unprocessable-entity"));
        log.error(ex.getMessage(), ex);
        return problemDetail;
    }

    @ExceptionHandler(CustomerEmailInUseException.class)
    public ProblemDetail handleDomainException(final CustomerEmailInUseException ex,
                                               final WebRequest request) {
        final var problemDetail = ProblemDetail.forStatus(CONFLICT);
        problemDetail.setTitle("Conflict");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/conflict"));
        log.error(ex.getMessage(), ex);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleDomainException(final Exception ex,
                                               final WebRequest request) {
        final var problemDetail = ProblemDetail.forStatus(INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected internal error occurred");
        problemDetail.setType(URI.create("/errors/internal-server-error"));
        log.error(ex.getMessage(), ex);
        return problemDetail;
    }

}
