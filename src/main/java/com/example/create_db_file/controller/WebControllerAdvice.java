package com.example.create_db_file.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class WebControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionHandler(Exception e, Model model){
        log.warn("error handling. catch exceptionHandler: {}", e.getClass());
        model.addAttribute("status",HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", "予期しないエラーが発生しました");
        return "error";
    }
}
