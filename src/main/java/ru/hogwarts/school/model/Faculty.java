package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @OneToMany(mappedBy = "faculty")
    private List<Student> students;

    private String name;

    private String color;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<String> getFacultyStudents() {
        List<Student> studentList = students;
        return studentList.stream()
                .map(student -> "student's id: " + student.getId() + "; student's name: " + student.getName() + "; student's age: " + student.getAge() + "; student's faculty: " + student.getFaculty().getName() + " ; color: " + student.getFaculty().getColor())
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "{" + "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", color='" + color + '\'' +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return id == faculty.id && Objects.equals(students, faculty.students) && Objects.equals(name, faculty.name) && Objects.equals(color, faculty.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, students, name, color);
    }
}
