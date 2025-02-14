package com.growup.pms.status.controller.dto.response;

import com.growup.pms.status.domain.Status;
import lombok.Builder;

@Builder
public record StatusResponse(Long statusId, Long projectId, String statusName, String colorCode, Short sortOrder) {
    public static StatusResponse of(Status status) {
        return StatusResponse.builder()
                .statusId(status.getId())
                .projectId(status.getProject().getId())
                .statusName(status.getName())
                .colorCode(status.getColorCode())
                .sortOrder(status.getSortOrder())
                .build();
    }
}
