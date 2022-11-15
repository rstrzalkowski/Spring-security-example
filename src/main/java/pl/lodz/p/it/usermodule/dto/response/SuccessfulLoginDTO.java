package pl.lodz.p.it.usermodule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SuccessfulLoginDTO {
    private String jwt;
    private String refreshToken;
    private String email;
}
