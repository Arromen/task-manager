package org.example.taskmanager.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.taskmanager.model.Task;
import org.example.taskmanager.model.TaskHistory;
import org.example.taskmanager.repository.TaskHistoryRepository;
import org.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskHistoryRepository historyRepository;

    public TaskService(TaskRepository taskRepository, TaskHistoryRepository historyRepository) {
        this.taskRepository = taskRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public Task createTask(Task task, String user) {
        Task saved = taskRepository.save(task);
        recordHistory(saved.getId(), user, "Created task");
        return saved;
    }

    @Transactional
    public Task updateTask(Long id, Task incoming, String user) {
        Task t = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        StringBuilder changes = new StringBuilder();

        if (incoming.getTitle() != null && !incoming.getTitle().equals(t.getTitle())) {
            changes.append("title: ").append(t.getTitle()).append(" -> ").append(incoming.getTitle()).append("; ");
            t.setTitle(incoming.getTitle());
        }
        if (incoming.getDescription() != null && !incoming.getDescription().equals(t.getDescription())) {
            changes.append("description changed; ");
            t.setDescription(incoming.getDescription());
        }
        if (incoming.getStatus() != null && incoming.getStatus() != t.getStatus()) {
            changes.append("status: ").append(t.getStatus()).append(" -> ").append(incoming.getStatus()).append("; ");
            t.setStatus(incoming.getStatus());
        }
        if (incoming.getPriority() != null && incoming.getPriority() != t.getPriority()) {
            changes.append("priority: ").append(t.getPriority()).append(" -> ").append(incoming.getPriority()).append("; ");
            t.setPriority(incoming.getPriority());
        }
        if (incoming.getDeadline() != null && !incoming.getDeadline().equals(t.getDeadline())) {
            changes.append("deadline: ").append(t.getDeadline()).append(" -> ").append(incoming.getDeadline()).append("; ");
            t.setDeadline(incoming.getDeadline());
        }
        if (incoming.getAssignee() != null && !incoming.getAssignee().equals(t.getAssignee())) {
            changes.append("assignee: ").append(t.getAssignee()).append(" -> ").append(incoming.getAssignee()).append("; ");
            t.setAssignee(incoming.getAssignee());
        }

        Task saved = taskRepository.save(t);
        if (changes.length() == 0) changes.append("no changes");
        recordHistory(saved.getId(), user, changes.toString());
        return saved;
    }

    public Optional<Task> getTask(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> listAll() {
        return taskRepository.findAll();
    }

    public List<Task> listByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> listByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    public void deleteTask(Long id, String user) {
        taskRepository.deleteById(id);
        recordHistory(id, user, "Deleted task");
    }

    public List<TaskHistory> getHistory(Long taskId) {
        return historyRepository.findByTaskIdOrderByChangedAtDesc(taskId);
    }

    private void recordHistory(Long taskId, String user, String desc) {
        TaskHistory h = new TaskHistory(taskId, user, LocalDateTime.now(), desc);
        historyRepository.save(h);
    }

    // Generate Excel report for DONE tasks between provided bounds
    public byte[] generateCompletedReport(List<Task> tasks) throws Exception {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("CompletedTasks");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Title");
            header.createCell(2).setCellValue("Assignee");
            header.createCell(3).setCellValue("Deadline");
            header.createCell(4).setCellValue("Priority");
            header.createCell(5).setCellValue("Status");

            int row = 1;
            for (Task t : tasks) {
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(t.getId());
                r.createCell(1).setCellValue(t.getTitle());
                r.createCell(2).setCellValue(t.getAssignee() == null ? "" : t.getAssignee());
                r.createCell(3).setCellValue(t.getDeadline() == null ? "" : t.getDeadline().toString());
                r.createCell(4).setCellValue(t.getPriority().name());
                r.createCell(5).setCellValue(t.getStatus().name());
            }
            wb.write(out);
            return out.toByteArray();
        }
    }
}
