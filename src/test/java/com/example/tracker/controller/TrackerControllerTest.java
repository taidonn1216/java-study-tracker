package com.example.tracker.controller;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import com.example.tracker.model.Task;
import com.example.tracker.repository.SubjectRepository;
import com.example.tracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * TrackerControllerのテストクラス
 */
@WebMvcTest(TrackerController.class)
class TrackerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private SubjectRepository subjectRepository;
    
    @MockBean
    private TaskRepository taskRepository;
    
    @Test
    void testIndex() throws Exception {
        Subject subject1 = new Subject(1L, "数学");
        Subject subject2 = new Subject(2L, "英語");
        List<SubjectSummary> summaries = Arrays.asList(
            new SubjectSummary(subject1, 10, 7),
            new SubjectSummary(subject2, 5, 3)
        );
        
        when(subjectRepository.findAllWithTaskStats()).thenReturn(summaries);
        
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("subjects"))
            .andExpect(model().attribute("subjects", hasSize(2)))
            .andExpect(model().attribute("subjects", hasItem(
                hasProperty("subject", hasProperty("name", is("数学")))
            )))
            .andExpect(model().attribute("subjects", hasItem(
                hasProperty("subject", hasProperty("name", is("英語")))
            )));
        
        verify(subjectRepository, times(1)).findAllWithTaskStats();
    }
    
    @Test
    void testCreateSubject() throws Exception {
        mockMvc.perform(post("/subjects")
                .param("name", "数学"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
        
        verify(subjectRepository, times(1)).insert("数学");
    }
    
    @Test
    void testDeleteSubject() throws Exception {
        mockMvc.perform(post("/subjects/1/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
        
        verify(subjectRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testSubjectDetails() throws Exception {
        Subject subject = new Subject(1L, "数学");
        List<Task> tasks = Arrays.asList(
            new Task(1L, 1L, "問題集1-10ページ", true),
            new Task(2L, 1L, "問題集11-20ページ", false),
            new Task(3L, 1L, "テスト勉強", false)
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(taskRepository.findBySubjectId(1L)).thenReturn(tasks);
        
        mockMvc.perform(get("/subjects/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("subject_details"))
            .andExpect(model().attributeExists("subject"))
            .andExpect(model().attributeExists("tasks"))
            .andExpect(model().attribute("subject", hasProperty("name", is("数学"))))
            .andExpect(model().attribute("tasks", hasSize(3)))
            .andExpect(model().attribute("totalTasks", is(3L)))
            .andExpect(model().attribute("completedTasks", is(1L)))
            .andExpect(model().attribute("incompleteTasks", is(2L)));
        
        verify(subjectRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).findBySubjectId(1L);
    }
    
    @Test
    void testSubjectDetails_NoTasks() throws Exception {
        Subject subject = new Subject(1L, "数学");
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(taskRepository.findBySubjectId(1L)).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/subjects/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("subject_details"))
            .andExpect(model().attribute("totalTasks", is(0L)))
            .andExpect(model().attribute("completedTasks", is(0L)))
            .andExpect(model().attribute("incompleteTasks", is(0L)));
        
        verify(subjectRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).findBySubjectId(1L);
    }
    
    @Test
    void testSubjectDetails_NotFound() throws Exception {
        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/subjects/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
        
        verify(subjectRepository, times(1)).findById(999L);
        verify(taskRepository, never()).findBySubjectId(anyLong());
    }
    
    @Test
    void testCreateTask() throws Exception {
        mockMvc.perform(post("/subjects/1/tasks")
                .param("title", "問題集1-10ページ")
                .param("status", "進行中")      
                .param("deadline", "2026-02-27") 
                .param("reflection", "頑張る"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/1"));
        
        verify(taskRepository, times(1)).insert(eq(1L), eq( "問題集1-10ページ"), eq("進行中"), eq(LocalDate.parse("2026-02-27")), eq("頑張る"));
    }
    
    @Test
    void testCompleteTask() throws Exception {
        mockMvc.perform(post("/tasks/1/complete")
                .param("subjectId", "2")
                .param("completed", "true"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/2"));
        
        verify(taskRepository, times(1)).updateCompleted(1L, true);
    }
    
    @Test
    void testCompleteTask_Uncomplete() throws Exception {
        mockMvc.perform(post("/tasks/1/complete")
                .param("subjectId", "2")
                .param("completed", "false"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/2"));
        
        verify(taskRepository, times(1)).updateCompleted(1L, false);
    }
    
    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(post("/tasks/1/delete")
                .param("subjectId", "2"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/2"));
        
        verify(taskRepository, times(1)).deleteById(1L);
    }
}
