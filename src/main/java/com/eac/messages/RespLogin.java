package com.eac.messages;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RespLogin {
    private Integer idUser;
    private String name;
    private String role;
}
