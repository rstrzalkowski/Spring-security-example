package pl.lodz.p.it.usermodule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.usermodule.dto.request.LoginDTO;
import pl.lodz.p.it.usermodule.dto.request.RegisterUserDTO;
import pl.lodz.p.it.usermodule.dto.response.JwtDTO;
import pl.lodz.p.it.usermodule.model.User;
import pl.lodz.p.it.usermodule.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping("/register")
    public User register(@RequestBody RegisterUserDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public JwtDTO login(@RequestBody LoginDTO dto) {
        return userService.login(dto);
    }

}
