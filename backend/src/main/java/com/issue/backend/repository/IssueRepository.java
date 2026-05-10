package com.issue.backend.repository;

import com.issue.backend.entity.Issue;
import com.issue.backend.entity.IssuePriority;
import com.issue.backend.entity.IssueStatus;
import com.issue.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByReportedBy(User reportedBy);
    List<Issue> findByStatus(IssueStatus status);
    List<Issue> findByPriority(IssuePriority priority);
    List<Issue> findByStatusAndPriority(IssueStatus status, IssuePriority priority);
}
