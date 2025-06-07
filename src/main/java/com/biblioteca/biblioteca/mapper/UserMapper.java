package com.biblioteca.biblioteca.mapper;

import com.biblioteca.biblioteca.model.DTO.UserDTO;
import com.biblioteca.biblioteca.model.DTO.UserDTOWithOutPass;
import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {

    public User userDTOtouSer(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(Role.valueOf(userDTO.getRole()));
        return user;
    }
    
    public User userDTOtouSer(Optional<UserDTO> optionalUserDTO) {
        if (optionalUserDTO.isPresent()) {
            return userDTOtouSer(optionalUserDTO.get());
        }
        return null;
    }

    public UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole().name());
        return userDTO;
    }

    public UserDTOWithOutPass UserToUserWithoutPass(User user) {
        UserDTOWithOutPass userDTO = new UserDTOWithOutPass();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        return userDTO;
    }

    public UserDTOWithOutPass UserDTOToUserWithoutPass(UserDTO user) {
        UserDTOWithOutPass userDTO = new UserDTOWithOutPass();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        return userDTO;
    }


}
