package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.ReportDto;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.UserService;
import com.example.GrowLink.service.ReportService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/user/{userId}")
    public String showReportUserPage(@PathVariable Long userId, Model model) {
        User reportedUser = userService.getUserById(userId);

        ReportDto reportDto = new ReportDto();
        reportDto.setReportedUserId(userId);

        model.addAttribute("reportedUser", reportedUser);
        model.addAttribute("reportDto", reportDto);

        return "report/report-user";
    }

    @PostMapping("/submit")
    public String submitReport(@Valid @ModelAttribute("reportDto") ReportDto reportDto,
                               BindingResult bindingResult,
                               Principal principal,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("reportedUser", userService.getUserById(reportDto.getReportedUserId()));
            return "report/report-user";
        }

        reportService.submitReport(principal.getName(), reportDto);

        return "redirect:/profile?message=" + URLEncoder.encode("Report submitted successfully.", StandardCharsets.UTF_8);
    }
}