package dao;

import com.example.biblioteca.entity.Autor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;  // Asegúrate de importar java.sql.Date
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class AutorDAO {

    private final SessionFactory sessionFactory;

    public AutorDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Autor autor) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(autor);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public Autor findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Autor.class, id);
        }
    }

    public List<Autor> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Autor", Autor.class).list();
        }
    }

    public List<Object[]> autoresDestacados() {
        String query = """
            SELECT a.nombreCompleto, COUNT(p.id) AS totalPrestamos
            FROM Autor a
            JOIN a.libros l
            JOIN Prestamo p ON l.id = p.libro.id
            WHERE p.fechaPrestamo >= :fechaInicio
            GROUP BY a.id
            ORDER BY totalPrestamos DESC
    """;

        LocalDate haceUnAnio = LocalDate.now().minusYears(1);

        // Convertir LocalDate a java.sql.Date
        Date fechaInicio = Date.valueOf(haceUnAnio);

        List<Object[]> resultados = null;
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> queryObj = session.createQuery(query);
            queryObj.setParameter("fechaInicio", fechaInicio);  // Pasa el java.sql.Date

            // Limitar los resultados a los primeros 10
            queryObj.setMaxResults(10);

            // Ejecutar la consulta
            resultados = queryObj.getResultList();
        } catch (Exception e) {
            System.out.println("Error al obtener los autores destacados: " + e.getMessage());
        }
        return resultados;
    }

    public List<Autor> autoresConBajoPromedioDeCopias() {
        String query = """
        SELECT a
        FROM Autor a
        JOIN a.libros l
        WHERE (l.copiasDisponibles * 1.0 / l.copiasTotales) < 0.2
        GROUP BY a
    """;

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(query, Autor.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error al obtener autores con libros con bajo promedio de copias disponibles: " + e.getMessage());
            return Collections.emptyList();
        }
    }


}
