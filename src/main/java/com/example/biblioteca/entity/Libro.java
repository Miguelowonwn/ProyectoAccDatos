package com.example.biblioteca.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "libros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @Column(name = "isbn", unique = true, nullable = false)
    private String isbn;

    @Column(name = "anioPublicacion", nullable = false)
    private Integer anioPublicacion;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "copiasDisponibles", nullable = false)
    private Integer copiasDisponibles;

    @ManyToMany(mappedBy = "libros")
    @ToString.Exclude  // Evita bucles infinitos al imprimir
    private List<Autor> autores;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL)
    @ToString.Exclude  // Evita bucles infinitos al imprimir
    private List<Prestamo> prestamos;

}
