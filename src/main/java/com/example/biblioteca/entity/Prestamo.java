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
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "libro_id")
    @ToString.Exclude  // Evita bucles infinitos al imprimir
    private Libro libro;


    @Column(name = "fechaPrestamo", nullable = false)
    private Date fechaPrestamo;

    @Column(name = "fechaDevolucion")
    private Date fechaDevolucion;

    @Column(name = "fechaDevolucionReal")
    private Date fechaDevolucionReal;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoPrestamo estado;

    // Getters, setters y constructores
}


