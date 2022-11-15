package pl.lodz.p.it.usermodule.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.usermodule.dto.request.LoginDTO;
import pl.lodz.p.it.usermodule.dto.request.RegisterUserDTO;
import pl.lodz.p.it.usermodule.dto.response.SuccessfulLoginDTO;
import pl.lodz.p.it.usermodule.exception.EmailAlreadyRegisteredException;
import pl.lodz.p.it.usermodule.model.RefreshToken;
import pl.lodz.p.it.usermodule.model.User;
import pl.lodz.p.it.usermodule.model.VerificationToken;
import pl.lodz.p.it.usermodule.repository.RefreshTokenRepository;
import pl.lodz.p.it.usermodule.repository.UserRepository;
import pl.lodz.p.it.usermodule.repository.VerificationTokenRepository;
import pl.lodz.p.it.usermodule.security.JwtProvider;

import java.time.Instant;
import java.util.UUID;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;
    private final MailService mailService;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository, AuthenticationManager authenticationManager,
                       VerificationTokenRepository verificationTokenRepository,
                       PasswordEncoder encoder,
                       JwtProvider jwtProvider, MailService mailService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.verificationTokenRepository = verificationTokenRepository;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
        this.mailService = mailService;

        User admin = new User("admin", "Admin", encoder.encode("admin"), User.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);
    }

    public User register(RegisterUserDTO dto) {
        String hashedPassword = encoder.encode(dto.getPassword());
        User user = new User(dto.getEmail(), dto.getUsername(), hashedPassword);
        try {
            userRepository.save(user);
            String token = generateAndSaveVerificationToken(user).getToken();
            String link = "http://localhost:8080/api/register/confirm?token=" + token;
            mailService.send(user.getEmail(), user.getVisibleName(), link);
            return user;
        } catch (Exception e) {
            throw new EmailAlreadyRegisteredException();
        }
    }


    private VerificationToken generateAndSaveVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken(user);
        return verificationTokenRepository.save(verificationToken);
    }

    public SuccessfulLoginDTO login(LoginDTO dto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (LockedException | DisabledException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED);
        }

        String jwt = jwtProvider.generateJWT(dto.getEmail());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());
        refreshTokenRepository.save(refreshToken);
        return new SuccessfulLoginDTO(jwt, refreshToken.getToken(), dto.getEmail());
    }


    @Transactional
    public void confirmRegistration(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
//        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
        User user = verificationToken.getUser();
        user.setActive(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }
}
