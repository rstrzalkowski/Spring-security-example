package pl.lodz.p.it.usermodule.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {
    @NotNull
    String email;

    @NotNull
    String username;

    @NotNull
    String password;
}
