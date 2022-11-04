package pl.lodz.p.it.usermodule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.usermodule.dto.LoginDTO;
import pl.lodz.p.it.usermodule.dto.RegisterUserDTO;
import pl.lodz.p.it.usermodule.model.User;
import pl.lodz.p.it.usermodule.service.AuthService;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterUserDTO dto) {
        return authService.register(dto);
    }

    @PostMapping("/auth")
    public String register(LoginDTO dto) {
        return authService.login(dto);
    }

}
