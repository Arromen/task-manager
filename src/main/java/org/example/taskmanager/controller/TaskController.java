package org.example.taskmanager.controller;

import org.example.taskmanager.dto.ReportFilters;
import org.example.taskmanager.model.Task;
import org.example.taskmanager.model.TaskHistory;
import org.example.taskmanager.service.TaskService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // Create
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task,
                                           @RequestHeader(value = "X-User", defaultValue = "unknown") String user) {
        Task created = service.createTask(task, user);
        return ResponseEntity.ok(created);
    }

    // Read all
    @GetMapping
    public List<Task> listTasks(@RequestParam(value = "status", required = false) Task.Status status,
                                @RequestParam(value = "priority", required = false) Task.Priority priority,
                                @RequestParam(value = "assignee", required = false) String assignee) {
        if (status != null && priority != null) {
            return service.listByStatus(status).stream().filter(t -> t.getPriority() == priority).toList();
        } else if (status != null) {
            return service.listByStatus(status);
        } else if (priority != null) {
            return service.listByPriority(priority);
        } else if (assignee != null) {
            return service.listByPriority(null).stream().filter(t -> assignee.equals(t.getAssignee())).toList(); // fallback
        } else {
            return service.listAll();
        }
    }

    // Read one
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return service.getTask(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @RequestBody Task task,
                                           @RequestHeader(value = "X-User", defaultValue = "unknown") String user) {
        Task updated = service.updateTask(id, task, user);
        return ResponseEntity.ok(updated);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
                                           @RequestHeader(value = "X-User", defaultValue = "unknown") String user) {
        service.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }

    // History
    @GetMapping("/{id}/history")
    public List<TaskHistory> history(@PathVariable Long id) {
        return service.getHistory(id);
    }

    // Report: completed tasks (Excel)
    @PostMapping(value = "/report/completed", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> completedReport(@RequestBody ReportFilters filters) throws Exception {
        List<Task> done = service.listByStatus(Task.Status.DONE);
        byte[] excel = service.generateCompletedReport(done);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=completed-tasks.xlsx");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(excel);
    }
}
