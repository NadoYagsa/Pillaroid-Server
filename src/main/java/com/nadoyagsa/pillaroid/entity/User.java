package com.nadoyagsa.pillaroid.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(name = "user_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;

    @JsonIgnore
    @Column(nullable = false)
    private String email;

    @Override
    public String toString() {
        return "User{" +
                "userIdx=" + userIdx +
                ", email='" + email + '\'' +
                '}';
    }
}
