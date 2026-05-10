package com.issue.backend.dto;

import com.issue.backend.entity.IssuePriority;
import com.issue.backend.entity.IssueStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IssueDTO {
    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;
    private String reportedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
