package com.nadoyagsa.pillaroid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("alarm_token")
    private String alarmToken;
}
