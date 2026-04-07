package com.example.GrowLink.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.LearningRequestDto;
import com.example.GrowLink.entity.LearningRequest;
import com.example.GrowLink.entity.Skill;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.entity.UserTeachSkill;
import com.example.GrowLink.enums.RequestStatus;
import com.example.GrowLink.repository.LearningRequestRepository;
import com.example.GrowLink.repository.SkillRepository;
import com.example.GrowLink.repository.UserRepository;
import com.example.GrowLink.repository.UserTeachSkillRepository;

@Service
public class LearningRequestService {

    private final LearningRequestRepository learningRequestRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserTeachSkillRepository userTeachSkillRepository;

    public LearningRequestService(LearningRequestRepository learningRequestRepository,
                                  UserService userService,
                                  UserRepository userRepository,
                                  SkillRepository skillRepository,
                                  UserTeachSkillRepository userTeachSkillRepository) {
        this.learningRequestRepository = learningRequestRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userTeachSkillRepository = userTeachSkillRepository;
    }

    public List<LearningRequest> getSentRequests(String email) {
        User learner = userService.getUserByEmail(email);
        return learningRequestRepository.findByLearner(learner);
    }

    public List<LearningRequest> getReceivedRequests(String email) {
        User teacher = userService.getUserByEmail(email);
        return learningRequestRepository.findByTeacher(teacher);
    }

    public List<UserTeachSkill> getAllTeachSkills() {
        return userTeachSkillRepository.findAll();
    }

    @Transactional
    public String sendLearningRequest(String learnerEmail, LearningRequestDto dto) {
        User learner = userService.getUserByEmail(learnerEmail);
        User teacher = userRepository.findById(dto.getTeacherId()).orElse(null);
        Skill skill = skillRepository.findById(dto.getSkillId()).orElse(null);

        if (teacher == null) {
            return "Teacher not found.";
        }

        if (skill == null) {
            return "Skill not found.";
        }

        if (learner.getId().equals(teacher.getId())) {
            return "You cannot send a learning request to yourself.";
        }

        LearningRequest request = new LearningRequest();
        request.setLearner(learner);
        request.setTeacher(teacher);
        request.setSkill(skill);
        request.setMessage(dto.getMessage());
        request.setStatus(RequestStatus.PENDING);

        learningRequestRepository.save(request);
        return "Learning request sent successfully.";
    }

    @Transactional
    public String acceptRequest(String teacherEmail, Long requestId) {
        User teacher = userService.getUserByEmail(teacherEmail);
        LearningRequest request = learningRequestRepository.findByIdAndTeacher(requestId, teacher).orElse(null);

        if (request == null) {
            return "Learning request not found.";
        }

        request.setStatus(RequestStatus.ACCEPTED);
        learningRequestRepository.save(request);

        return "Learning request accepted.";
    }

    @Transactional
    public String rejectRequest(String teacherEmail, Long requestId) {
        User teacher = userService.getUserByEmail(teacherEmail);
        LearningRequest request = learningRequestRepository.findByIdAndTeacher(requestId, teacher).orElse(null);

        if (request == null) {
            return "Learning request not found.";
        }

        request.setStatus(RequestStatus.REJECTED);
        learningRequestRepository.save(request);

        return "Learning request rejected.";
    }

    @Transactional
    public String completeRequest(String teacherEmail, Long requestId) {
        User teacher = userService.getUserByEmail(teacherEmail);
        LearningRequest request = learningRequestRepository.findByIdAndTeacher(requestId, teacher).orElse(null);

        if (request == null) {
            return "Learning request not found.";
        }

        request.setStatus(RequestStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        learningRequestRepository.save(request);

        return "Learning request marked as completed.";
    }
}