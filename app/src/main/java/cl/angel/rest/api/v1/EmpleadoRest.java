package cl.angel.rest.api.v1;

import cl.angel.rest.administrador.AdministradorEmpleado;
import cl.angel.rest.dominio.modelo.Empleado;
import cl.angel.rest.dominio.vo.EmpleadoVO;
import cl.angel.rest.excepcion.DatoMaloException;
import cl.angel.rest.excepcion.RepetidoException;
import cl.angel.rest.excepcion.SinDatosException;
import cl.angel.rest.utils.TextoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/empleados",
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class EmpleadoRest {

    private final AdministradorEmpleado administradorEmpleado;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdministradorEmpleado.class);

    @Autowired
    public EmpleadoRest(AdministradorEmpleado administradorEmpleado) {
        this.administradorEmpleado = administradorEmpleado;
    }

    @Operation(summary = "Consulta empleado", description = "Busca en la base de datos usando el rut el empleado asociado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Se encuentra un empleado",
                content = @Content(schema = @Schema(implementation = EmpleadoVO.class))
        ),
        @ApiResponse(responseCode = "404",
                description = "El rut proporcionado no existe en la base de datos",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(responseCode = "415",
                description = "No se use el método GET",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(responseCode = "400",
                description = "La sintaxis del dato no es válida",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @GetMapping(value = "/{rut}",
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EmpleadoVO> consultarEmpleadoPorRut(@PathVariable("rut") Long rut) {
        Empleado empleado = administradorEmpleado.consultar(rut);
        if (empleado == null) {
            throw new SinDatosException(String.format("No se ha encontrado empleado con rut %d", rut));
        }

        LOGGER.info("Encontré el empleado {}", empleado);
        return ResponseEntity.ok(new EmpleadoVO(empleado));
    }

    //CREACIÓN -> POST
    @PostMapping(value = "/{rut}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EmpleadoVO> crearEmpleado(@PathVariable("rut") Long rut, @RequestBody EmpleadoVO body) {

        final Long bodyRut = body.getRut();
        if (bodyRut != null && !java.util.Objects.equals(rut, bodyRut)) {
            throw new DatoMaloException("Los ruts no coinciden");
        }

        final String nombres = TextoUtils.normalizar(body.getNombres());
        if (StringUtils.isBlank(nombres)) {
            throw new DatoMaloException("Los nombres no pueden ser vacíos");
        }

        if (StringUtils.length(nombres) > 250) {
            throw new DatoMaloException("Los nombres exceden el largo permitido de 250");
        }

        final String apellidos = TextoUtils.normalizar(body.getApellidos());
        if (StringUtils.isBlank(apellidos)) {
            throw new DatoMaloException("Los apellidos no pueden ser vacíos");
        }

        if (StringUtils.length(apellidos) > 250) {
            throw new DatoMaloException("Los apellidos exceden el largo permitido de 250");
        }

        final LocalDate fechaContratacion = body.getFechaContratacion();
        if (fechaContratacion == null) {
            throw new DatoMaloException("Se debe ingresar una fecha de contratación");
        }

        Empleado emp = administradorEmpleado.consultar(rut);
        if (emp != null) {
            throw new RepetidoException(String.format("Ya existe un empleado con rut %d", rut));
        }

        emp = new Empleado();
        emp.setRut(rut);
        emp.setNombres(nombres);
        emp.setApellidos(apellidos);
        emp.setContratacion(fechaContratacion);
        Empleado creado = administradorEmpleado.guardar(emp);

        return ResponseEntity.status(HttpStatus.CREATED).body(new EmpleadoVO(creado));
    }

    //ACTUALIZACIÓN -> PUT
    @PutMapping(value = "/{rut}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EmpleadoVO> actualizarEmpleadoNombres(@PathVariable("rut") Long rut, @RequestBody EmpleadoVO body) {

        final LocalDate fechaContratacion = body.getFechaContratacion();
        final Long bodyRut = body.getRut();
        if (bodyRut != null && !java.util.Objects.equals(rut, bodyRut)) {
            throw new DatoMaloException("Los ruts no coinciden");
        }

        final String nombres = TextoUtils.normalizar(body.getNombres());
        if (StringUtils.length(nombres) > 250) {
            throw new DatoMaloException("Los nombres exceden el largo permitido de 250");
        }

        final String apellidos = TextoUtils.normalizar(body.getApellidos());
        if (StringUtils.length(apellidos) > 250) {
            throw new DatoMaloException("Los apellidos exceden el largo permitido de 250");
        }

        if (StringUtils.isBlank(nombres) && StringUtils.isBlank(apellidos) && fechaContratacion == null) {
            throw new DatoMaloException("No hay datos para actualizar");
        }

        Empleado emp = administradorEmpleado.consultar(rut);
        if (emp == null) {
            throw new SinDatosException(String.format("No existe un empleado con rut %d", rut));
        }

        if (StringUtils.isNotBlank(nombres)) {
            emp.setNombres(nombres);
        }

        if (StringUtils.isNotBlank(apellidos)) {
            emp.setApellidos(apellidos);
        }

        if (fechaContratacion != null) {
            emp.setContratacion(fechaContratacion);
        }

        emp.setActualizado(LocalDateTime.now());
        Empleado actualizado = administradorEmpleado.guardar(emp);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new EmpleadoVO(actualizado));
    }

    //ELIMINACIÓN -> DELETE
    @DeleteMapping(value = "/{rut}",
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable("rut") Long rut) {
        Empleado empleado = administradorEmpleado.consultar(rut);
        if (empleado == null) {
            throw new SinDatosException(String.format("No se ha encontrado empleado con rut %d", rut));
        }

        administradorEmpleado.eliminar(empleado);
        return ResponseEntity.noContent().build();
    }

}
