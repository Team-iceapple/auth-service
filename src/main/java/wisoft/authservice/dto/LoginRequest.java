package wisoft.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @JsonProperty("user_name")
    private String username;
    private String password;
}

