package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.DiagnosticQuizRequest;
import com.tqt.englishApp.dto.response.PlacementQuizResponse;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.service.PlacementTestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/placement")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiPlacementController {

    PlacementTestService placementTestService;

    @GetMapping("/generate")
    public ApiResponse<PlacementQuizResponse> generateQuiz(@RequestParam Level level) {
        return ApiResponse.<PlacementQuizResponse>builder()
                .result(placementTestService.generatePlacementQuiz(level))
                .build();
    }

    @PostMapping("/submit")
    public ApiResponse<String> submitResults(@RequestBody DiagnosticQuizRequest request) {
        placementTestService.processDiagnosticResults(request);
        return ApiResponse.<String>builder()
                .result("Placement test processed successfully. Your learning path has been initialized.")
                .build();
    }
}
