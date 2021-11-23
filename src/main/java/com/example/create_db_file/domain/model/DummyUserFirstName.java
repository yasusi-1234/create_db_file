package com.example.create_db_file.domain.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class DummyUserFirstName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userFirstNameId;

    private String firstName;

    private String firstNameKana;

    private String firstNameRoman;


    public static DummyUserFirstName of(String firstName,
                               String firstNameKana,
                               String firstNameRoman){
        DummyUserFirstName dummyUserFirstName = new DummyUserFirstName();
        dummyUserFirstName.setFirstName(firstName);
        dummyUserFirstName.setFirstNameKana(firstNameKana);
        dummyUserFirstName.setFirstNameRoman(firstNameRoman);
        return dummyUserFirstName;
    }
}
