package org.example.taskmanager.repository;

import org.example.taskmanager.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired TaskRepository repo;

    @Test
    void findByStatus_returnsTasks() {
        Task t = new Task();
        t.setTitle("A"); t.setStatus(Task.Status.DONE);
        em.persistAndFlush(t);

        List<Task> done = repo.findByStatus(Task.Status.DONE);
        assertFalse(done.isEmpty());
    }
}