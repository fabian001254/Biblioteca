package com.biblioteca.biblioteca.model.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTOWithOutPass {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String role;
}
