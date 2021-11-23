package com.example.create_db_file.domain.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class DummyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String firstName;
    private String lastName;

    private String firstNameKana;
    private String lastNameKana;

    private String firstNameRoman;
    private String lastNameRoman;

    private String mailAddress;

    public static DummyUser of(String firstName, String lastName,
                        String firstNameKana, String lastNameKana,
                        String firstNameRoman, String lastNameRoman,
                        String mailAddress){
        DummyUser dummyUser = new DummyUser();
        dummyUser.setFirstName(firstName);
        dummyUser.setLastName(lastName);
        dummyUser.setFirstNameKana(firstNameKana);
        dummyUser.setLastNameKana(lastNameKana);
        dummyUser.setFirstNameRoman(firstNameRoman);
        dummyUser.setLastNameRoman(lastNameRoman);
        dummyUser.setMailAddress(mailAddress);
        return dummyUser;
    }

}
