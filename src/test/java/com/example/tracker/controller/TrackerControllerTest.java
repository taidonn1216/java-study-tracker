package com.example.tracker.controller;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import com.example.tracker.model.Task;
import com.example.tracker.model.TaskStatus;
import com.example.tracker.service.TrackerService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * TrackerControllerのテストクラス
 */
@WebMvcTest(TrackerController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrackerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private TrackerService trackerService;
    
    @Test
    @WithMockUser(username = "testuser")
    void testIndex() throws Exception {
        Subject subject1 = new Subject(1L, "数学");
        Subject subject2 = new Subject(2L, "英語");
        List<SubjectSummary> summaries = Arrays.asList(
            new SubjectSummary(subject1, 10, 7),
            new SubjectSummary(subject2, 5, 3)
        );
        
        when(trackerService.getSubjectSummariesForCurrentUser("testuser")).thenReturn(summaries);
        when(trackerService.getOverdueTasksForCurrentUser(eq("tastuser"), any())).thenReturn(Collections.emptyList());
        
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
        
        verify(trackerService, times(1)).getSubjectSummariesForCurrentUser("testuser");
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCreateSubject() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        
        mockMvc.perform(post("/subjects")
                .param("name", "数学"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
        
        verify(trackerService, times(1)).createSubjectForCurrentUser("数学", 1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testDeleteSubject() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        
        mockMvc.perform(post("/subjects/1/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
        
       verify(trackerService, times(1)).deleteSubjectForCurrentUser(1L, 1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSubjectDetails() throws Exception {
        Subject subject = new Subject(1L, "数学");
        List<Task> tasks = Arrays.asList(
            new Task(1L, 1L, "問題集1-10ページ", true,  TaskStatus.DONE,LocalDate.parse("2026-02-20"), "記入してください"),
            new Task(2L, 1L, "問題集11-20ページ", false, TaskStatus.IN_PROGRESS,LocalDate.parse("2026-02-20"), "記入してください"),
            new Task(3L, 1L, "テスト勉強", false, TaskStatus.NOT_STARTED,LocalDate.parse("2026-02-20"), "記入してください")
        );
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        when(trackerService.getSubjectForCurrentUser(1L, 1L)).thenReturn(subject);
        when(trackerService.getTasksForSubject(1l, 1L)).thenReturn(tasks);
        
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
        
        verify(trackerService, times(1)).getSubjectForCurrentUser(1L, 1L);
        verify(trackerService, times(1)).getTasksForSubject(1L, 1L);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSubjectDetails_NoTasks() throws Exception {
        Subject subject = new Subject(1L, "数学");
        
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        when(trackerService.getSubjectForCurrentUser(1L, 1L)).thenReturn(subject);
        when(trackerService.getTasksForSubject(1L, 1L)).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/subjects/1"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("totalTasks", is(0L)))
            .andExpect(model().attribute("completedTasks", is(0L)))
            .andExpect(model().attribute("incompleteTasks", is(0L)));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSubjectDetails_NotFound() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        when(trackerService.getSubjectForCurrentUser(999L, 1L))
            .thenThrow(new RuntimeException("Subject not found or forbidden"));
        
        mockMvc.perform(get("/subjects/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSubjectDetails_OtherUserSubject() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        when(trackerService.getSubjectForCurrentUser(99L, 1L))
            .thenThrow(new RuntimeException("Subject not found or forbidden"));

        mockMvc.perform(get("/subjects/99"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));    
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCreateTask() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        
        mockMvc.perform(post("/subjects/1/tasks")
                .param("title", "問題集1-10ページ")
                .param("status", "進行中")      
                .param("deadline", "2026-02-27") 
                .param("reflection", "頑張る"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/1"));
        
        verify(trackerService, times(1)).createTask(1L, 1L,"問題集1-10ページ", TaskStatus.IN_PROGRESS, LocalDate.parse("2026-02-27"), "頑張る");
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testUpdateTaskStatus_ToDone() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        
        mockMvc.perform(post("/tasks/1/status")
                .param("subjectId", "2")
                .param("status", "完了"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/2"));
        
        verify(trackerService, times(1)).updateTaskStatusForCurrentUser(1L, 2L, 1L, TaskStatus.DONE);   
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateTaskStatus_ToDoing() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        
        mockMvc.perform(post("/tasks/1/status")
                .param("subjectId", "2")
                .param("status", "進行中"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/2"));
        
        verify(trackerService, times(1)).updateTaskStatusForCurrentUser(1L, 2L, 1L, TaskStatus.IN_PROGRESS);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDeleteTask() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        
        mockMvc.perform(post("/tasks/1/delete")
                .param("subjectId", "2"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/2"));
        
        verify(trackerService, times(1)).deleteTaskForCurrentUser(1L, 1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateTask_DeadlineBlank() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        
        mockMvc.perform(post("/subjects/1/tasks")
            .param("title", "問題集")
            .param("status", "未着手")
            .param("deadline", "")
            .param("reflection", ""))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/subjects/1"))
        .andExpect(flash().attribute("errorMessage", "期限日を入力してください。"));
        
        verify(trackerService,never()).createTask(anyLong(), anyLong(), anyString(), any(TaskStatus.class), any(), anyString());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateTask_DeadlineInvalidFormat() throws Exception {
        when(trackerService.currentUserId("testuser")).thenReturn(1L);
        
        mockMvc.perform(post("/subjects/1/tasks")
                .param("title", "問題集")
                .param("status", "未着手" )
                .param("deadline", "2026/02/30")
                .param("reflection", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/subjects/1"))
            .andExpect(flash().attribute("errorMessage", "期限日の形式が不正です。"));

        verify(trackerService, never()).createTask(anyLong(), anyLong(), anyString(), any(TaskStatus.class), any(), anyString());    
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateTask_OtherUsersSubject() throws Exception {
        when(trackerService.currentUserId("testuser")). thenReturn(1L);
        when(trackerService.getSubjectForCurrentUser(99L, 1L))
            .thenThrow(new RuntimeException("Subject not found or forbidden"));

        mockMvc.perform(post("/subjects/99/tasks")
                .param("title", "不正タスク")
                .param("status", "未着手")
                .param("deadline", "2026-03-01")
                .param("reflection", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
            
        verify(trackerService, never()).createTask(anyLong(), anyLong(), anyString(), any(), any(), anyString());
    }
}
