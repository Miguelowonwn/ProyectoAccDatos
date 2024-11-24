package com.example.biblioteca.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(name = "fechaPrestamo", nullable = false)
    private Date fechaPrestamo;

    @Column(name = "fechaDevolucion", nullable = false)
    private Date fechaDevolucion;

    @Column(name = "fechaDevolucionReal")
    private Date fechaDevolucionReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPrestamo estado;
}

