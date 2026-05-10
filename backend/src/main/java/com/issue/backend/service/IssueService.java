package com.issue.backend.service;

import com.issue.backend.dto.IssueDTO;
import com.issue.backend.entity.*;
import com.issue.backend.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserService userService;

    public IssueDTO createIssue(IssueDTO issueDTO) {
        User currentUser = userService.getCurrentUser();
        Issue issue = Issue.builder()
                .title(issueDTO.getTitle())
                .description(issueDTO.getDescription())
                .status(IssueStatus.PENDING)
                .priority(issueDTO.getPriority())
                .reportedBy(currentUser)
                .build();

        Issue savedIssue = issueRepository.save(issue);
        return mapToDTO(savedIssue);
    }

    public List<IssueDTO> getAllIssues() {
        User currentUser = userService.getCurrentUser();
        List<Issue> issues;
        
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            issues = issueRepository.findAll();
        } else {
            issues = issueRepository.findByReportedBy(currentUser);
        }
        
        return issues.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public IssueDTO getIssueById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ROLE_ADMIN && !issue.getReportedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to issue");
        }
        
        return mapToDTO(issue);
    }

    public IssueDTO updateIssue(Long id, IssueDTO issueDTO) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        User currentUser = userService.getCurrentUser();
        
        // Users can only update their own issues, Admins can update any
        if (currentUser.getRole() != Role.ROLE_ADMIN && !issue.getReportedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized update");
        }

        issue.setTitle(issueDTO.getTitle());
        issue.setDescription(issueDTO.getDescription());
        issue.setPriority(issueDTO.getPriority());
        
        // Only admins or the owner can update status (usually owners can't resolve their own issues unless allowed)
        if (issueDTO.getStatus() != null) {
            issue.setStatus(issueDTO.getStatus());
        }

        Issue updatedIssue = issueRepository.save(issue);
        return mapToDTO(updatedIssue);
    }

    public void deleteIssue(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ROLE_ADMIN && !issue.getReportedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized deletion");
        }
        
        issueRepository.delete(issue);
    }

    public List<IssueDTO> filterIssues(IssueStatus status, IssuePriority priority) {
        List<Issue> issues;
        if (status != null && priority != null) {
            issues = issueRepository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            issues = issueRepository.findByStatus(status);
        } else if (priority != null) {
            issues = issueRepository.findByPriority(priority);
        } else {
            issues = issueRepository.findAll();
        }
        
        // For non-admins, filter to only their own
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            issues = issues.stream()
                    .filter(i -> i.getReportedBy().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        }
        
        return issues.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private IssueDTO mapToDTO(Issue issue) {
        IssueDTO dto = new IssueDTO();
        dto.setId(issue.getId());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setStatus(issue.getStatus());
        dto.setPriority(issue.getPriority());
        dto.setReportedBy(issue.getReportedBy().getUsername());
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setUpdatedAt(issue.getUpdatedAt());
        return dto;
    }
}
