package ru.hogwarts.school;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerRestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() {
        // Arrange
        Faculty faculty = new Faculty();
        faculty.setName("Test Faculty");
        faculty.setColor("Blue");

        // Act
        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                baseUrl, faculty, Faculty.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        //??assertNotNull(response.getBody().getId());
        assertEquals("Test Faculty", response.getBody().getName());
        assertEquals("Blue", response.getBody().getColor());
    }

    @Test
    void getFaculty_shouldReturnFacultyWhenExists() {
        // Arrange
        Faculty faculty = restTemplate.postForObject(
                baseUrl,
                new Faculty(1L, "Faculty 1", "Red"),
                Faculty.class);

        // Act
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + faculty.getId(), Faculty.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Faculty 1", response.getBody().getName());
        assertEquals("Red", response.getBody().getColor());
    }

    @Test
    void getAllFaculties_shouldReturnListOfFaculties() {
        // Arrange
        restTemplate.postForObject(baseUrl, new Faculty(1L, "Faculty 1", "Red"), Faculty.class);
        restTemplate.postForObject(baseUrl, new Faculty(2L, "Faculty 2", "Blue"), Faculty.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/all", List.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    void findByNameOrColorIgnoreCase_shouldReturnMatchingFaculties() {
        // Arrange
        restTemplate.postForObject(baseUrl, new Faculty(1L, "Gryffindor", "Scarlet"), Faculty.class);
        restTemplate.postForObject(baseUrl, new Faculty(2L, "Slytherin", "Green"), Faculty.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/findby?name=gryffindor", List.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateFaculty_shouldUpdateFaculty() {
        // Arrange
        Faculty faculty = restTemplate.postForObject(
                baseUrl,
                new Faculty(1L, "Original Name", "Red"),
                Faculty.class);

        faculty.setName("Updated Name");
        faculty.setColor("Blue");

        // Act
        restTemplate.put(baseUrl, faculty);

        // Assert
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + faculty.getId(), Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("Blue", response.getBody().getColor());
    }

    @Test
    void deleteFaculty_shouldRemoveFaculty() {
        // Arrange
        Faculty faculty = restTemplate.postForObject(
                baseUrl,
                new Faculty(1L, "Faculty to delete", "Red"),
                Faculty.class);

        // Act
        restTemplate.delete(baseUrl + "/" + faculty.getId());

        // Assert
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + faculty.getId(), Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getFacultyStudents_shouldReturnListOfStudents() {
        // Arrange
        Faculty faculty = restTemplate.postForObject(
                baseUrl,
                new Faculty(1L, "Test Faculty", "Blue"),
                Faculty.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/faculties/" + faculty.getId() + "/students",
                List.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
}
