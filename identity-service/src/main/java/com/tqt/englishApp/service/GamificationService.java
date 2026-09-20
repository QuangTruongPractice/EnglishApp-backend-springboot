package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.AchievementResponse;
import com.tqt.englishApp.dto.response.AvatarFrameResponse;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GamificationService {
    UserRepository userRepository;
    AvatarFrameRepository avatarFrameRepository;
    UserAvatarFrameRepository userAvatarFrameRepository;
    AchievementRepository achievementRepository;
    UserAchievementRepository userAchievementRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        log.info("Initializing Gamification Avatar Frames & Achievements seed data...");
        initAvatarFrames();
        initAchievements();
    }

    private void initAvatarFrames() {
        List<AvatarFrame> defaultFrames = List.of(
                AvatarFrame.builder().frameKey("common-bronze-octagon").name("Khiên Bát Giác Đồng").rarity("Common").gemCost(50).description("Cấu trúc viền bát giác đúc bằng đồng thau với 8 chốt nịt đính ở góc.").iconSymbol("🛡️").avatarBg("#FDF6E2").build(),
                AvatarFrame.builder().frameKey("common-iron-cog").name("Bánh Răng Sắt Cổ").rarity("Common").gemCost(75).description("Cấu trúc bánh răng cơ khí sắt rèn có các khớp răng cưa chạy quanh viền.").iconSymbol("⚙️").avatarBg("#F1F5F9").build(),
                AvatarFrame.builder().frameKey("common-stone-slab").name("Bảng Đá Cổ Núi").rarity("Common").gemCost(90).description("Khung đá thô vuông bo tròn góc chạm vết nứt núi đá huyền bí.").iconSymbol("🪨").avatarBg("#E2E8F0").build(),
                AvatarFrame.builder().frameKey("common-wood-oak").name("Vòng Gỗ Sồi Rừng").rarity("Common").gemCost(120).description("Khung gỗ sồi nguyên khối khắc vân uốn và gờ lá rừng ở 2 bên.").iconSymbol("🪵").avatarBg("#FEF3C7").build(),
                AvatarFrame.builder().frameKey("rare-silver-swords").name("Hiệp Sĩ Kiếm Bạc").rarity("Rare").gemCost(250).description("Cấu trúc viền bạc chạm trổ khiên Hiệp sĩ với 2 thanh kiếm chéo đính ở chân khung.").iconSymbol("⚔️").avatarBg("#F0F9FF").build(),
                AvatarFrame.builder().frameKey("rare-frost-spikes").name("Chông Băng Bão Tuyết").rarity("Rare").gemCost(350).description("Các chông băng sắc nhọn đâm ra từ 4 phía kèm bông hoa tuyết lục giác trên đỉnh.").iconSymbol("❄️").avatarBg("#F0F9FF").build(),
                AvatarFrame.builder().frameKey("rare-sakura-heart").name("Trái Tim Anh Đào").rarity("Rare").gemCost(480).description("Vòng dây hoa anh đào kết hình trái tim uốn lượn kèm 5 cánh hoa rơi 3D ở góc dưới.").iconSymbol("🌸").avatarBg("#FDF2F8").build(),
                AvatarFrame.builder().frameKey("legendary-ruby-warlock").name("Pháp Sư Hồng Ngọc").rarity("Legendary").gemCost(2400).description("Vương miện 3 đỉnh ngọc đỏ rực kết hợp ma pháp tam giác ngược và 3 quả cầu năng lượng lơ lửng.").iconSymbol("🔮").avatarBg("#FFF1F2").build(),
                AvatarFrame.builder().frameKey("legendary-cyber-wings").name("Cánh Giáp Cyberpunk").rarity("Legendary").gemCost(4000).description("Cụm cánh giáp Cyberpunk đa tầng nhọn hoắt 2 bên hông kèm mũ giáp sừng chéo đỉnh.").iconSymbol("👾").avatarBg("#ECFEFF").build(),
                AvatarFrame.builder().frameKey("mythic-god-king-dragon").name("Nhà Vô Địch Tuyệt Đối (Thần Vương)").rarity("Mythic").gemCost(9000).description("🏆 KHUNG HOÀNG GIA THẦN THOẠI DUY NHẤT: Đôi cánh Rồng Thần 3D uốn lượn toàn thân, Vương miện Thần Vương 5 đỉnh nạm ngọc Ruby/Diamond, Khiên Rồng phong ấn và các vì sao tự động xoay lấp lánh!").iconSymbol("👑").avatarBg("#FEF3C7").build()
        );

        for (AvatarFrame frame : defaultFrames) {
            var existing = avatarFrameRepository.findByFrameKey(frame.getFrameKey());
            if (existing.isPresent()) {
                AvatarFrame f = existing.get();
                f.setGemCost(frame.getGemCost());
                avatarFrameRepository.save(f);
            } else {
                avatarFrameRepository.save(frame);
            }
        }
    }

    private void initAchievements() {
        List<Achievement> defaultAchievements = List.of(
                Achievement.builder().code("VOCAB_100").title("Học 100 Từ Vựng").description("Trở thành kho từ vựng di động với 100 từ tiếng Anh ghi nhớ sâu.").requirementText("💡 Cách đạt: Hoàn thành và vượt qua bài kiểm tra của 100 từ vựng khác nhau trong các bài học.").category("vocab").progressTarget(100).rewardGems(100).rewardXp(250).badgeIcon("📚").badgeColor("#FEF08A").badgeBorderColor("#EAB308").badgeGlowColor("rgba(234, 179, 8, 0.4)").build(),
                Achievement.builder().code("STREAK_7").title("Đạt Streak 7 Ngày").description("Xây dựng thói quen học tập bền bỉ không bỏ lỡ ngày nào.").requirementText("💡 Cách đạt: Duy trì chuỗi học liên tục mỗi ngày trong 7 ngày liên tiếp.").category("streak").progressTarget(7).rewardGems(150).rewardXp(350).badgeIcon("🔥").badgeColor("#FFEDD5").badgeBorderColor("#F97316").badgeGlowColor("rgba(249, 115, 22, 0.45)").build(),
                Achievement.builder().code("NIGHT_OWL").title("Cú Đêm Học Tập").description("Chăm chỉ học tập khi cả thế giới đang chìm vào giấc ngủ.").requirementText("💡 Cách đạt: Hoàn thành ít nhất 1 bài học trọn vẹn trong khung giờ đêm từ 22:00 đến 04:00 sáng.").category("time").progressTarget(1).rewardGems(80).rewardXp(180).badgeIcon("🌙").badgeColor("#E9D5FF").badgeBorderColor("#8B5CF6").badgeGlowColor("rgba(139, 92, 246, 0.45)").build(),
                Achievement.builder().code("SPEED_DEMON").title("Tốc Độ Ánh Sáng").description("Phản xạ nhanh như chớp với vốn từ vựng chuẩn xác.").requirementText("💡 Cách đạt: Hoàn thành 1 bài kiểm tra 10 câu hỏi với điểm tuyệt đối 100% trong thời gian dưới 90 giây.").category("study").progressTarget(1).rewardGems(120).rewardXp(280).badgeIcon("⚡").badgeColor("#CFFAFE").badgeBorderColor("#06B6D4").badgeGlowColor("rgba(6, 182, 212, 0.4)").build(),
                Achievement.builder().code("GRAMMAR_GURU").title("Bậc Thầy Ngữ Pháp").description("Nắm vững các cấu trúc thì và câu phức trong tiếng Anh.").requirementText("💡 Cách đạt: Vượt qua 20 bài tập thử thách Ngữ Pháp nâng cao không mắc lỗi nào.").category("grammar").progressTarget(20).rewardGems(200).rewardXp(450).badgeIcon("📜").badgeColor("#E0E7FF").badgeBorderColor("#6366F1").badgeGlowColor("rgba(99, 102, 241, 0.4)").build(),
                Achievement.builder().code("SPEAKING_PRO").title("Phát Âm Chuẩn CHỦN").description("Luyện giọng đọc tiếng Anh như người bản xứ.").requirementText("💡 Cách đạt: Đạt điểm đánh giá giọng nói AI từ 90% trở lên cho 30 câu đàm thoại.").category("speaking").progressTarget(30).rewardGems(180).rewardXp(400).badgeIcon("🎙️").badgeColor("#FCE7F3").badgeBorderColor("#EC4899").badgeGlowColor("rgba(236, 72, 153, 0.45)").build(),
                Achievement.builder().code("TOP_LEAGUE").title("Vinh Quang Bảng Xếp Hạng").description("Khẳng định vị thế dẫn đầu trong cộng đồng người học.").requirementText("💡 Cách đạt: Kết thúc tuần ở vị trí Top 3 trên Bảng Xếp Hạng Giải Vàng (Gold League) trở lên.").category("streak").progressTarget(1).rewardGems(300).rewardXp(600).badgeIcon("🏆").badgeColor("#FEF3C7").badgeBorderColor("#D97706").badgeGlowColor("rgba(217, 119, 6, 0.5)").build(),
                Achievement.builder().code("EARLY_BIRD").title("Sơn Dụ Bình Minh").description("Năng lượng dồi dào chào đón ngày mới bằng bài học Tiếng Anh.").requirementText("💡 Cách đạt: Hoàn thành bài học đầu tiên trong ngày vào khung giờ sớm từ 05:00 đến 07:00 sáng.").category("time").progressTarget(1).rewardGems(90).rewardXp(200).badgeIcon("🌅").badgeColor("#FFEDD5").badgeBorderColor("#EA580C").badgeGlowColor("rgba(234, 88, 12, 0.4)").build()
        );

        for (Achievement ach : defaultAchievements) {
            if (!achievementRepository.existsByCode(ach.getCode())) {
                achievementRepository.save(ach);
            }
        }
    }

    public Integer getUserGems(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getGems() != null ? user.getGems() : 0;
    }

    @Transactional
    public Integer addGems(String username, int amount) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        int current = user.getGems() != null ? user.getGems() : 0;
        int updated = current + amount;
        user.setGems(updated);
        userRepository.save(user);
        return updated;
    }

    public List<AvatarFrameResponse> getAllAvatarFrames(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<AvatarFrame> allFrames = avatarFrameRepository.findAll();
        Set<String> unlockedKeys = userAvatarFrameRepository.findByUserId(user.getId())
                .stream()
                .map(UserAvatarFrame::getFrameKey)
                .collect(Collectors.toSet());

        return allFrames.stream().map(frame -> {
            String status = "locked";
            if (frame.getFrameKey().equals(user.getEquippedFrameKey())) {
                status = "equipped";
            } else if (unlockedKeys.contains(frame.getFrameKey())) {
                status = "unlocked";
            }

            return AvatarFrameResponse.builder()
                    .id(frame.getId())
                    .frameKey(frame.getFrameKey())
                    .name(frame.getName())
                    .rarity(frame.getRarity())
                    .gemCost(frame.getGemCost())
                    .description(frame.getDescription())
                    .iconSymbol(frame.getIconSymbol())
                    .avatarBg(frame.getAvatarBg())
                    .status(status)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public AvatarFrameResponse buyAvatarFrame(String username, String frameKey) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        AvatarFrame frame = avatarFrameRepository.findByFrameKey(frameKey)
                .orElseThrow(() -> new RuntimeException("Avatar frame not found"));

        if (userAvatarFrameRepository.existsByUserIdAndFrameKey(user.getId(), frameKey)) {
            throw new RuntimeException("Khung Avatar này đã được mở khóa!");
        }

        int currentGems = user.getGems() != null ? user.getGems() : 0;
        if (currentGems < frame.getGemCost()) {
            throw new RuntimeException("Số Gem hiện tại không đủ để mua khung này!");
        }

        // Deduct gems & save unlocked frame
        user.setGems(currentGems - frame.getGemCost());
        userRepository.save(user);

        UserAvatarFrame userFrame = UserAvatarFrame.builder()
                .userId(user.getId())
                .frameKey(frameKey)
                .unlockedAt(LocalDateTime.now())
                .build();
        userAvatarFrameRepository.save(userFrame);

        return AvatarFrameResponse.builder()
                .id(frame.getId())
                .frameKey(frame.getFrameKey())
                .name(frame.getName())
                .rarity(frame.getRarity())
                .gemCost(frame.getGemCost())
                .description(frame.getDescription())
                .iconSymbol(frame.getIconSymbol())
                .avatarBg(frame.getAvatarBg())
                .status("unlocked")
                .build();
    }

    @Transactional
    public void equipAvatarFrame(String username, String frameKey) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (frameKey != null && !userAvatarFrameRepository.existsByUserIdAndFrameKey(user.getId(), frameKey)) {
            throw new RuntimeException("Bạn chưa mở khóa khung Avatar này!");
        }

        user.setEquippedFrameKey(frameKey);
        userRepository.save(user);
    }

    public List<AchievementResponse> getUserAchievements(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<Achievement> allAchievements = achievementRepository.findAll();
        Map<String, UserAchievement> userAchMap = userAchievementRepository.findByUserId(user.getId())
                .stream()
                .collect(Collectors.toMap(UserAchievement::getAchievementCode, u -> u));

        return allAchievements.stream().map(ach -> {
            UserAchievement uAch = userAchMap.get(ach.getCode());
            int progressCurrent = uAch != null ? uAch.getProgressCurrent() : 0;
            String status = uAch != null ? (uAch.getStatus() != null ? uAch.getStatus().toLowerCase() : "locked") : "locked";

            return AchievementResponse.builder()
                    .id(ach.getId())
                    .code(ach.getCode())
                    .title(ach.getTitle())
                    .description(ach.getDescription())
                    .requirementText(ach.getRequirementText())
                    .category(ach.getCategory())
                    .progressCurrent(progressCurrent)
                    .progressTarget(ach.getProgressTarget())
                    .rewardGems(ach.getRewardGems())
                    .rewardXp(ach.getRewardXp())
                    .badgeIcon(ach.getBadgeIcon())
                    .badgeColor(ach.getBadgeColor())
                    .badgeBorderColor(ach.getBadgeBorderColor())
                    .badgeGlowColor(ach.getBadgeGlowColor())
                    .status(status)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public AchievementResponse claimAchievementReward(String username, String achievementCode) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Achievement ach = achievementRepository.findByCode(achievementCode)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));

        UserAchievement uAch = userAchievementRepository.findByUserIdAndAchievementCode(user.getId(), achievementCode)
                .orElseThrow(() -> new RuntimeException("Thành tựu chưa sẵn sàng để nhận thưởng!"));

        if (!"claimable".equalsIgnoreCase(uAch.getStatus())) {
            throw new RuntimeException("Phần thưởng thành tựu này đã nhận hoặc chưa đạt điều kiện!");
        }

        uAch.setStatus("claimed");
        uAch.setClaimedAt(LocalDateTime.now());
        userAchievementRepository.save(uAch);

        int currentGems = user.getGems() != null ? user.getGems() : 0;
        user.setGems(currentGems + ach.getRewardGems());
        userRepository.save(user);

        return AchievementResponse.builder()
                .id(ach.getId())
                .code(ach.getCode())
                .title(ach.getTitle())
                .description(ach.getDescription())
                .requirementText(ach.getRequirementText())
                .category(ach.getCategory())
                .progressCurrent(uAch.getProgressCurrent())
                .progressTarget(ach.getProgressTarget())
                .rewardGems(ach.getRewardGems())
                .rewardXp(ach.getRewardXp())
                .badgeIcon(ach.getBadgeIcon())
                .badgeColor(ach.getBadgeColor())
                .badgeBorderColor(ach.getBadgeBorderColor())
                .badgeGlowColor(ach.getBadgeGlowColor())
                .status("claimed")
                .build();
    }
}