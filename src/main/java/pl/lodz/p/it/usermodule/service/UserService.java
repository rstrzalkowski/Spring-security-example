package pl.lodz.p.it.usermodule.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.usermodule.dto.request.LoginDTO;
import pl.lodz.p.it.usermodule.dto.request.RegisterUserDTO;
import pl.lodz.p.it.usermodule.dto.response.JwtDTO;
import pl.lodz.p.it.usermodule.exception.EmailAlreadyRegisteredException;
import pl.lodz.p.it.usermodule.model.User;
import pl.lodz.p.it.usermodule.repository.UserRepository;
import pl.lodz.p.it.usermodule.security.JwtProvider;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder encoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;

        User admin = new User("admin", encoder.encode("admin"), User.ADMIN);
        userRepository.save(admin);
    }

    public User register(RegisterUserDTO dto) {
        String hashedPassword = encoder.encode(dto.getPassword());
        User user = new User(dto.getEmail(), hashedPassword);
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new EmailAlreadyRegisteredException();
        }

    }

    public JwtDTO login(LoginDTO dto) {
        //this throws some exception and returns 403 code if authentication fails
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        String jwt = jwtProvider.generateJWT(dto.getEmail());
        return new JwtDTO(jwt, dto.getEmail());
    }

}
