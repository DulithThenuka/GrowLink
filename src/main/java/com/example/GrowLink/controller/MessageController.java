package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.service.MessageService;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String showMessagesPage(@RequestParam(value = "userId", required = false) Long userId,
                                   @RequestParam(value = "message", required = false) String flashMessage,
                                   Principal principal,
                                   Model model) {

        model.addAttribute("allUsers", messageService.getAllOtherUsers(principal.getName()));
        model.addAttribute("conversationUsers", messageService.getConversationUsers(principal.getName()));
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("flashMessage", flashMessage);

        if (userId != null) {
            model.addAttribute("messages", messageService.getConversation(principal.getName(), userId));
        }

        return "messages/messages";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("receiverId") Long receiverId,
                              @RequestParam("messageText") String messageText,
                              Principal principal) {

        String message = messageService.sendMessage(principal.getName(), receiverId, messageText);

        return "redirect:/messages?userId=" + receiverId +
                "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}