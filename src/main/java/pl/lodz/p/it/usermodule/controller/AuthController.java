package pl.lodz.p.it.usermodule.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.usermodule.dto.request.LoginDTO;
import pl.lodz.p.it.usermodule.dto.request.RegisterUserDTO;
import pl.lodz.p.it.usermodule.dto.response.SuccessfulLoginDTO;
import pl.lodz.p.it.usermodule.model.user.User;
import pl.lodz.p.it.usermodule.service.AuthService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterUserDTO dto) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    public SuccessfulLoginDTO login(@Valid @RequestBody LoginDTO dto) {
        return authService.login(dto);
    }

    @GetMapping("/register/confirm")
    public String confirmRegistration(@Param("token") String token) {
        authService.confirmRegistration(token);
        return "Confirmed!";
    }

}
