package wisoft.authservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;

    @JsonProperty("user_name")
    private String username;

    private String password;
}
