package com.growup.pms.project.controller.dto.request;

import static com.growup.pms.common.constant.RegexConstants.LOCAL_DATE_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.growup.pms.project.service.dto.ProjectCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectCreateRequest {

    @NotBlank
    @Size(max = 128)
    private String projectName;

    @Size(max = 200)
    private String content;

    @JsonFormat(pattern = LOCAL_DATE_PATTERN)
    @NotNull
    private LocalDate startDate;

    @JsonFormat(pattern = LOCAL_DATE_PATTERN)
    @NotNull
    private LocalDate endDate;

    private List<ProjectUserCreateRequest> coworkers;

    @Builder
    public ProjectCreateRequest(String projectName, String content, LocalDate startDate, LocalDate endDate,
                                List<ProjectUserCreateRequest> coworkers) {
        this.projectName = projectName;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coworkers = coworkers == null ? new ArrayList<>() : coworkers;
    }

    public ProjectCreateCommand toCommand() {
        return ProjectCreateCommand.builder()
                .projectName(projectName)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .coworkers(coworkers.stream().map(ProjectUserCreateRequest::toCommand).toList())
                .build();
    }
}
