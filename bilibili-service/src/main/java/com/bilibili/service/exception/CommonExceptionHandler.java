package com.bilibili.service.exception;

import com.bilibili.response.JsonResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(Exception.class)
    public JsonResponse handle(Exception e, HttpServletRequest request) {
        String errorMessage = e.getMessage();
        if(e instanceof ConditionalException) {
            return JsonResponse.error(((ConditionalException) e).getCode(), errorMessage);
        } else {
            return JsonResponse.error("500", errorMessage);
        }
    }
}
