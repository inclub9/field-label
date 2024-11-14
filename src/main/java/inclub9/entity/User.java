package inclub9.entity;

import inclub9.annotation.FieldLabel;

public class User {
    private Long id;

    @FieldLabel("Full Name")
    String fullName;

    @FieldLabel("Email Address")
    String email;
}