package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.DiagnosticQuizRequest;
import com.tqt.englishApp.dto.response.PlacementQuizResponse;
import com.tqt.englishApp.service.PlacementTestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiPlacementController {

    PlacementTestService placementTestService;

    @GetMapping("/secure/placement/generate")
    public ApiResponse<PlacementQuizResponse> generateQuiz(Principal principal) {
        return ApiResponse.<PlacementQuizResponse>builder()
                .result(placementTestService.generatePlacementQuiz(principal.getName()))
                .build();
    }

    @PostMapping("/secure/placement/submit")
    public ApiResponse<String> submitResults(
            @RequestBody DiagnosticQuizRequest request,
            Principal principal) {
        
        request.setUserId(principal.getName());
        placementTestService.processDiagnosticResults(request);
        
        return ApiResponse.<String>builder()
                .result("Placement test processed successfully. Your learning path has been initialized.")
                .build();
    }
}
