package com.example.GrowLink.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.ReportDto;
import com.example.GrowLink.entity.Report;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.NotificationType;
import com.example.GrowLink.enums.ReportStatus;
import com.example.GrowLink.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public ReportService(ReportRepository reportRepository,
                         UserService userService,
                         NotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public long getTotalReports() {
        return reportRepository.count();
    }

    public long getReportsAgainstUser(Long userId) {
        User user = userService.getUserById(userId);
        return reportRepository.countByReportedUser(user);
    }

    @Transactional
    public void submitReport(String reporterEmail, ReportDto dto) {
        User reporter = userService.getUserByEmail(reporterEmail);
        User reportedUser = userService.getUserById(dto.getReportedUserId());

        if (reporter.getId().equals(reportedUser.getId())) {
            throw new IllegalArgumentException("You cannot report yourself.");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);
        report.setReason(dto.getReason().trim());
        report.setDetails(dto.getDetails() != null ? dto.getDetails().trim() : null);
        report.setStatus(ReportStatus.PENDING);

        reportRepository.save(report);

        notificationService.createNotification(
                reportedUser,
                "Account Report Submitted",
                "A report involving your account has been submitted and may be reviewed by admins.",
                NotificationType.SYSTEM
        );
    }

    @Transactional
    public void markReviewed(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found."));
        report.setStatus(ReportStatus.REVIEWED);
        reportRepository.save(report);
    }

    @Transactional
    public void markResolved(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found."));
        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);
    }

    @Transactional
    public void markRejected(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found."));
        report.setStatus(ReportStatus.REJECTED);
        reportRepository.save(report);
    }
}