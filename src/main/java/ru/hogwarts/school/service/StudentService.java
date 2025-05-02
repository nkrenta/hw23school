package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private final HashMap<Long, Student> studentMap = new HashMap<>();
    private Long COUNTER = 0L;

    public Student addStudent(Student student){
        student.setId(++COUNTER);
        studentMap.put(COUNTER, student);
        return student;
    }

    public Student findStudent(Long id){
        return studentMap.get(id);
    }

    public Map<Long, List<Student>> getAllStudents(){
        Map<Long, List<Student>> result = new HashMap<>();
        for (Map.Entry<Long, Student> entry : studentMap.entrySet()) {
            result.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }
        return result;
    }

    public Student editStudent(Student student) {
        if (!studentMap.containsKey(student.getId())) {
            return null;
        }
        studentMap.put(student.getId(), student);
        return student;
    }

    public Student deleteStudent(Long id) {
        return studentMap.remove(id);
    }

}
