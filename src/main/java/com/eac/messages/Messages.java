package com.eac.messages;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Messages {
    private String responseCode;
    private String responseMessage;
    private Object data;

    public void success() {
        this.responseCode = "000";
        this.responseMessage = "Sukses";
    }

    public void failed(String msg) {
        this.responseCode = "001";
        this.responseMessage = msg;
    }
}
