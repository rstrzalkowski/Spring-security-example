package pl.lodz.p.it.usermodule.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.usermodule.dto.request.LoginDTO;
import pl.lodz.p.it.usermodule.dto.request.RegisterUserDTO;
import pl.lodz.p.it.usermodule.dto.response.JwtDTO;
import pl.lodz.p.it.usermodule.model.VerificationToken;
import pl.lodz.p.it.usermodule.service.AuthService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public VerificationToken register(@Valid @RequestBody RegisterUserDTO dto) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    public JwtDTO login(@Valid @RequestBody LoginDTO dto) {
        return authService.login(dto);
    }

}
