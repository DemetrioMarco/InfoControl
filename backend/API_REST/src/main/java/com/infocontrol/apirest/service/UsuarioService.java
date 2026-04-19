package com.infocontrol.apirest.service;

import lombok.RequiredArgsConstructor;
import com.infocontrol.apirest.dto.request.ChangePasswordRequest;
import com.infocontrol.apirest.dto.request.UsuarioRequest;
import com.infocontrol.apirest.dto.request.UsuarioUpdateRequest;
import com.infocontrol.apirest.dto.response.UserResponse;
import com.infocontrol.apirest.entity.Role;
import com.infocontrol.apirest.entity.Usuario;
import com.infocontrol.apirest.exception.auth.PasswordConfirmException;
import com.infocontrol.apirest.exception.auth.PasswordMismatchException;
import com.infocontrol.apirest.exception.base.DuplicateResourceException;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import com.infocontrol.apirest.mapper.UserMapper;
import com.infocontrol.apirest.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRol().name())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listarTodos() {
        return repo.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse buscarPorId(Long id) {
        return repo.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Transactional
    public UserResponse crear(UsuarioRequest request) {
        if (repo.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email ya registrado: " + request.email());
        }

        Usuario user = new Usuario();
        user.setNombre(request.nombre());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRol(request.rol() != null ? request.rol() : Role.OPERADOR);

        return UserMapper.toResponse(repo.save(user));
    }

    @Transactional
    public UserResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        boolean emailCambio = !user.getEmail().equalsIgnoreCase(request.email());
        if (emailCambio && repo.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email ya registrado: " + request.email());
        }

        user.setNombre(request.nombre());
        user.setEmail(request.email());
        user.setRol(request.rol());
        user.setEnabled(request.enabled());

        return UserMapper.toResponse(repo.save(user));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        repo.deleteById(id);
    }

    @Transactional
    public void cambiarPassword(Long id, ChangePasswordRequest request) {
        Usuario user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (!passwordEncoder.matches(request.passwordActual(), user.getPassword())) {
            throw new PasswordMismatchException();
        }

        if (!request.passwordNuevo().equals(request.passwordConfirm())) {
            throw new PasswordConfirmException();
        }

        user.setPassword(passwordEncoder.encode(request.passwordNuevo()));
        repo.save(user);
    }
}
