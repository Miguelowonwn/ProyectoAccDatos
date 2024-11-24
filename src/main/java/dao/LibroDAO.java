package dao;

import com.example.biblioteca.entity.Libro;
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
            return session.find(Libro.class, id);
        }
    }

    public List<Libro> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Libro", Libro.class).list();
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
        List<Object[]> resultado = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            String hql = """
        SELECT l.titulo, a.nombreCompleto, COUNT(p.id)
        FROM Prestamo p
        JOIN p.libro l
        JOIN l.autores a
        WHERE p.fechaPrestamo BETWEEN :fechaInicio AND :fechaFin
        GROUP BY l.titulo, a.nombreCompleto
        ORDER BY COUNT(p.id) DESC
        """;

            Query<Object[]> query = session.createQuery(hql, Object[].class);

            // Calcular fechas para los últimos 12 meses
            LocalDate fechaFin = LocalDate.now();
            LocalDate fechaInicio = fechaFin.minusMonths(12);

            // Convertir fechas a java.sql.Date
            query.setParameter("fechaInicio", java.sql.Date.valueOf(fechaInicio));
            query.setParameter("fechaFin", java.sql.Date.valueOf(fechaFin));

            resultado = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public List<Libro> librosNoPrestadosUltimos6Meses() {
        String query = """
            SELECT l
            FROM Libro l
            WHERE NOT EXISTS (
                SELECT p
                FROM Prestamo p
                WHERE p.libro = l AND p.fechaPrestamo >= :fechaLimite
            )
            """;

        LocalDate fechaLimite = LocalDate.now().minusMonths(6);

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(query, Libro.class)
                    .setParameter("fechaLimite", Date.valueOf(fechaLimite))
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Error al obtener libros no prestados en los últimos 6 meses: " + e.getMessage());
            return Collections.emptyList();
        }
    }





}
