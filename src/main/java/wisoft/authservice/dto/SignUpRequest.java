package wisoft.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @JsonProperty("user_name")
    private String username;

    private String password;
}