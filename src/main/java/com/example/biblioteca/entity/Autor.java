package com.example.biblioteca.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombreCompleto", length = 150, nullable = false)
    private String nombreCompleto;

    @Column(name = "nacionalidad", nullable = false)
    private String nacionalidad;


    @ManyToMany
    @JoinTable(
            name = "autores_libros",
            joinColumns = @JoinColumn(name = "autor_id"),
            inverseJoinColumns = @JoinColumn(name = "libro_id")
    )
    @ToString.Exclude  // Ya configurado
    private List<Libro> libros;



}

