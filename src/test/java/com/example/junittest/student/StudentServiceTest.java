package com.example.junittest.student;

import com.example.junittest.student.exception.BadRequestException;
import com.example.junittest.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

// Run With Coverage to see which part is tested or not
@ExtendWith(MockitoExtension.class) // Initialized all the mocks
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    /*
     * Before Each Test
     * */
    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        underTest.getAllStudents();

        // then
        verify(studentRepository).findAll();
    }

    @Test
//    @Disabled
    void canAddStudent() {
        Student student = new Student(
                "karina", "karina@gmail.com", Gender.FEMALE
        );

        underTest.addStudent(student);

        // capture the student argument that has been passed
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentArgumentCaptor.capture());
        Student capturedStudent = studentArgumentCaptor.getValue();

        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
//    @Disabled
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = new Student(
                "karina", "karina@gmail.com", Gender.FEMALE
        );

        // make selectExistsEmail method return true
        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);

        assertThatThrownBy(() -> underTest.addStudent(student))
                // check Exception class and message
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        // never save any student
        verify(studentRepository, never()).save(any());
    }

    @Test
//    @Disabled
    void canDeleteStudent() {
        Long id = 3L;

        given(studentRepository.existsById(anyLong()))
                .willReturn(true);

        underTest.deleteStudent(id);

        ArgumentCaptor<Long> idCapture =
                ArgumentCaptor.forClass(Long.class);
        verify(studentRepository).deleteById(idCapture.capture());
        Long captureValue = idCapture.getValue();

        assertThat(captureValue).isEqualTo(id);
    }

    @Test
//    @Disabled
    void willThrownWhenIdDoesNotExists(){

        long studentId = 3;

        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");

        verify(studentRepository, never()).deleteById(studentId);
    }
}