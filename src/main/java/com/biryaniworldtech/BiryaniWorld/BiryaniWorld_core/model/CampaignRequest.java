package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CampaignRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private LocalDateTime scheduledTime;

    @NotBlank
    private String targetAudience;
} 