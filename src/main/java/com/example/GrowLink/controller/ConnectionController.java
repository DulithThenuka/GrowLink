package com.example.GrowLink.controller;

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
    public String showConnectionsPage(Model model, Principal principal,
                                      @RequestParam(value = "message", required = false) String message) {

        model.addAttribute("users", connectionService.getOtherUsers(principal.getName()));
        model.addAttribute("sentRequests", connectionService.getSentRequests(principal.getName()));
        model.addAttribute("receivedRequests", connectionService.getReceivedRequests(principal.getName()));
        model.addAttribute("acceptedConnections", connectionService.getAcceptedConnections(principal.getName()));
        model.addAttribute("message", message);

        return "connections/connections";
    }

    @PostMapping("/send/{receiverId}")
    public String sendRequest(@PathVariable Long receiverId, Principal principal) {
        String message = connectionService.sendConnectionRequest(principal.getName(), receiverId);
        return "redirect:/connections?message=" + message.replace(" ", "%20");
    }

    @PostMapping("/accept/{requestId}")
    public String acceptRequest(@PathVariable Long requestId, Principal principal) {
        String message = connectionService.acceptRequest(principal.getName(), requestId);
        return "redirect:/connections?message=" + message.replace(" ", "%20");
    }

    @PostMapping("/reject/{requestId}")
    public String rejectRequest(@PathVariable Long requestId, Principal principal) {
        String message = connectionService.rejectRequest(principal.getName(), requestId);
        return "redirect:/connections?message=" + message.replace(" ", "%20");
    }
}