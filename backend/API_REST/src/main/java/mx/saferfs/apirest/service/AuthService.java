package mx.saferfs.apirest.service;

import lombok.RequiredArgsConstructor;
import mx.saferfs.apirest.dto.request.LoginRequest;
import mx.saferfs.apirest.dto.request.RefreshRequest;
import mx.saferfs.apirest.dto.request.RegisterRequest;
import mx.saferfs.apirest.dto.response.AuthResponse;
import mx.saferfs.apirest.entity.Role;
import mx.saferfs.apirest.entity.Usuario;
import mx.saferfs.apirest.exception.EmailAlreadyExistsException;
import mx.saferfs.apirest.exception.InvalidCredentialsException;
import mx.saferfs.apirest.exception.InvalidRefreshTokenException;
import mx.saferfs.apirest.exception.UserNotFoundException;
import mx.saferfs.apirest.mapper.UserMapper;
import mx.saferfs.apirest.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register (RegisterRequest request){

        if(repo.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException();
        }

        Usuario user = new Usuario();
        user.setNombre(request.nombre());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRol(Role.ADMIN);

        repo.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                UserMapper.toResponse(user)
        );
    }

    public AuthResponse login(LoginRequest request){

        Usuario user = repo.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new InvalidCredentialsException();

        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                UserMapper.toResponse(user)
        );
    }

    public AuthResponse refresh(RefreshRequest request){

        String email;

        try {
            email = jwtService.extractUsername(request.refreshToken());
        } catch (Exception e) {
            throw new InvalidRefreshTokenException();
        }

        if(email == null) throw new InvalidRefreshTokenException();

        Usuario user = repo.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if(!jwtService.isTokenValid(request.refreshToken(), user)){
            throw new InvalidRefreshTokenException();
        }

        String newAccess = jwtService.generateToken(user);

        return new AuthResponse(newAccess, request.refreshToken(), UserMapper.toResponse(user));
    }

}
