package com.biblioteca.biblioteca.model.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String city;
    private String role;
}
