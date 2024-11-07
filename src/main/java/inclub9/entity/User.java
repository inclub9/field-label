package inclub9.entity;

import inclub9.annotation.FieldLabel;

public class User {
    private Long id;

    @FieldLabel("Full Name")
    private String fullName;

    @FieldLabel("Email Address")
    private String email;
}