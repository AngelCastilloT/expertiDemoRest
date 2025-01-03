package cl.angel.rest.repositorio;

import cl.angel.rest.dominio.modelo.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author angelexperti
 */
@Repository
public interface RepositorioEmpleado extends JpaRepository<Empleado, Long> {

    /**
     *
     * @param rut Rut del embleando
     * @return El empleado asociado al rut
     */
    public Empleado findByRut(Long rut);
}
