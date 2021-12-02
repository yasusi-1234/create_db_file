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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DummyUserLastName that = (DummyUserLastName) o;
        return userLastNameId != null && Objects.equals(userLastNameId, that.userLastNameId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
