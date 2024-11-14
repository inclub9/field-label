package inclub9.entity;

import inclub9.annotation.FieldLabel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    private Long id;

    @FieldLabel("Full Name")
    String fullName;

    @FieldLabel("Email Address")
    String email;
}