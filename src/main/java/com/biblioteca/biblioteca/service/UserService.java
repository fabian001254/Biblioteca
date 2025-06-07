package com.biblioteca.biblioteca.service;

import com.biblioteca.biblioteca.exceptions.userExceptions.UserAlreadyExistsException;
import com.biblioteca.biblioteca.exceptions.userExceptions.UserInvalidCredentials;
import com.biblioteca.biblioteca.exceptions.userExceptions.UserNotExist;
import com.biblioteca.biblioteca.mapper.UserMapper;
import com.biblioteca.biblioteca.model.DTO.UserDTO;
import com.biblioteca.biblioteca.model.DTO.UserDTOWithOutPass;
import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean login(String email, String password) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return passwordEncoder.matches(password, user.getPassword());
            }
            throw new UserInvalidCredentials();
        } catch (Exception e) {
            throw new UserInvalidCredentials();
        }
    }

    public boolean register(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if(existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(userMapper.userDTOtouSer(userDTO));
        return true;
    }

    public boolean UpdateUser(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(userMapper.userDTOtouSer(userDTO));
            return true;
        }
        return false;
    }

    public boolean deleteUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            return true;
        }
        return false;
    }

    public UserDTOWithOutPass getUser(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return userMapper.UserToUserWithoutPass(userOpt.get());
            }
            throw new UserNotExist();
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<UserDTO> userExists(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserDTO userDTO = userMapper.userToUserDTO(userOpt.get());
            return Optional.of(userDTO);
        }
        return Optional.empty();
    }


}
