package com.lolavictoria.service;

import com.lolavictoria.controller.QuestionController.CategoryStats;
import com.lolavictoria.entity.Category;
import com.lolavictoria.entity.Question;
import com.lolavictoria.entity.Status;
import com.lolavictoria.repository.QuestionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question addQuestion(String link, String title, Category category, Status status) {
        if (questionRepository.existsByLink(link)) {
            throw new IllegalArgumentException("This question has already been added: " + link);
        }
        Question question = new Question();
        question.setLink(link);
        question.setTitle(title);
        question.setCategory(category);
        question.setStatus(status);
        return questionRepository.save(question);
    }

    public List<Question> getFilteredQuestions(Status status, Category category) {
        return questionRepository.findAll().stream()
                .filter(q -> status == null || q.getStatus() == status)
                .filter(q -> category == null || q.getCategory() == category)
                .toList();
    }

    public Question updateStatus(Long questionId, Status newStatus) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        question.setStatus(newStatus);
        question.setLastPracticedAt(LocalDateTime.now());
        question.setPracticeCount(question.getPracticeCount() + 1);
         question.setNextReviewAt(calculateNextReviewAt(newStatus));

        return questionRepository.save(question);
    }

    private LocalDateTime calculateNextReviewAt(Status status) {
        return switch (status) {
            case PERFECT -> null;
            case NEEDS_RETRY -> LocalDateTime.now().plusDays(1);
            case MEDIUM -> LocalDateTime.now().plusDays(3);
        };
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));
    }
    public List<Question> getDueQuestions() {
        System.out.println("SCHEDULED JOB FIRED AT: " + java.time.LocalDateTime.now());
        return questionRepository.findAll().stream()
                .filter(q -> q.getStatus() != Status.PERFECT)
                .filter(q -> q.getNextReviewAt() != null && !q.getNextReviewAt().isAfter(LocalDateTime.now()))
                .toList();
    }

    public Question updateNotes(Long questionId, String notes) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        question.setNotes(notes);
        return questionRepository.save(question);
    }

    public Question updateQuestion(Long id, String title, Category category, String notes) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));

        if (title != null) question.setTitle(title);
        if (category != null) question.setCategory(category);
        if (notes != null) question.setNotes(notes);

        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("Question not found: " + id);
        }
        questionRepository.deleteById(id);
    }

    public List<CategoryStats> getCategoryBreakdown() {
        List<Question> all = questionRepository.findAll();
        Map<Category, List<Question>> byCategory = all.stream()
                .filter(q -> q.getCategory() != null)
                .collect(Collectors.groupingBy(Question::getCategory));

        return byCategory.entrySet().stream()
            .map(entry -> {
                List<Question> qs = entry.getValue();
                long perfect = qs.stream().filter(q -> q.getStatus() == Status.PERFECT).count();
                long medium = qs.stream().filter(q -> q.getStatus() == Status.MEDIUM).count();
                long needsRetry = qs.stream().filter(q -> q.getStatus() == Status.NEEDS_RETRY).count();
                return new CategoryStats(entry.getKey(), perfect, medium, needsRetry);
            })
            .toList();

    }
}