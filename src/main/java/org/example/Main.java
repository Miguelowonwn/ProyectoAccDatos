package org.example;

import com.example.biblioteca.entity.*;
import dao.AutorDAO;
import dao.LibroDAO;
import dao.PrestamoDAO;
import dao.UsuarioDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static SessionFactory sessionFactory;
    private static UsuarioDAO usuarioDAO;
    private static LibroDAO libroDAO;
    private static PrestamoDAO prestamoDAO;
    private static AutorDAO autorDAO;

    public static void main(String[] args) {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Usuario.class)
                .addAnnotatedClass(Libro.class)
                .addAnnotatedClass(Autor.class)
                .addAnnotatedClass(Prestamo.class)
                .buildSessionFactory();

        usuarioDAO = new UsuarioDAO(sessionFactory);
        libroDAO = new LibroDAO(sessionFactory);
        prestamoDAO = new PrestamoDAO(sessionFactory);
        autorDAO = new AutorDAO(sessionFactory);

        mostrarMenu();

        sessionFactory.close();
    }

    private static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n==== Menú de Gestión de la Biblioteca ====");
            System.out.println("1. Listar Usuarios");
            System.out.println("2. Crear Usuario");
            System.out.println("3. Listar Libros");
            System.out.println("4. Crear Libro");
            System.out.println("5. Realizar Préstamo");
            System.out.println("6. Devolver Préstamo");
            System.out.println("7. Reportes y Consultas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    listarUsuarios();
                    break;
                case 2:
                    crearUsuario(scanner);
                    break;
                case 3:
                    listarLibros();
                    break;
                case 4:
                    crearLibro(scanner);
                    break;
                case 5:
                    realizarPrestamo(scanner);
                    break;
                case 6:
                    devolverPrestamo(scanner);
                    break;
                case 7:
                    mostrarReportesYConsultas(scanner);
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }


    private static void listarUsuarios() {
        usuarioDAO.findAll().forEach(System.out::println);
    }

    private static void crearUsuario(Scanner scanner) {
        try {
            String nombre;
            System.out.print("Ingrese el nombre del usuario (Menor de 150 carácteres): ");
            do {
                nombre = scanner.nextLine();
                try {
                    if (nombre.length() > 150) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.out.println("Nombre demasiado largo. Introduce un nombre con menos de 150 carácteres");
                }
            } while (nombre.length() > 150);
            System.out.print("Ingrese el email del usuario: ");
            String email = scanner.nextLine();

            Usuario usuario = Usuario.builder()
                    .nombreCompleto(nombre)
                    .email(email)
                    .estado(EstadoUsuario.ACTIVO)
                    .numeroDeMultas(0)
                    .build();

            usuarioDAO.save(usuario);
            System.out.println("Usuario creado con éxito.");
        } catch (Exception e) {
            e.printStackTrace(); // Diagnóstico detallado
            System.out.println("Error al crear el usuario.");
        }
    }


    private static void listarLibros() {
        libroDAO.findAll().forEach(System.out::println);
    }

    private static void crearLibro(Scanner scanner) {
        String titulo;
        System.out.print("Ingrese el título del libro (Menor de 200 carácteres): ");
        do {
            titulo = scanner.nextLine();
            try {
                if (titulo.length() > 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println("Titulo demasiado largo. Introduce un título con menos de 200 carácteres");
            }
        } while (titulo.length() > 200);
        System.out.print("Ingrese el ISBN del libro: ");
        String isbn = scanner.nextLine();
        int anio;
        while (true) {
            System.out.print("Ingrese año en el que se publicó: ");
            if (scanner.hasNextInt()) {  // Verifica si el siguiente valor es un número entero
                anio = scanner.nextInt();

                // Verifica si el año tiene 4 cifras y está dentro de un rango razonable
                if (anio >= 1000 && anio <= 9999) {
                    break;  // Sale del ciclo si el valor es válido
                } else {
                    System.out.println("Por favor, ingrese un año de 4 cifras válido.");
                }
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.next();  // Limpiar el buffer del scanner
            }
        }

        System.out.print("Ingrese la categoria del libro: ");
        String categoria = scanner.nextLine();
        scanner.next();
        System.out.print("Ingrese las copias disponibles del libro: ");
        int copias = scanner.nextInt();

        Libro libro = Libro.builder()
                .titulo(titulo)
                .isbn(isbn)
                .anioPublicacion(anio)
                .categoria(categoria)
                .copiasDisponibles(copias)
                .build();

        libroDAO.save(libro);
        System.out.println("Libro creado con éxito.");
    }

    private static void realizarPrestamo(Scanner scanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fecha = null;
        boolean fechaValida = false;

        System.out.print("Ingrese el ID del usuario: ");
        Long usuarioId = scanner.nextLong();
        System.out.print("Ingrese el ID del libro: ");
        Long libroId = scanner.nextLong();
        do {
            System.out.print("Ingrese la fecha de terminación del préstamo (DD-MM-AAAA): ");
            String entrada = scanner.nextLine();

            try {
                fecha = LocalDate.parse(entrada, formatter);
                fechaValida = true; // Si no lanza excepción, la fecha es válida
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha incorrecto. Por favor, ingrese una fecha en formato DD-MM-AAAA.");
            }
        } while (!fechaValida);


        try {
            Usuario usuario = usuarioDAO.findById(usuarioId);
            Libro libro = libroDAO.findById(libroId);

            if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
                System.out.println("El usuario está suspendido y no puede realizar préstamos.");
                return;
            }

            if (usuario.getNumeroDeMultas() > 3) {
                usuario.setEstado(EstadoUsuario.SUSPENDIDO);
                usuarioDAO.update(usuario);
                System.out.println("El usuario ha sido suspendido por exceso de multas.");
                return;
            }

            if (libro.getCopiasDisponibles() <= 0) {
                System.out.println("No hay copias disponibles del libro.");
                return;
            }

            // Crear préstamo
            Prestamo prestamo = Prestamo.builder()
                    .usuario(usuario)
                    .libro(libro)
                    .fechaPrestamo(Date.valueOf(LocalDate.now()))
                    .fechaDevolucion(Date.valueOf(fecha))
                    .estado(EstadoPrestamo.EN_CURSO)
                    .build();

            libro.setCopiasDisponibles(libro.getCopiasDisponibles() - 1);
            libroDAO.update(libro);

            System.out.println("Préstamo realizado con éxito.");
        } catch (Exception e) {
            System.out.println("Error al realizar el préstamo: " + e.getMessage());
        }
    }


    private static void devolverPrestamo(Scanner scanner) {
        System.out.print("Ingrese el ID del préstamo: ");
        Long prestamoId = scanner.nextLong();

        try {
            Prestamo prestamo = prestamoDAO.findById(prestamoId);

            if (prestamo.getEstado() != EstadoPrestamo.EN_CURSO) {
                System.out.println("Este préstamo ya fue finalizado o no está activo.");
                return;
            }
            prestamo.setEstado(EstadoPrestamo.FINALIZADO);
            prestamoDAO.update(prestamo);

            Libro libro = prestamo.getLibro();
            libro.setCopiasDisponibles(libro.getCopiasDisponibles() + 1);
            libroDAO.update(libro);

            System.out.println("Préstamo devuelto con éxito.");
        } catch (Exception e) {
            System.out.println("Error al devolver el préstamo: " + e.getMessage());
        }
    }


    private static void mostrarReportesYConsultas(Scanner scanner) {
        System.out.println("\n1. Libros más prestados en los últimos 12 meses");
        System.out.println("2. Libros no prestados en los últimos 6 meses");
        System.out.println("3. Listar todos los autores cuyos libros se encuentran con un promedio de copias disponibles menor al 20%.");
        System.out.println("4. Consultar cuántos libros por categoría tiene la biblioteca, ordenados por la categoría con más ejemplares");
        System.out.println("5. Usuarios más activos");
        System.out.println("6. Autores destacados");
        int opcion = scanner.nextInt();

        switch (opcion) {
            case 1:
                System.out.println("\n==== Libros y autores más prestados en los últimos 12 meses ====");
                List<Object[]> librosMasPrestadoVar = libroDAO.librosMasPrestados();

                if (librosMasPrestadoVar.isEmpty()) {
                    System.out.println("No hay datos de préstamos en los últimos 12 meses.");
                } else {
                    for (Object[] libro : librosMasPrestadoVar) {
                        String titulo = (String) libro[0];  // Título del libro
                        String nombreCompleto = (String) libro[1];  // Nombre del autor
                        Long prestamos = (Long) libro[2];  // Número de préstamos (debe ser Long)

                        // Imprimir el título, nombre del autor y el número de préstamos
                        System.out.printf("Título: %s, Autor: %s, Préstamos: %d%n", titulo, nombreCompleto, prestamos);
                    }

                }
                break;

            case 2:  // Caso 4, para mostrar los libros no prestados en los últimos 6 meses
                List<Libro> librosMenosPrestados = libroDAO.librosNoPrestadosUltimos6Meses();
                if (librosMenosPrestados.isEmpty()) {
                    System.out.println("No hay libros que no hayan sido prestados en los últimos 6 meses.");
                } else {
                    System.out.println("\n==== Libros no prestados en los últimos 6 meses ====");
                    librosMenosPrestados.forEach(libro -> {
                        System.out.println("Título: " + libro.getTitulo() + ", ISBN: " + libro.getIsbn() + ", Año de Publicación: " + libro.getAnioPublicacion());
                    });
                }
                break;
            case 3:
                List<Autor> autoresBajoPromedio = autorDAO.autoresConBajoPromedioDeCopias();
                if (autoresBajoPromedio.isEmpty()) {
                    System.out.println("No hay autores con libros cuyo promedio de copias disponibles sea menor al 20%.");
                } else {
                    System.out.println("\n==== Autores con libros con bajo promedio de copias disponibles ====");
                    autoresBajoPromedio.forEach(autor -> {
                        System.out.println("Autor: " + autor.getNombreCompleto());  // Se asume que "getNombre" es un método de la clase Autor
                    });
                }
                break;
            case 4:
                List<Object[]> usuariosActivos = usuarioDAO.usuariosMasActivos();
                if (usuariosActivos.isEmpty()) {
                    System.out.println("No hay usuarios activos con datos suficientes.");
                } else {
                    usuariosActivos.forEach(usuario -> {
                        String nombre = (String) usuario[0];
                        Long totalPrestamos = (Long) usuario[1];
                        Long devolucionesATiempo = (Long) usuario[2];
                        System.out.printf("Usuario: %s, Total Prestamos: %d, Devoluciones a Tiempo: %d%n",
                                nombre, totalPrestamos, devolucionesATiempo);
                    });
                }
                break;
            case 5:
                List<Object[]> resultados = autorDAO.autoresDestacados();
                if (resultados != null && !resultados.isEmpty()) {
                    for (Object[] resultado : resultados) {
                        // El primer elemento es el nombre del autor (String)
                        String nombreAutor = (String) resultado[0];
                        // El segundo elemento es el número total de préstamos (Long)
                        Long totalPrestamos = (Long) resultado[1];

                        // Imprimir los resultados de forma legible
                        System.out.printf("Autor: %s, Total Prestamos: %d%n", nombreAutor, totalPrestamos);
                    }
                } else {
                    System.out.println("No hay autores destacados.");
                }
                break;

            default:
                System.out.println("Opción no válida.");
                break;
        }
    }
}


