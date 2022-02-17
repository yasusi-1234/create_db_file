package com.example.create_db_file.controller;


import com.example.create_db_file.from_zero.domain.model.DummyUserFirstName;
import com.example.create_db_file.from_zero.domain.repository.DummyUserFirstNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SampleRestController {

    private final DummyUserFirstNameRepository repository;

    @Autowired
    public SampleRestController(DummyUserFirstNameRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/first1/{id}")
    public ResponseEntity<Object> getFirstName1(@PathVariable("id") Long id) {
        return ResponseEntity.ok(repository.findByUserFirstNameId(id));
    }

    @GetMapping("/first2/{id}")
    public ResponseEntity<Object> getFirstName2(@PathVariable("id") Long id) {
        return ResponseEntity.ok(repository.findByUserFirstNameId2(id));
    }

    @GetMapping("/first3/{id}")
    public ResponseEntity<Object> getFirstName3(@PathVariable("id") Long id) {
        return ResponseEntity.ok(repository.findByUserFirstNameId3(id));
    }

    @GetMapping("/first4/{id}")
    public ResponseEntity<Object> getFirstName4(@PathVariable("id") Long id) {
        DummyUserFirstName dummyUserFirstName =
                repository.findOne(Specification.where(
                        (root, query, cb) -> id == null ? null:
                                cb.equal(root.<String>get("userFirstNameId"), id)
                )).orElse(new DummyUserFirstName());

        return ResponseEntity.ok(dummyUserFirstName);
    }


}
