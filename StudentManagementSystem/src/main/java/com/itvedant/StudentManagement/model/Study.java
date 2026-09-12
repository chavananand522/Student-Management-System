package com.itvedant.StudentManagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "study")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String subject;

    private String description;

    public Study() {
    }

    public Study(String subject, String description) {
        this.subject = subject;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}