package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarFrameResponse {
    String id;
    String frameKey;
    String name;
    String rarity;
    Integer gemCost;
    String description;
    String iconSymbol;
    String avatarBg;
    String status; // "UNLOCKED", "EQUIPPED", "LOCKED"
}
