package com.example.planetze;

public class User {
    private String name;
    private String email;
    private boolean on_boarded;

    public User(){
    }

    public User(String name, String email, boolean on_boarded){
        this.name = name;
        this.email = email;
        this.on_boarded = on_boarded;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isOn_boarded() {
        return on_boarded;
    }
}
