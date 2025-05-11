package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAge(Integer age);

    Optional<Student> findById(Long id);

    Collection<Student> findByAgeBetweenOrderById(Integer min, Integer max);
}
