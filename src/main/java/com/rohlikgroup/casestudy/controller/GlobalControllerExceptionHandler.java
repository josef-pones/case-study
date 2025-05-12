package com.rohlikgroup.casestudy.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.rohlikgroup.casestudy.dto.ErrorResponseDto;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            HttpServletResponse response = servletWebRequest.getResponse();
            if (response != null && response.isCommitted()) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Response already committed. Ignoring: " + ex);
                }
                return null;
            }
        }

        if (body == null && ex instanceof ErrorResponse errorResponse) {
            body = errorResponse.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale());
        }

        if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR) && body == null) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        if (body != null && !(body instanceof ErrorResponseDto)) {
            if (body instanceof ProblemDetail problemDetail) {
                body = new ErrorResponseDto(statusCode, problemDetail.getTitle(), problemDetail.getDetail());
            } else {
                body = new ErrorResponseDto(statusCode, "UnknownError", body.toString());
            }
        }
        return createResponseEntity(body, headers, statusCode, request);
    }
}
