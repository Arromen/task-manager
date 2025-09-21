package org.example.taskmanager.service;

import org.example.taskmanager.model.Task;
import org.example.taskmanager.repository.TaskHistoryRepository;
import org.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    @Mock
    TaskRepository taskRepository;
    @Mock
    TaskHistoryRepository historyRepository;
    @InjectMocks
    TaskService taskService;

    public TaskServiceTest() { MockitoAnnotations.openMocks(this); }

    @Test
    void createTask_recordsHistory() {
        Task t = new Task();
        t.setTitle("T");
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> { Task p = i.getArgument(0); p.setId(1L); return p; });

        Task saved = taskService.createTask(t, "user1");
        verify(historyRepository, times(1)).save(any());
    }
}