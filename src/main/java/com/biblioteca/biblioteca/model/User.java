package com.biblioteca.biblioteca.model;

import com.biblioteca.biblioteca.model.Enums.Role;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "biblioteca_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String city;
    
    @Enumerated(EnumType.STRING)
    private Role role;
}
