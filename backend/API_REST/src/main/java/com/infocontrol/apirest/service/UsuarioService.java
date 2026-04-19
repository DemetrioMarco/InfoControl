package com.infocontrol.apirest.service;

import lombok.RequiredArgsConstructor;
import com.infocontrol.apirest.dto.request.ChangePasswordRequest;
import com.infocontrol.apirest.dto.request.UsuarioRequest;
import com.infocontrol.apirest.dto.request.UsuarioUpdateRequest;
import com.infocontrol.apirest.dto.response.UserResponse;
import com.infocontrol.apirest.entity.Role;
import com.infocontrol.apirest.entity.Usuario;
import com.infocontrol.apirest.exception.EmailAlreadyExistsException;
import com.infocontrol.apirest.exception.PasswordConfirmException;
import com.infocontrol.apirest.exception.PasswordMismatchException;
import com.infocontrol.apirest.exception.UserNotFoundException;
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
    public UserDetails loadUserByUsername(String email){
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
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public UserResponse crear(UsuarioRequest request) {
        if (repo.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
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
                .orElseThrow(UserNotFoundException::new);

        // Validar email único solo si cambió
        boolean emailCambio = !user.getEmail().equalsIgnoreCase(request.email());
        if (emailCambio && repo.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
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
            throw new UserNotFoundException();
        }
        repo.deleteById(id);
    }

    @Transactional
    public void cambiarPassword(Long id, ChangePasswordRequest request) {
        Usuario user = repo.findById(id)
                .orElseThrow(UserNotFoundException::new);

        // 1. Verificar que la contraseña actual es correcta
        if (!passwordEncoder.matches(request.passwordActual(), user.getPassword())) {
            throw new PasswordMismatchException();
        }

        // 2. Verificar que nueva contraseña y confirmación coinciden
        if (!request.passwordNuevo().equals(request.passwordConfirm())) {
            throw new PasswordConfirmException();
        }

        user.setPassword(passwordEncoder.encode(request.passwordNuevo()));
        repo.save(user);
    }

}
