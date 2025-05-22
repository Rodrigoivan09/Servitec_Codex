// AuthRequest.java
package edu.unam.springsecurity.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String login;      // puede ser correo o teléfono
    private String contrasena;
}
