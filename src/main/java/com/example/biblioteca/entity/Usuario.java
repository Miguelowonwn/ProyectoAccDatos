package com.example.biblioteca.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "usuarios")
@Data // Genera automáticamente getters, setters, equals, hashCode y toString
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los campos
@Builder // Genera un patrón Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombreCompleto", length = 150, nullable = false)
    private String nombreCompleto;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado;

    @Column(name = "numeroDeMultas", nullable = false)
    private Integer numeroDeMultas = 0;


}

