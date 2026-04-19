package com.infocontrol.apirest.service;

import lombok.RequiredArgsConstructor;
import com.infocontrol.apirest.dto.request.LoginRequest;
import com.infocontrol.apirest.dto.request.RefreshRequest;
import com.infocontrol.apirest.dto.request.RegisterRequest;
import com.infocontrol.apirest.dto.response.AuthResponse;
import com.infocontrol.apirest.entity.Role;
import com.infocontrol.apirest.entity.Usuario;
import com.infocontrol.apirest.exception.EmailAlreadyExistsException;
import com.infocontrol.apirest.exception.InvalidCredentialsException;
import com.infocontrol.apirest.exception.InvalidRefreshTokenException;
import com.infocontrol.apirest.exception.UserNotFoundException;
import com.infocontrol.apirest.mapper.UserMapper;
import com.infocontrol.apirest.repository.UsuarioRepository;
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
