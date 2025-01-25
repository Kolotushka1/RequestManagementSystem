package org.vaadin.example.domain;

import lombok.Getter;

@Getter
public enum Role {
    OPERATOR,
    CLIENT;

    public String getAuthority() {
        return "ROLE_" + name();
    }

}