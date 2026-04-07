package com.example.GrowLink.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.LearnSkillDto;
import com.example.GrowLink.dto.TeachSkillDto;
import com.example.GrowLink.enums.SkillLevel;
import com.example.GrowLink.service.SkillService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public String showMySkillsPage(Model model, Principal principal) {
        if (!model.containsAttribute("teachSkillDto")) {
            model.addAttribute("teachSkillDto", new TeachSkillDto());
        }

        if (!model.containsAttribute("learnSkillDto")) {
            model.addAttribute("learnSkillDto", new LearnSkillDto());
        }

        model.addAttribute("skillLevels", SkillLevel.values());
        model.addAttribute("teachSkills", skillService.getTeachSkillsByUserEmail(principal.getName()));
        model.addAttribute("learnSkills", skillService.getLearnSkillsByUserEmail(principal.getName()));

        return "skills/my-skills";
    }

    @PostMapping("/teach")
    public String addTeachSkill(@Valid @ModelAttribute("teachSkillDto") TeachSkillDto teachSkillDto,
                                BindingResult bindingResult,
                                @ModelAttribute("learnSkillDto") LearnSkillDto learnSkillDto,
                                Principal principal,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("learnSkillDto", learnSkillDto);
            model.addAttribute("skillLevels", SkillLevel.values());
            model.addAttribute("teachSkills", skillService.getTeachSkillsByUserEmail(principal.getName()));
            model.addAttribute("learnSkills", skillService.getLearnSkillsByUserEmail(principal.getName()));
            return "skills/my-skills";
        }

        skillService.addTeachSkill(principal.getName(), teachSkillDto);

        return "redirect:/skills?teachSuccess";
    }

    @PostMapping("/learn")
    public String addLearnSkill(@Valid @ModelAttribute("learnSkillDto") LearnSkillDto learnSkillDto,
                                BindingResult bindingResult,
                                @ModelAttribute("teachSkillDto") TeachSkillDto teachSkillDto,
                                Principal principal,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("teachSkillDto", teachSkillDto);
            model.addAttribute("skillLevels", SkillLevel.values());
            model.addAttribute("teachSkills", skillService.getTeachSkillsByUserEmail(principal.getName()));
            model.addAttribute("learnSkills", skillService.getLearnSkillsByUserEmail(principal.getName()));
            return "skills/my-skills";
        }

        skillService.addLearnSkill(principal.getName(), learnSkillDto);

        return "redirect:/skills?learnSuccess";
    }
}