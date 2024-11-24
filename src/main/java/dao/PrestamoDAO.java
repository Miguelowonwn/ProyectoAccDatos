package dao;

import com.example.biblioteca.entity.Prestamo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class PrestamoDAO {

    private final SessionFactory sessionFactory;

    public PrestamoDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Prestamo prestamo) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(prestamo);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public Prestamo findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Prestamo.class, id);
        }
    }

    public List<Prestamo> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Prestamo", Prestamo.class).list();
        }
    }

    public void update(Prestamo prestamo) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Actualiza el préstamo en la base de datos
            session.update(prestamo);

            transaction.commit();
            System.out.println("Préstamo actualizado con éxito.");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Error al actualizar el préstamo: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
