package mx.saferfs.apirest.mapper;

import mx.saferfs.apirest.dto.response.UserResponse;
import mx.saferfs.apirest.entity.Usuario;

public class UserMapper {

    public static UserResponse toResponse(Usuario user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getRol().name(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


}
