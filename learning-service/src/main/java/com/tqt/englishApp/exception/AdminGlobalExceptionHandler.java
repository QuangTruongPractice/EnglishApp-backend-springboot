package com.tqt.englishApp.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.tqt.englishApp.controller.admin")
@Slf4j
public class AdminGlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        log.error("AppException occurred in Admin area: {} at URI: {}", e.getErrorCode().getMessage(), request.getRequestURI());
        redirectAttributes.addFlashAttribute("errorMessage", e.getErrorCode().getMessage());
        return getRedirectUrl(request);
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        log.error("General Exception occurred in Admin area: {} at URI: {}", e.getMessage(), request.getRequestURI(), e);
        redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        return getRedirectUrl(request);
    }

    private String getRedirectUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        // If the error occurred on a specific admin page, redirect to the general admin dashboard
        // to break the self-redirection loop.
        if (uri.contains("/vocabularies") && !uri.endsWith("/admin/vocabularies") && !uri.endsWith("/admin/vocabularies/"))
            return "redirect:/admin/vocabularies";
        if (uri.contains("/topics") && !uri.endsWith("/admin/topics") && !uri.endsWith("/admin/topics/"))
            return "redirect:/admin/topics";
        if (uri.contains("/subTopics") && !uri.endsWith("/admin/subTopics") && !uri.endsWith("/admin/subTopics/"))
            return "redirect:/admin/subTopics";
        if (uri.contains("/quizs") && !uri.endsWith("/admin/quizs") && !uri.endsWith("/admin/quizs/"))
            return "redirect:/admin/quizs";
        if (uri.contains("/answers") && !uri.endsWith("/admin/answers") && !uri.endsWith("/admin/answers/"))
            return "redirect:/admin/answers";
        if (uri.contains("/videos") && !uri.endsWith("/admin/videos") && !uri.endsWith("/admin/videos/"))
            return "redirect:/admin/videos";

        // If the error happened on the main dashboard itself, redirect to login to avoid a secondary loop.
        // Or if it's any other case that doesn't match above.
        if (uri.endsWith("/admin") || uri.endsWith("/admin/")) {
            return "redirect:/login?error=true";
        }

        return "redirect:/admin";
    }
}
