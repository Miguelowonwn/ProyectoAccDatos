package dao;

import com.example.biblioteca.entity.Libro;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibroDAO {

    private final SessionFactory sessionFactory;

    public LibroDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Libro libro) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(libro);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public Libro findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Libro.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Libro> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<Libro> libros = session.createQuery("FROM Libro", Libro.class).list();

            libros.forEach(libro -> {
                Hibernate.initialize(libro.getAutores());
                Hibernate.initialize(libro.getPrestamos());
            });

            return libros;
        }
    }


    public void update(Libro libro) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(libro);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public void delete(Libro libro) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(libro);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public List<Object[]> librosMasPrestados() {
        try (Session session = sessionFactory.openSession()) {
            String hql = """
            SELECT l.titulo, a.nombreCompleto, COUNT(p.id) AS totalPrestamos
            FROM Prestamo p
            JOIN p.libro l
            JOIN l.autores a
            WHERE p.fechaPrestamo BETWEEN :fechaInicio AND :fechaFin
            GROUP BY l.titulo, a.nombreCompleto
            ORDER BY totalPrestamos DESC
        """;

            Query<Object[]> query = session.createQuery(hql, Object[].class);

            // Calcular fechas para el último año
            LocalDate fechaFin = LocalDate.now();
            LocalDate fechaInicio = fechaFin.minusMonths(12);

            // Pasar fechas como parámetros
            query.setParameter("fechaInicio", java.sql.Date.valueOf(fechaInicio));
            query.setParameter("fechaFin", java.sql.Date.valueOf(fechaFin));

            return query.list();
        } catch (Exception e) {
            System.out.println("Error al obtener los libros más prestados: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<Libro> librosNoPrestadosUltimos6Meses() {
        String hql = """
        SELECT l
        FROM Libro l
        WHERE l.id NOT IN (
            SELECT DISTINCT p.libro.id
            FROM Prestamo p
            WHERE p.fechaPrestamo >= :fechaLimite
        )
    """;

        LocalDate fechaLimite = LocalDate.now().minusMonths(6);

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(hql, Libro.class)
                    .setParameter("fechaLimite", Date.valueOf(fechaLimite))
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Error al obtener libros no prestados en los últimos 6 meses: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    public List<Object[]> titulosPorCategoria() {
        try (Session session = sessionFactory.openSession()) {
            String hql = """
            SELECT l.categoria, COUNT(l.id) AS totalTitulos
            FROM Libro l
            GROUP BY l.categoria
            ORDER BY totalTitulos DESC
        """;

            return session.createQuery(hql, Object[].class).getResultList();
        } catch (Exception e) {
            System.out.println("Error al obtener el conteo de títulos por categoría: " + e.getMessage());
            return new ArrayList<>();
        }
    }




}
