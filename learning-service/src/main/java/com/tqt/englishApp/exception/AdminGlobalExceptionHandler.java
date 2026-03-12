package com.tqt.englishApp.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.tqt.englishApp.controller.admin")
public class AdminGlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getErrorCode().getMessage());
        return getRedirectUrl(request);
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        return getRedirectUrl(request);
    }

    private String getRedirectUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("/vocabularies"))
            return "redirect:/admin/vocabularies";
        if (uri.contains("/topics"))
            return "redirect:/admin/topics";
        if (uri.contains("/subTopics"))
            return "redirect:/admin/subTopics";
        if (uri.contains("/quizs"))
            return "redirect:/admin/quizs";
        if (uri.contains("/answers"))
            return "redirect:/admin/answers";
        if (uri.contains("/videos"))
            return "redirect:/admin/videos";
        return "redirect:/admin";
    }
}
