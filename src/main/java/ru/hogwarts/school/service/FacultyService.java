package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.*;

@Service
public class FacultyService {

    private final HashMap<Long, Faculty> facultyMap = new HashMap<>();
    private long COUNTER = 0;

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(++COUNTER);
        facultyMap.put(COUNTER, faculty);
        return faculty;
    }

    public Faculty findFaculty(long id) {
        return facultyMap.get(id);
    }

    public Map<Long, List<Faculty>> getAllFaculties() {
        Map<Long, List<Faculty>> result = new HashMap<>();
        for (Map.Entry<Long, Faculty> entry : facultyMap.entrySet()) {
            result.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }
        return result;
    }

    public Collection<Faculty> findByColor(String color) {
        ArrayList<Faculty> result = new ArrayList<>();
        for (Faculty faculty : facultyMap.values()) {
            if (Objects.equals(faculty.getColor(), color)) {
                result.add(faculty);
            }
        }
        return result;
    }

    public Faculty editFaculty(Faculty faculty) {
        if (!facultyMap.containsKey(faculty.getId())) {
            return null;
        }
        facultyMap.put(faculty.getId(), faculty);
        return faculty;
    }

    public void deleteFaculty(long id) {
        facultyMap.remove(id);
    }
}
