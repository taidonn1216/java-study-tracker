package com.example.tracker.controller;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import com.example.tracker.model.Task;
import com.example.tracker.repository.SubjectRepository;
import com.example.tracker.repository.TaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 学習進捗トラッカーのコントローラー
 */
@Controller
public class TrackerController {
    
    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    
    public TrackerController(SubjectRepository subjectRepository, TaskRepository taskRepository) {
        this.subjectRepository = subjectRepository;
        this.taskRepository = taskRepository;
    }
    
    /**
     * 科目一覧ページ (/)
     */
    @GetMapping("/")
    public String index(Model model) {
        List<SubjectSummary> subjects = subjectRepository.findAllWithTaskStats();
        model.addAttribute("subjects", subjects);
        return "index";
    }
    
    /**
     * 科目登録処理
     */
    @PostMapping("/subjects")
    public String createSubject(@RequestParam("name") String name) {
        subjectRepository.insert(name);
        return "redirect:/";
    }
    
    /**
     * 科目削除処理
     */
    @PostMapping("/subjects/{id}/delete")
    public String deleteSubject(@PathVariable("id") Long id) {
        subjectRepository.deleteById(id);
        return "redirect:/";
    }
    
    /**
     * タスク一覧ページ (/subjects/{id})
     */
    @GetMapping("/subjects/{id}")
    public String subjectDetails(@PathVariable("id") Long id, Model model) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        
        if (subjectOpt.isEmpty()) {
            return "redirect:/";
        }
        
        Subject subject = subjectOpt.get();
        List<Task> tasks = taskRepository.findBySubjectId(id);
        
        // タスク統計を計算
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(task -> Boolean.TRUE.equals(task.getCompleted())).count();
        long incompleteTasks = totalTasks - completedTasks;
        
        model.addAttribute("subject", subject);
        model.addAttribute("tasks", tasks);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("incompleteTasks", incompleteTasks);
        
        return "subject_details";
    }
    
    /**
     * タスク登録処理
     */
    @PostMapping("/subjects/{subjectId}/tasks")
    public String createTask(
            @PathVariable("subjectId") Long subjectId,
            @RequestParam("title") String title) {
        taskRepository.insert(subjectId, title);
        return "redirect:/subjects/" + subjectId;
    }
    
    /**
     * タスク完了処理
     */
    @PostMapping("/tasks/{taskId}/complete")
    public String completeTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("completed") boolean completed) {
        taskRepository.updateCompleted(taskId, completed);
        return "redirect:/subjects/" + subjectId;
    }
    
    /**
     * タスク削除処理
     */
    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId) {
        taskRepository.deleteById(taskId);
        return "redirect:/subjects/" + subjectId;
    }
}
