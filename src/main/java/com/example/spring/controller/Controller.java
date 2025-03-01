package com.example.spring.controller;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.example.spring.dto.StudentRequestDTO;
import com.example.spring.dto.StudentResponseDTO;
import com.example.spring.http.ResponseEntityFactory;
import com.example.spring.repository.StudentRepository;
import com.example.spring.service.StudentService;

import jakarta.persistence.Id;
import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class Controller {

    @Autowired
    private StudentService studentService;
    
    @SuppressWarnings("rawtypes")
    ResponseEntityFactory responseEntityFactory = new ResponseEntityFactory<>();
    
    @GetMapping("/healthCheck")
    public ResponseEntity<?> healthcheck() {
        return responseEntityFactory.create(HttpStatus.OK);

    }

    @SuppressWarnings("unchecked")
    @PostMapping(value = "/createStudent",
    produces = {"application/json"})
    public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody StudentRequestDTO studentRequestDTO) {        
        
        if (!studentService.isDataValid(studentRequestDTO)) {
            return responseEntityFactory.create(HttpStatus.BAD_REQUEST);
        }

        if (studentService.doesStudentExistbyPII(studentRequestDTO.getSsn())) {

            return responseEntityFactory.create(HttpStatus.METHOD_NOT_ALLOWED);
        }

        // Else proceed to create the student
        StudentResponseDTO studentResponseDTO = studentService.createStudent(studentRequestDTO);

        return responseEntityFactory.create(studentResponseDTO, HttpStatus.CREATED);
    }

    @SuppressWarnings("unchecked")
    @GetMapping(value = "/readStudent/{Id}",
    produces = ("application/json"))
    public ResponseEntity<StudentResponseDTO> readStudent(@PathVariable UUID Id) {

        StudentResponseDTO studentResponseDTO = studentService.getStudent(Id);

        if (studentResponseDTO == null) {
            return responseEntityFactory.create(HttpStatus.NOT_FOUND);
        }

        return responseEntityFactory.create(studentResponseDTO, HttpStatus.OK);
    
    }
    @SuppressWarnings("unchecked")
    @PutMapping(value = "/{Id}/updateStudent",
    produces = {"application/json"})
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable UUID Id, @RequestBody StudentRequestDTO studentRequestDTO) {        
                
        if (!studentService.isDataValid(studentRequestDTO)) {
            return responseEntityFactory.create(HttpStatus.BAD_REQUEST);
        }

        if (!studentService.doesStudentExist(Id)) {

            return responseEntityFactory.create(HttpStatus.NOT_FOUND); 
        }
        studentService.updateStudent(Id, studentRequestDTO);

        return responseEntityFactory.create(HttpStatus.NO_CONTENT);
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping(value = "/{Id}/deleteStudent",
    produces = {"application/json"})
    public ResponseEntity<StudentResponseDTO> deleteStudent(@PathVariable UUID Id) {        

        if (!studentService.doesStudentExist(Id)) {

            return responseEntityFactory.create(HttpStatus.NOT_FOUND); 
        }
        studentService.deleteStudent(Id);

        return responseEntityFactory.create(HttpStatus.NO_CONTENT);
    }
    
}
