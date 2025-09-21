package org.example.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanager.model.Task;
import org.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    TaskService taskService;

    @Test
    void createTask_returnsCreated() throws Exception {
        Task t = new Task();
        t.setId(1L);
        t.setTitle("Test");
        t.setDeadline(LocalDate.now().plusDays(3));
        when(taskService.createTask(any(Task.class), any(String.class))).thenReturn(t);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User", "tester")
                        .content(mapper.writeValueAsString(t)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value(1))
                .andExpect((ResultMatcher) jsonPath("$.title").value("Test"));
    }
}