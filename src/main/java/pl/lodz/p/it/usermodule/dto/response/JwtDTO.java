package pl.lodz.p.it.usermodule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JwtDTO {
    private String token;
    private String email;
}
