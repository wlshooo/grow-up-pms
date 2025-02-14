package com.growup.pms.user.controller.dto.request;

import com.growup.pms.user.service.dto.UserLinksUpdateCommand;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.openapitools.jackson.nullable.JsonNullable;
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLinksUpdateRequest {
    private JsonNullable<List<@URL @Length(max = 255) String>> links = JsonNullable.undefined();

    @Builder
    public UserLinksUpdateRequest(JsonNullable<List<@URL @Length(max = 255) String>> links) {
        this.links = links;
    }

    public UserLinksUpdateCommand toCommand() {
        return UserLinksUpdateCommand.builder()
                .links(links)
                .build();
    }
}
