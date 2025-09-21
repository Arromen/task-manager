package org.example.taskmanager.repository;

import org.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Task.Status status);
    List<Task> findByPriority(Task.Priority priority);
    List<Task> findByStatusAndPriority(Task.Status status, Task.Priority priority);
    List<Task> findByAssignee(String assignee);
}
