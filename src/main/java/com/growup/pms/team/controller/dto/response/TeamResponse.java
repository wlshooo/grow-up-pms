package com.growup.pms.team.controller.dto.response;

import com.growup.pms.team.domain.Team;
import lombok.Builder;

@Builder
public record TeamResponse(String teamName, String content, Long creatorId) {
    public static TeamResponse from(Team team) {
        return TeamResponse.builder()
                .teamName(team.getName())
                .content(team.getContent())
                .creatorId(team.getCreator().getId())
                .build();
    }
}
