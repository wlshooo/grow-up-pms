package com.growup.pms.user.controller.dto.response;

import lombok.Builder;

@Builder
public record UserSearchResponse(Long userId, String nickname) {
}
