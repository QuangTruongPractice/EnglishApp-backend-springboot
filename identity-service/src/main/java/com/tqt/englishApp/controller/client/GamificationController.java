package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.AchievementResponse;
import com.tqt.englishApp.dto.response.ApiResponse;
import com.tqt.englishApp.dto.response.AvatarFrameResponse;
import com.tqt.englishApp.service.GamificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GamificationController {
    GamificationService gamificationService;

    @GetMapping("/frames")
    public ApiResponse<List<AvatarFrameResponse>> getAvatarFrames(Principal principal) {
        String username = principal.getName();
        return ApiResponse.<List<AvatarFrameResponse>>builder()
                .result(gamificationService.getAllAvatarFrames(username))
                .message("Lấy danh sách khung Avatar thành công")
                .build();
    }

    @PostMapping("/frames/{frameKey}/buy")
    public ApiResponse<AvatarFrameResponse> buyAvatarFrame(Principal principal, @PathVariable String frameKey) {
        String username = principal.getName();
        return ApiResponse.<AvatarFrameResponse>builder()
                .result(gamificationService.buyAvatarFrame(username, frameKey))
                .message("Mở khóa khung Avatar thành công")
                .build();
    }

    @PostMapping("/frames/{frameKey}/equip")
    public ApiResponse<String> equipAvatarFrame(Principal principal, @PathVariable String frameKey) {
        String username = principal.getName();
        gamificationService.equipAvatarFrame(username, frameKey);
        return ApiResponse.<String>builder()
                .result(frameKey)
                .message("Trang bị khung Avatar thành công")
                .build();
    }

    @GetMapping("/achievements")
    public ApiResponse<List<AchievementResponse>> getAchievements(Principal principal) {
        String username = principal.getName();
        return ApiResponse.<List<AchievementResponse>>builder()
                .result(gamificationService.getUserAchievements(username))
                .message("Lấy danh sách thành tựu thành công")
                .build();
    }

    @PostMapping("/achievements/{code}/claim")
    public ApiResponse<AchievementResponse> claimAchievementReward(Principal principal, @PathVariable String code) {
        String username = principal.getName();
        return ApiResponse.<AchievementResponse>builder()
                .result(gamificationService.claimAchievementReward(username, code))
                .message("Nhận phần thưởng thành tựu thành công")
                .build();
    }
}
