package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.service.ReportService;
import com.example.GrowLink.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ReportService reportService;

    public AdminController(UserService userService, ReportService reportService) {
        this.userService = userService;
        this.reportService = reportService;
    }

    @GetMapping
    public String showAdminDashboard(Model model) {
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("disabledUsers", userService.getDisabledUserCount());
        model.addAttribute("totalReports", reportService.getTotalReports());
        return "admin/admin-dashboard";
    }

    @GetMapping("/users")
    public String showUsersPage(Model model,
                                @RequestParam(value = "message", required = false) String message) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("message", message);
        return "admin/users";
    }

    @PostMapping("/users/{userId}/disable")
    public String disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
        return "redirect:/admin/users?message=" + URLEncoder.encode("User disabled successfully.", StandardCharsets.UTF_8);
    }

    @PostMapping("/users/{userId}/enable")
    public String enableUser(@PathVariable Long userId) {
        userService.enableUser(userId);
        return "redirect:/admin/users?message=" + URLEncoder.encode("User enabled successfully.", StandardCharsets.UTF_8);
    }

    @GetMapping("/reports")
    public String showReportsPage(Model model,
                                  @RequestParam(value = "message", required = false) String message) {
        model.addAttribute("reports", reportService.getAllReports());
        model.addAttribute("message", message);
        return "admin/reports";
    }

    @PostMapping("/reports/{reportId}/review")
    public String markReviewed(@PathVariable Long reportId) {
        reportService.markReviewed(reportId);
        return "redirect:/admin/reports?message=" + URLEncoder.encode("Report marked as reviewed.", StandardCharsets.UTF_8);
    }

    @PostMapping("/reports/{reportId}/resolve")
    public String markResolved(@PathVariable Long reportId) {
        reportService.markResolved(reportId);
        return "redirect:/admin/reports?message=" + URLEncoder.encode("Report marked as resolved.", StandardCharsets.UTF_8);
    }

    @PostMapping("/reports/{reportId}/reject")
    public String markRejected(@PathVariable Long reportId) {
        reportService.markRejected(reportId);
        return "redirect:/admin/reports?message=" + URLEncoder.encode("Report rejected.", StandardCharsets.UTF_8);
    }
}