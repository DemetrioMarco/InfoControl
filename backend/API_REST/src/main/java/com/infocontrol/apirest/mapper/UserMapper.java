package com.infocontrol.apirest.mapper;

import com.infocontrol.apirest.dto.response.UserResponse;
import com.infocontrol.apirest.entity.Usuario;

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
