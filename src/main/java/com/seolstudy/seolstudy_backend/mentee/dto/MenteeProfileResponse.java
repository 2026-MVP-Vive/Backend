package com.seolstudy.seolstudy_backend.mentee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeProfileResponse {
    private Long id;
    private String name;
    private String profileImageUrl;
    private String mentorName;
    private LocalDate startDate;
    private long totalStudyDays;
}
