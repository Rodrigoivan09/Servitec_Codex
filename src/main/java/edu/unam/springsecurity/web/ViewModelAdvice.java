package edu.unam.springsecurity.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class ViewModelAdvice {

    @ModelAttribute("uri")
    public String currentRequestUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}

