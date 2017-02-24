package com.giraldo.jaime.loginandroid;

import java.io.Serializable;

/**
 * Created by jaime on 24/02/2017.
 */

public class Usuario implements Serializable {

    private String nickname;
    private String correo;
    private String pass;

    public Usuario(String nickname, String correo, String pass) {
        this.nickname = nickname;
        this.correo = correo;
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public String getNickname() {
        return nickname;
    }

    public String getCorreo() {
        return correo;
    }

}
