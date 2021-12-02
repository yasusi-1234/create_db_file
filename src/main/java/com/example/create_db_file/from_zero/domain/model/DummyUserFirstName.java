package com.example.create_db_file.from_zero.domain.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DummyUserFirstName that = (DummyUserFirstName) o;
        return userFirstNameId != null && Objects.equals(userFirstNameId, that.userFirstNameId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
