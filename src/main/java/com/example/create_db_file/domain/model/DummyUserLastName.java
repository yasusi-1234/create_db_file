package com.example.create_db_file.domain.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class DummyUserLastName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userLastNameId;

    private String lastName;

    private String lastNameKana;

    private String lastNameRoman;

    public static DummyUserLastName of(
            String lastName,
            String lastNameKana,
            String lastNameRoman) {
        DummyUserLastName dummyUserLastName = new DummyUserLastName();
        dummyUserLastName.setLastName(lastName);
        dummyUserLastName.setLastNameKana(lastNameKana);
        dummyUserLastName.setLastNameRoman(lastNameRoman);
        return dummyUserLastName;
    }
}
