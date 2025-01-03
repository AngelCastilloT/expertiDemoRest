package cl.angel.rest.administrador;

import cl.angel.rest.dominio.modelo.Empleado;
import cl.angel.rest.repositorio.RepositorioEmpleado;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author angelexperti
 */
@Service
public class AdministradorEmpleado {

    private final RepositorioEmpleado repositorioEmpleado;

    @Autowired
    public AdministradorEmpleado(RepositorioEmpleado repositorioEmpleado) {
        this.repositorioEmpleado = repositorioEmpleado;
    }

    public Empleado consultar(Long rut) {
        Empleado emp = null;
        if (rut != null) {
            emp = repositorioEmpleado.findByRut(rut);
        }
        return emp;
    }

    public List<Empleado> consultarTodos() {
        return repositorioEmpleado.findAll();
    }

    @Transactional
    public Empleado guardar(Empleado emp) {
        Empleado guardado = null;
        if (emp != null) {
            guardado = repositorioEmpleado.saveAndFlush(emp);
        }
        return guardado;
    }

    @Transactional
    public void eliminar(Empleado emp) {
        if (emp != null) {
            repositorioEmpleado.delete(emp);
        }
    }

}
