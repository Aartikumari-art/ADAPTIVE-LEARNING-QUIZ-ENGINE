package com.quiz.AdaptiveQuiz.dto;

public class UserProfileDTO {
    private String name;
    private String email;
    private String phone;
    private String bio;
    private String address;
    private int profileCompletionPercentage;

    private Long id;
    private String role;
    private String provider;

    public UserProfileDTO(Long id, String name, String email, String phone, String bio, String address, String role,
            String provider,
            int profileCompletionPercentage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bio = bio;
        this.address = address;
        this.role = role;
        this.provider = provider;
        this.profileCompletionPercentage = profileCompletionPercentage;
    }

    // Getters and Setters
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getProfileCompletionPercentage() {
        return profileCompletionPercentage;
    }

    public void setProfileCompletionPercentage(int profileCompletionPercentage) {
        this.profileCompletionPercentage = profileCompletionPercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
