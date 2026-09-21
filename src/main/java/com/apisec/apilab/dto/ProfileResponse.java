package com.apisec.apilab.dto;

import java.time.Instant;

public class ProfileResponse {

    private String name;
    private String email;
    private String role;
    private Instant createdAt;

    public ProfileResponse() {
    }

    public ProfileResponse(String name, String email, String role, Instant createdAt) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
