package com.goviya.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String phone;
    private String name;
    private String role;
    private String district;
    private String language;
    private String fcmToken;
    private Float rating;
    private LocalDateTime createdAt;
}
