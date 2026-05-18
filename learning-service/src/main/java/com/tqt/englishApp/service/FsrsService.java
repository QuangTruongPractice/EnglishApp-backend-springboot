package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.UserVocabularyProgress;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Thuật toán FSRS (Free Spaced Repetition Scheduler)
 */
@Service
public class FsrsService {

    // 21 trọng số tối ưu hóa cho FSRS
    private static final double[] W = {
            0.212, 1.2931, 2.3065, 8.2956, 6.4133, 0.8334, 3.0194,
            0.001, 1.8722, 0.1666, 0.796, 1.4835, 0.0614, 0.2629,
            1.6483, 0.6014, 1.8729, 0.5425, 0.0912, 0.0658, 0.1542
    };

    // factor = 0.9^(−1/w₂₀) − 1
    private static final double DECAY  = W[20];
    private static final double FACTOR = Math.pow(0.9, -1.0 / DECAY) - 1.0;

    /**
     * Maps response time và kết quả trả lời sang FSRS rating (1–4).
     * 1=Again, 2=Hard, 3=Good, 4=Easy
     */
    public int calculateRating(boolean isCorrect, Long responseTimeMs) {
        if (!isCorrect)              return 1; // Again
        if (responseTimeMs <= 5000)  return 4; // Easy
        if (responseTimeMs <= 10000) return 3; // Good
        return 2;                              // Hard
    }

    /**
     * Khởi tạo bản ghi SRS lần đầu tiên.
     *
     * S₀(G) = w[G−1]
     * D₀(G) = w₄ − exp(w₅ × (G−1)) + 1
     */
    public void initProgress(UserVocabularyProgress progress, int rating) {
        // Initial stability
        progress.setStability(W[rating - 1]);

        // Initial difficulty
        double d0 = W[4] - Math.exp(W[5] * (rating - 1)) + 1;
        progress.setDifficulty(clamp(d0, 1, 10));
    }

    /**
     * Cập nhật tiến độ FSRS
     */
    public void updateProgress(UserVocabularyProgress progress, int rating, LocalDateTime now) {
        double elapsedDays = calculateElapsedDays(progress.getLastReviewedAt(), now);

        // 1. Same-day review: S' = S * exp(w₁₇ * (G−3+w₁₈)) * S^(−w₁₉)
        if (elapsedDays < 0.01) {
            double sNext = calculateShortTermStability(progress.getStability(), rating);
            progress.setStability(clamp(sNext, 0.1, 36500));
            return;
        }

        // 2. Retrievability: R(t,S) = (1 + factor * t/S)^(−w₂₀)
        double r = calculateRetrievability(elapsedDays, progress.getStability());

        // 3. Difficulty (D)
        double dFinal = calculateNextDifficulty(progress.getDifficulty(), rating);
        progress.setDifficulty(clamp(dFinal, 1, 10));

        // 4. Stability (S)
        double sNext = calculateNextStability(progress.getStability(), progress.getDifficulty(), r, rating);
        progress.setStability(clamp(sNext, 0.1, 36500));
    }

    private double calculateShortTermStability(double s, int rating) {
        return s * Math.exp(W[17] * (rating - 3 + W[18])) * Math.pow(s, -W[19]);
    }

    private double calculateNextDifficulty(double currentDifficulty, int rating) {
        // D' = D + (−w₆ * (G−3)) * (10−D) / 9
        // D'' = w₇ * D₀(3) + (1−w₇) * D'
        double deltaD = -W[6] * (rating - 3);
        double dPrime = currentDifficulty + deltaD * (10 - currentDifficulty) / 9.0;

        double d0_3 = W[4] - Math.exp(W[5] * 2) + 1; // target mean reversion = D₀(3)
        return W[7] * d0_3 + (1 - W[7]) * dPrime;
    }

    private double calculateNextStability(double s, double dUpdated, double r, int rating) {
        if (rating > 1) {
            // Recall: S'ᵣ = S * (SInc + 1)
            // SInc = exp(w₈) * (11−D) * S^(−w₉) * (exp((1−R)*w₁₀) − 1) * w₁₅(Hard) * w₁₆(Easy)
            double sInc = Math.exp(W[8])
                    * (11 - dUpdated)
                    * Math.pow(s, -W[9])
                    * (Math.exp((1 - r) * W[10]) - 1);

            if (rating == 2) sInc *= W[15]; // Hard multiplier
            if (rating == 4) sInc *= W[16]; // Easy multiplier

            return s * (sInc + 1);
        } else {
            // Forget: S'f = w₁₁ * D^(−w₁₂) * ((S+1)^w₁₃ − 1) * exp((1−R)*w₁₄)
            return W[11]
                    * Math.pow(dUpdated, -W[12])
                    * (Math.pow(s + 1, W[13]) - 1)
                    * Math.exp((1 - r) * W[14]);
        }
    }

    /**
     * Tính khoảng lặp tiếp theo (ngày) theo retention mong muốn.
     *
     * I(r, S) = S / FACTOR * (r^(1/−DECAY) − 1)
     *
     * @param requestRetention xác suất nhớ mong muốn, ví dụ 0.9
     * @param stability        giá trị S hiện tại
     * @return số ngày cho lần ôn tiếp theo
     */
    public int nextInterval(double requestRetention, double stability) {
        double interval = (stability / FACTOR) * (Math.pow(requestRetention, 1.0 / -DECAY) - 1);
        return (int) Math.max(1, Math.round(interval));
    }

    /**
     * Tính toán Retrievability (Khả năng nhớ lại / Memory Strength) hiện tại.
     * Rất hữu ích để hiển thị thanh sức mạnh trí nhớ trên UI.
     *
     * @param progress Bản ghi tiến độ
     * @param now Thời điểm kiểm tra
     * @return Retrievability (0.0 đến 1.0)
     */

    private double calculateElapsedDays(LocalDateTime lastReviewedAt, LocalDateTime now) {
        if (lastReviewedAt == null) return 0.0;
        double elapsedDays = ChronoUnit.SECONDS.between(lastReviewedAt, now) / 86400.0;
        return Math.max(0.0, elapsedDays);
    }

    private double calculateRetrievability(double elapsedDays, double stability) {
        return Math.pow(1 + FACTOR * elapsedDays / stability, -DECAY);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}