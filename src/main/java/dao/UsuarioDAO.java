package dao;

import com.example.biblioteca.entity.EstadoUsuario;
import com.example.biblioteca.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final SessionFactory sessionFactory;

    public UsuarioDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(usuario);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }



    public Usuario findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Usuario.class, id);
        }
    }

    public List<Usuario> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Usuario", Usuario.class).list();
        }
    }

    public void update(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(usuario);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public void delete(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(usuario);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public List<Usuario> findByEstado(EstadoUsuario estado) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Usuario WHERE estado = :estado", Usuario.class)
                    .setParameter("estado", estado)
                    .list();
        }
    }

    public List<Object[]> usuariosMasActivos() {
        String query = """
            SELECT u.nombreCompleto, COUNT(p.id) AS totalPrestamos,
                   SUM(CASE WHEN p.fechaDevolucionReal <= p.fechaDevolucion THEN 1 ELSE 0 END) AS devolucionesATiempo
            FROM Usuario u
            JOIN Prestamo p ON u.id = p.usuario.id
            WHERE p.estado = 'FINALIZADO'
            GROUP BY u.id
            ORDER BY totalPrestamos DESC, devolucionesATiempo DESC
            """;

        List<Object[]> resultados = new ArrayList<>();  // Asegura que no sea null
        try (Session session = sessionFactory.openSession()) {
            // Crear la consulta HQL
            var queryObject = session.createQuery(query);

            // Limitar los resultados a los primeros 10
            queryObject.setMaxResults(10);

            // Ejecutar la consulta
            resultados = queryObject.getResultList();
        } catch (Exception e) {
            System.out.println("Error al obtener los usuarios más activos: " + e.getMessage());
            e.printStackTrace();  // Esto te dará más detalles sobre el error
        }
        return resultados;  // Retorna una lista vacía si no hay resultados
    }



}
