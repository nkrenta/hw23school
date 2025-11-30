package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StudentControllerMvcTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentController studentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() throws Exception {
        // Given
        Student student = new Student(1L, "Harry Potter", 17);
        when(studentService.addStudent(any(Student.class))).thenReturn(student);

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"));
    }

    @Test
    void getStudent_shouldReturnStudent() throws Exception {
        // Given
        Student student = new Student(1L, "Harry Potter", 17);
        when(studentService.findStudent(1L)).thenReturn(student);

        // When & Then
        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"));
    }

    @Test
    void getAllStudents_shouldReturnListOfStudents() throws Exception {
        // Given
        List<Student> students = Arrays.asList(
                new Student(1L, "Harry Potter", 17),
                new Student(2L, "Hermione Granger", 17)
        );
        when(studentService.getAllStudents()).thenReturn(students);

        // When & Then
        mockMvc.perform(get("/student/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[1].name").value("Hermione Granger"));
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {
        // Given
        Student student = new Student(1L, "Harry Potter", 18);
        when(studentService.editStudent(any(Student.class))).thenReturn(student);

        // When & Then
        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void deleteStudent_shouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
void findByAge_shouldReturnStudentsByAge() throws Exception {
    // Given
    List<Student> students = Arrays.asList(
            new Student(1L, "Harry Potter", 17),
            new Student(2L, "Ron Weasley", 17)
    );
    when(studentRepository.findByAge(17)).thenReturn(students);

    // When & Then
    mockMvc.perform(get("/student").param("age", "17"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].age").value(17))
            .andExpect(jsonPath("$[1].age").value(17))
            .andExpect(jsonPath("$[0].name").value("Harry Potter"))
            .andExpect(jsonPath("$[1].name").value("Ron Weasley"));
}
}