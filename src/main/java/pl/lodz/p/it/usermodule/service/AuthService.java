package pl.lodz.p.it.usermodule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.usermodule.dto.LoginDTO;
import pl.lodz.p.it.usermodule.dto.RegisterUserDTO;
import pl.lodz.p.it.usermodule.model.User;
import pl.lodz.p.it.usermodule.repository.UserRepository;

import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User register(RegisterUserDTO dto) {
        User user = new User(dto.getEmail(), dto.getPassword());
        return userRepository.save(user);
    }

    public String login(LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());

        if (Objects.equals(dto.getPassword(), user.getPassword())) {
            return "JWT";
        }
        throw new RuntimeException();
    }

}
