package com.nadoyagsa.pillaroid.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class User {
    @Id
    @Column(name = "user_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;

    @JsonIgnore
    @Column(name = "kakao_account_id", nullable = false)
    private Long kakaoAccountId;

    @JsonIgnore
    @Setter
    @Column(name = "alarm_token")
    private String alarmToken;
}
