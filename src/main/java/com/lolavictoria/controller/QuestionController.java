package com.lolavictoria.controller;

import com.lolavictoria.entity.Category;
import com.lolavictoria.entity.Question;
import com.lolavictoria.entity.Status;
import com.lolavictoria.service.QuestionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<Question> addQuestion(@Valid @RequestBody AddQuestionRequest request) {
        Question question = questionService.addQuestion(request.link(), request.title(), request.category());
        return ResponseEntity.ok(question);
    }

    @PatchMapping("update-status/{id}")
    public ResponseEntity<Question>updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody PracticeRequest request) {
        Question question = questionService.updateStatus(id, request.status());
        return ResponseEntity.ok(question);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getOneQuestion(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping
    public ResponseEntity<List<Question>> getQuestions(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Category category) {
        return ResponseEntity.ok(questionService.getFilteredQuestions(status, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<List<CategoryStats>> getAnalytics() {
        return ResponseEntity.ok(questionService.getCategoryBreakdown());
    }

    @GetMapping("/due")
    public ResponseEntity<List<Question>> getDueQuestions() {
        return ResponseEntity.ok(questionService.getDueQuestions());
    }

    @PatchMapping("/notes/{id}")
    public ResponseEntity<Question> updatesNotes(
            @PathVariable Long id,
            @RequestBody NotesRequest request) {
        Question question = questionService.updateNotes(id, request.notes());
        return ResponseEntity.ok(question);
        }

    
   
    
    public record CategoryStats(Category category, long perfect, long medium, long needsRetry) {}
    public record AddQuestionRequest(
        @NotBlank(message = "Link is required") String link,
        @NotBlank(message = "Title is required") String title,
        Category category
    ) {}
    public record PracticeRequest(
        @jakarta.validation.constraints.NotNull(message = "Status is required") Status status
    ) {}
    public record NotesRequest(
        @NotBlank(message = "Notes cannot be empty") String notes
    ) {}
} 