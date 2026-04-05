package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.VideoRequest;
import com.tqt.englishApp.service.VideoProcessingService;
import com.tqt.englishApp.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoProcessingService videoProcessingService;

    @GetMapping
    public String listVideos(Model model, @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (title != null && !title.isEmpty()) {
            params.put("title", title);
        }
        params.put("page", String.valueOf(page));

        Page<?> videos = videoService.getVideos(params, null);
        model.addAttribute("videos", videos.getContent());
        model.addAttribute("totalPages", videos.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/videos";
    }

    @GetMapping("/add")
    public String videosForm(Model model) {
        model.addAttribute("videos", new VideoRequest());
        return "admin/videos_form";
    }

    @PostMapping("/add")
    public String addVideo(@ModelAttribute(value = "videos") @Valid VideoRequest request, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/videos_form";
        }
        try {
            System.out.println("addVideo: " + request.getYoutubeUrl());
            videoProcessingService.processAndSaveVideo(request.getYoutubeUrl());
            redirectAttributes.addFlashAttribute("successMessage", "Đang xử lý và thêm video mới...");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thao tác thất bại: " + e.getMessage());
        }
        return "redirect:/admin/videos";
    }

    @GetMapping("/edit/{videoId}")
    public String updateVideo(Model model, @PathVariable(value = "videoId") int id) {
        model.addAttribute("videos", videoService.getVideoById(id, null));
        return "admin/videos_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteVideo(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            videoService.deleteVideo(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa video thành công!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa video này vì có dữ liệu liên quan.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/admin/videos";
    }
}
