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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FacultyControllerMvcTest {

    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyService;

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyController facultyController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(facultyController).build();
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() throws Exception {
        // Given
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(faculty);

        // When & Then
        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void getFaculty_shouldReturnFaculty() throws Exception {
        // Given
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyService.findFaculty(1L)).thenReturn(Optional.of(faculty));

        // When & Then
        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }


    @Test
    void getAllFaculties_shouldReturnListOfFaculties() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Gryffindor", "Red"),
                new Faculty(2L, "Slytherin", "Green")
        );
        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(get("/faculty/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[1].name").value("Slytherin"));
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Scarlet");
        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Scarlet"));
    }

    @Test
    void deleteFaculty_shouldReturnOk() throws Exception {
        // Given
        long facultyId = 1L;
        Faculty faculty = new Faculty(facultyId, "Gryffindor", "Red");
        when(facultyService.findFaculty(facultyId)).thenReturn(Optional.of(faculty));
        doNothing().when(facultyService).deleteFaculty(facultyId);
        // When/Then
        mockMvc.perform(delete("/faculty/" + facultyId))
                .andExpect(status().isOk());
    }

    @Test
    void findByNameOrColor_shouldReturnMatchingFaculties() throws Exception {
        // Given
        List<Faculty> faculties = List.of(
                new Faculty(1L, "Gryffindor", "Red")
        );

        // Mock the repository call that the controller is actually making
        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase("Gryffindor", null))
                .thenReturn(faculties);

        // When/Then
        mockMvc.perform(get("/faculty/findby?name=Gryffindor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Red"));
    }
}
