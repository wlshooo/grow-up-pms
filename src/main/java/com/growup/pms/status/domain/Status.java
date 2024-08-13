package com.growup.pms.status.domain;

import com.growup.pms.project.domain.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "project_status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_status_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private Project project;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(nullable = false, length = 6)
    private String colorCode;

    @Column(nullable = false)
    private Short sortOrder;

    @Builder
    public Status(Project project, String name, String colorCode, Short sortOrder) {
        this.project = project;
        this.name = name;
        this.colorCode = colorCode;
        this.sortOrder = sortOrder;
    }

    // Business Logics //
    public void editName(String name) {
        this.name = name;
    }

    public void editColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public void editSortOrder(Short sortOrder) {
        this.sortOrder = sortOrder;
    }
}
