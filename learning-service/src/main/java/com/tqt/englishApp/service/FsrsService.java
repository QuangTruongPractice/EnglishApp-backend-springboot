package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.enums.Level;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Thuật toán FSRS v5 (Free Spaced Repetition Scheduler)
 */
@Service
public class FsrsService {

    // 19 Trọng số tối ưu hóa cho FSRS v5
    private static final double[] W = {
           0.40255, 1.18385, 3.173, 15.69105, 
           7.1949, 0.5345, 1.4604,            
           0.0046, 1.54575, 0.1192,           
           1.01925, 1.9395, 0.11,             
           0.29605, 2.2698, 0.2315,           
           2.9898, 0.51655, 0.6621            
    };

    /**
     * Maps response time and correctness to an FSRS rating (1-4).
     * 1: Again, 2: Hard, 3: Good, 4: Easy
     */
    public int calculateRating(boolean isCorrect, Long responseTimeMs) {
        if (!isCorrect) return 1; // Again
        if (responseTimeMs <= 5000) return 4; // Easy
        if (responseTimeMs <= 10000) return 3; // Good
        if (responseTimeMs > 10000) return 2; // Hard
        return 1;
    }

    /**
     * Khởi tạo bản ghi SRS lần đầu tiên
     * Formula: D0(G) = w4 - exp(w5 * (G - 1)) + 1
     */
    public void initProgress(UserVocabularyProgress progress, int rating) {
        // S0 = w[G-1]
        progress.setStability(W[rating - 1]);
        
        // D0 = w4 - exp(w5 * (G - 1)) + 1
        double d0 = W[4] - Math.exp(W[5] * (rating - 1)) + 1;
        progress.setDifficulty(clamp(d0, 1, 10));
    }

    /**
     * Cập nhật tiến độ theo FSRS v5
     */
    public void updateProgress(UserVocabularyProgress progress, int rating, LocalDateTime now) {
        double elapsedDays = 0;
        if (progress.getLastReviewedAt() != null) {
            elapsedDays = ChronoUnit.SECONDS.between(progress.getLastReviewedAt(), now) / 86400.0;
        }
        elapsedDays = Math.max(0, elapsedDays);

        // --- 1. Xử lý Same-day Review (Heuristic) ---
        // Formula: S' = S * exp(w17 * (G - 3 + w18))
        if (elapsedDays < 0.01) { 
            double s = progress.getStability();
            double sNext = s * Math.exp(W[17] * (rating - 3 + W[18]));
            progress.setStability(clamp(sNext, 0.1, 36500));
            return;
        }

        // --- 2. Tính Retrievability (R) ---
        // Formula: R = (1 + (FACTOR) * t / S) ^ -DECAY
        // FACTOR = 19/81, DECAY = 0.5
        double FACTOR = 19.0 / 81.0;
        double DECAY = 0.5;
        double r = Math.pow(1 + FACTOR * elapsedDays / progress.getStability(), -DECAY);

        // --- 3. Cập nhật Difficulty (D) ---
        // Formula: deltaD = -w6 * (G - 3)
        // D' = D + deltaD * (10 - D) / 9
        double d = progress.getDifficulty();
        double deltaD = -W[6] * (rating - 3);
        double dPrime = d + deltaD * (10 - d) / 9.0;
        
        // Mean Reversion Target: D0(4) = w4 - exp(w5 * (4 - 1)) + 1
        double d0_4 = W[4] - Math.exp(W[5] * 3) + 1; 
        //D'' = w7 * D0(4) + (1 - w7) * D'
        double dFinal = W[7] * d0_4 + (1 - W[7]) * dPrime;
        
        progress.setDifficulty(clamp(dFinal, 1, 10));
        double dUpdated = progress.getDifficulty();

        // --- 4. Cập nhật Stability (S) ---
        double s = progress.getStability();
        double sNext;
        if (rating > 1) { // RECALL (Nhớ)
            // S_new = S * (exp(w8) * (11 - D) * S^-w9 * (exp((1 - R) * w10) - 1) * multiplier + 1)
            double sInc = Math.exp(W[8]) * (11 - dUpdated) * Math.pow(s, -W[9]) * (Math.exp((1 - r) * W[10]) - 1);
            if (rating == 2) sInc *= W[15]; // Hard multiplier
            else if (rating == 4) sInc *= W[16]; // Easy multiplier
            
            sNext = s * (sInc + 1);
        } else { // FORGET (Quên)
            // S_new = w11 * D^-w12 * ((S + 1)^w13 - 1) * exp((1 - R) * w14)
            sNext = W[11] * Math.pow(dUpdated, -W[12]) * (Math.pow(s + 1, W[13]) - 1) * Math.exp((1 - r) * W[14]);
        }

        progress.setStability(clamp(sNext, 0.1, 36500));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

}
