package com.issue.backend.controller;

import com.issue.backend.dto.ApiResponse;
import com.issue.backend.dto.IssueDTO;
import com.issue.backend.entity.IssuePriority;
import com.issue.backend.entity.IssueStatus;
import com.issue.backend.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueDTO> createIssue(@RequestBody IssueDTO issueDTO) {
        return ResponseEntity.ok(issueService.createIssue(issueDTO));
    }

    @GetMapping
    public ResponseEntity<List<IssueDTO>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueDTO> getIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssueById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueDTO> updateIssue(@PathVariable Long id, @RequestBody IssueDTO issueDTO) {
        return ResponseEntity.ok(issueService.updateIssue(id, issueDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.ok(new ApiResponse(true, "Issue deleted successfully"));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<IssueDTO>> filterIssues(
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) IssuePriority priority) {
        return ResponseEntity.ok(issueService.filterIssues(status, priority));
    }
}
