package com.example.PKI.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SubjectData {
    @Id
    private Long id;
    private String email;
    private String name;
    private String lastname;
}
