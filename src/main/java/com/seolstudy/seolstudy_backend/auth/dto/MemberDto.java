package com.seolstudy.seolstudy_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MemberDto {
    private Long id;
    private String name;
    private String role;
    private String profileImageUrl;
}
