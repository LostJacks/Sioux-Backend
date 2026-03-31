package org.spms.spmsbackend.domain.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private Role role;
    private String firstName;
    private String lastName;
    private String building;
}