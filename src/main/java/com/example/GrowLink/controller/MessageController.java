package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.entity.Conversation;
import com.example.GrowLink.entity.Message;
import com.example.GrowLink.service.MessageService;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String messagesPage(@RequestParam(value = "conversationId", required = false) Long conversationId,
                               @RequestParam(value = "message", required = false) String message,
                               Principal principal,
                               Model model) {

        String email = principal.getName();
        List<Conversation> conversations = messageService.getUserConversations(email);

        model.addAttribute("conversations", conversations);
        model.addAttribute("message", message);

        if (conversationId != null) {
            if (!messageService.isUserInConversation(conversationId, email)) {
                return "redirect:/messages?message=" + URLEncoder.encode("You cannot open that conversation.", StandardCharsets.UTF_8);
            }

            Conversation selectedConversation = messageService.getConversationById(conversationId);
            List<Message> messages = messageService.getMessagesByConversation(conversationId);

            model.addAttribute("selectedConversation", selectedConversation);
            model.addAttribute("messages", messages);
            model.addAttribute("otherUser", messageService.getOtherUser(selectedConversation, email));
        }

        return "messages/messages";
    }

    @PostMapping("/start")
    public String startConversation(@RequestParam("otherUserId") Long otherUserId,
                                    Principal principal) {

        Conversation conversation = messageService.startConversation(principal.getName(), otherUserId);
        return "redirect:/messages?conversationId=" + conversation.getId();
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("conversationId") Long conversationId,
                              @RequestParam("content") String content,
                              Principal principal) {

        messageService.sendMessage(conversationId, principal.getName(), content);
        return "redirect:/messages?conversationId=" + conversationId;
    }
}