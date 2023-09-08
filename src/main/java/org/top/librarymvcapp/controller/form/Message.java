package org.top.librarymvcapp.controller.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Message {
    String message;

    public Message() {
        this.message = "";
    }
}
