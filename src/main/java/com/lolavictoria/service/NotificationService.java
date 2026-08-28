package com.lolavictoria.service;

import com.lolavictoria.entity.Question;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final QuestionService questionService;
    private final JavaMailSender mailSender;

    public NotificationService(QuestionService questionService, JavaMailSender mailSender) {
        this.questionService = questionService;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void sendDueQuestionsEmail() {
        List<Question> dueQuestions = questionService.getDueQuestions();

        if (dueQuestions.isEmpty()) {
            return;
        }

        StringBuilder body = new StringBuilder("Questions due for practice today:\n\n");
        for (Question q : dueQuestions) {
            body.append("- ").append(q.getTitle() != null ? q.getTitle() : q.getLink())
                    .append(" (").append(q.getStatus()).append(")\n")
                    .append("  ").append(q.getLink()).append("\n\n");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("damilolaoniyide11@gmail.com"); // your real inbox
        message.setSubject("LeetCode Tracker: " + dueQuestions.size() + " question(s) due today");
        message.setText(body.toString());
        message.setFrom("studyapptracker@gmail.com");
        mailSender.send(message);
    }
}