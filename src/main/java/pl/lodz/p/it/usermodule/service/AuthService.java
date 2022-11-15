package pl.lodz.p.it.usermodule.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.usermodule.dto.request.LoginDTO;
import pl.lodz.p.it.usermodule.dto.request.RegisterUserDTO;
import pl.lodz.p.it.usermodule.dto.response.SuccessfulLoginDTO;
import pl.lodz.p.it.usermodule.exception.EmailAlreadyRegisteredException;
import pl.lodz.p.it.usermodule.model.token.TokenType;
import pl.lodz.p.it.usermodule.model.user.User;
import pl.lodz.p.it.usermodule.model.token.Token;
import pl.lodz.p.it.usermodule.model.user.UserType;
import pl.lodz.p.it.usermodule.repository.UserRepository;
import pl.lodz.p.it.usermodule.repository.TokenRepository;
import pl.lodz.p.it.usermodule.security.JwtProvider;

import java.time.Instant;
import java.util.UUID;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;
    private final MailService mailService;

    public AuthService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       TokenRepository tokenRepository,
                       PasswordEncoder encoder,
                       JwtProvider jwtProvider, MailService mailService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
        this.mailService = mailService;

        User admin = new User("admin", "Admin", encoder.encode("admin"), UserType.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);
    }

    public User register(RegisterUserDTO dto) {
        String hashedPassword = encoder.encode(dto.getPassword());
        User user = new User(dto.getEmail(), dto.getUsername(), hashedPassword);
        try {
            userRepository.save(user);
            String verificationToken = generateAndSaveVerificationToken(user).getToken();
            String link = "http://localhost:8080/api/register/confirm?token=" + verificationToken;
            mailService.send(user.getEmail(), user.getVisibleName(), link);
            return user;
        } catch (Exception e) {
            throw new EmailAlreadyRegisteredException();
        }
    }


    private Token generateAndSaveVerificationToken(User user) {
        Token verificationToken = new Token();
        verificationToken.setUser(user);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setType(TokenType.VERIFICATION_TOKEN);
        verificationToken.setCreatedAt(Instant.now());
        return tokenRepository.save(verificationToken);
    }

    private Token generateAndSaveRefreshToken(User user) {
        Token verificationToken = new Token();
        verificationToken.setUser(user);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setType(TokenType.REFRESH_TOKEN);
        verificationToken.setCreatedAt(Instant.now());
        return tokenRepository.save(verificationToken);
    }

    public SuccessfulLoginDTO login(LoginDTO dto) {
        Authentication authentication;
        try {
            authentication = authenticationManager.
                    authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (LockedException | DisabledException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED);
        }

        String jwt = jwtProvider.generateJWT(dto.getEmail());
        User user = (User) authentication.getPrincipal();
        Token refreshToken = generateAndSaveRefreshToken(user);
        return new SuccessfulLoginDTO(jwt, refreshToken.getToken(), dto.getEmail());
    }


    @Transactional
    public void confirmRegistration(String token) {
        Token verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
//        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
        User user = verificationToken.getUser();
        user.setActive(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }
}
