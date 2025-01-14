package com.example.demo.model;

import com.example.demo.enums.CodeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @NotBlank(message = "Code is required")
    private String code;

    @Enumerated(EnumType.STRING)
    private CodeType type;
}
