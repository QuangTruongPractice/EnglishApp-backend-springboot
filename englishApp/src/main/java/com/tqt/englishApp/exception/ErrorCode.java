package com.tqt.englishApp.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNAUTHORIZED_EXCEPTION(9999, "Unauthorized"),
    USERNAME_EXISTED(1001, "Username đã tồn tại"),
    EMAIL_EXISTED(1002, "Email đã tồn tại"),
    USERNAME_INVALID(1003, "Username phải có ít nhất 3 kí tự"),
    EMAIL_INVALID(1004,"Phải đúng định dạng Email"),
    PASSWORD_INVALID(1005, "Password phải có ít nhất 6 kí tự"),
    AVATAR_REQUIRED(1006, "Avatar là bắt buộc"),
    IMAGE_REQUIRED(1006, "Image là bắt buộc"),
    USER_NOT_EXISTED(1007, "User chưa tồn tại"),
    UNAUTHENTICATED(1008, "Đăng nhập thất bại"),
    USERNAME_NOT_FOUND(1009, "Không tìm thấy username"),
    TOPIC_NOT_EXISTED(1010, "Không tìm thấy topic"),
    VOCABULARY_NOT_EXISTED(1011, "Không tìm thấy vocabulary"),
    WORDTYPE_NOT_EXISTED(1012, "Không tìm thấy Word type"),
    VIDEO_NOT_EXISTED(1013, "Không tìm thấy video"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    int code;
    String message;
}
