package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.service.ConnectionService;

@Controller
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @GetMapping
    public String connectionsPage(Model model,
                                  Principal principal,
                                  @RequestParam(value = "message", required = false) String message) {

        String email = principal.getName();

        model.addAttribute("sentRequests", connectionService.getSentRequests(email));
        model.addAttribute("receivedRequests", connectionService.getReceivedRequests(email));
        model.addAttribute("connections", connectionService.getAcceptedConnections(email));
        model.addAttribute("message", message);

        return "connections/connections";
    }

    @PostMapping("/send")
    public String sendConnectionRequest(@RequestParam("receiverId") Long receiverId,
                                        Principal principal,
                                        @RequestParam(value = "redirectTo", required = false) String redirectTo) {

        String message = connectionService.sendConnectionRequest(principal.getName(), receiverId);

        if (redirectTo != null && !redirectTo.isBlank()) {
            return "redirect:" + redirectTo + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        }

        return "redirect:/connections?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/accept")
    public String acceptRequest(@RequestParam("requestId") Long requestId,
                                Principal principal) {

        String message = connectionService.acceptRequest(requestId, principal.getName());
        return "redirect:/connections?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/reject")
    public String rejectRequest(@RequestParam("requestId") Long requestId,
                                Principal principal) {

        String message = connectionService.rejectRequest(requestId, principal.getName());
        return "redirect:/connections?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}