package com.eac.messages;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReqLogin {
    private String email;
    private String password;
}
