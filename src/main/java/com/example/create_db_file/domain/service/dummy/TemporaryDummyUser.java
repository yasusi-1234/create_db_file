package com.example.create_db_file.domain.service.dummy;

import lombok.Data;

@Data
public class TemporaryDummyUser {
    private String firstName;
    private String lastName;

    private String firstNameKana;
    private String lastNameKana;

    private String firstNameRoman;
    private String lastNameRoman;

    public static TemporaryDummyUser of(String firstName, String lastName,
                               String firstNameKana, String lastNameKana,
                               String firstNameRoman, String lastNameRoman){
        TemporaryDummyUser dummyUser = new TemporaryDummyUser();
        dummyUser.setFirstName(firstName);
        dummyUser.setLastName(lastName);
        dummyUser.setFirstNameKana(firstNameKana);
        dummyUser.setLastNameKana(lastNameKana);
        dummyUser.setFirstNameRoman(firstNameRoman);
        dummyUser.setLastNameRoman(lastNameRoman);
        return dummyUser;
    }
}
