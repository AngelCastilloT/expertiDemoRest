package cl.angel.rest.repositorio;

import cl.angel.rest.dominio.modelo.Aprobacion;
import cl.angel.rest.dominio.modelo.Empleado;
import cl.angel.rest.dominio.modelo.Vacacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author angelexperti
 */
@Repository
public interface RepositorioVacacion extends JpaRepository<Vacacion, Long> {

    public List<Vacacion> findByAprobacion(Aprobacion aprobacion);

    public List<Vacacion> findByAprobacionEmpleadoAndAprobacionMotivoIgnoreCase(Empleado empleado, String motivo);

    public List<Vacacion> findByAprobacionEmpleado(Empleado empleado);

}
