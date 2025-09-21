package org.example.taskmanager;

import org.example.taskmanager.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + "/api/tasks" + path;
    }

    @Test
    public void createAndGetTask() {
        Task t = new Task();
        t.setTitle("Test task");
        t.setDescription("desc");
        t.setDeadline(LocalDate.now().plusDays(3));
        t.setPriority(Task.Priority.HIGH);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User", "tester");

        HttpEntity<Task> e = new HttpEntity<>(t, headers);
        ResponseEntity<Task> resp = rest.postForEntity(url(""), e, Task.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Task created = resp.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());

        ResponseEntity<Task> got = rest.getForEntity(url("/" + created.getId()), Task.class);
        assertEquals(HttpStatus.OK, got.getStatusCode());
        assertEquals("Test task", got.getBody().getTitle());
    }

    @Test
    public void updateAndHistory() {
        // create
        Task t = new Task();
        t.setTitle("For update");
        HttpEntity<Task> e = new HttpEntity<>(t, new HttpHeaders());
        Task created = rest.postForObject(url(""), e, Task.class);
        assertNotNull(created);

        // update status
        Task upd = new Task();
        upd.setStatus(Task.Status.DONE);
        HttpEntity<Task> ue = new HttpEntity<>(upd, new HttpHeaders());
        rest.exchange(url("/" + created.getId()), HttpMethod.PUT, ue, Task.class);

        ResponseEntity<Task[]> arr = rest.getForEntity(url(""), Task[].class);
        assertTrue(arr.getBody().length >= 1);
        // history
        ResponseEntity<Object> history = rest.getForEntity(url("/" + created.getId() + "/history"), Object.class);
        assertEquals(HttpStatus.OK, history.getStatusCode());
    }
}
