package ru.hogwarts.school;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerRestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() {
        // Arrange
        Student student = new Student();
        student.setName("Test Student");
        student.setAge(20);

        // Act
        ResponseEntity<Student> response = restTemplate.postForEntity(
                baseUrl, student, Student.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Student", response.getBody().getName());
        assertEquals(20, response.getBody().getAge());
    }

    @Test
    void getStudent_shouldReturnStudentWhenExists() {
        // Arrange
        Student student = restTemplate.postForObject(
                baseUrl,
                new Student(1L, "Test Student", 20),
                Student.class);

        // Act
        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + student.getId(), Student.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Student", response.getBody().getName());
    }

    @Test
    void getAllStudents_shouldReturnListOfStudents() {
        // Arrange
        restTemplate.postForObject(baseUrl, new Student(1L, "Student 1", 20), Student.class);
        restTemplate.postForObject(baseUrl, new Student(2L, "Student 2", 21), Student.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/all", List.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    void findStudentsByAge_shouldReturnStudentsWithGivenAge() {
        // Arrange
        restTemplate.postForObject(baseUrl, new Student(1L, "Student 1", 22), Student.class);
        restTemplate.postForObject(baseUrl, new Student(2L, "Student 2", 22), Student.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "?age=22", List.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    void findStudentsByAgeBetween_shouldReturnStudentsInAgeRange() {
        // Arrange
        restTemplate.postForObject(baseUrl, new Student(1L, "Student 1", 20), Student.class);
        restTemplate.postForObject(baseUrl, new Student(2L, "Student 2", 25), Student.class);
        restTemplate.postForObject(baseUrl, new Student(3L, "Student 3", 30), Student.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/agebetween?min=20&max=25", List.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    void updateStudent_shouldUpdateStudent() {
        // Arrange
        Student student = restTemplate.postForObject(
                baseUrl,
                new Student(1L, "Original Name", 20),
                Student.class);

        student.setName("Updated Name");
        student.setAge(21);

        // Act
        restTemplate.put(baseUrl, student);

        // Assert
        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + student.getId(), Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals(21, response.getBody().getAge());
    }

    @Test
    void deleteStudent_shouldRemoveStudent() {
        // Arrange
        Student student = restTemplate.postForObject(
                baseUrl,
                new Student(1L, "Student to delete", 20),
                Student.class);

        // Act
        restTemplate.delete(baseUrl + "/" + student.getId());

        // Assert
        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + student.getId(), Student.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}