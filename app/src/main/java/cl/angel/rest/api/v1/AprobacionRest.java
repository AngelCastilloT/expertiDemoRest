package cl.angel.rest.api.v1;

import cl.angel.rest.administrador.AdministradorAprobacion;
import cl.angel.rest.administrador.AdministradorEmpleado;
import cl.angel.rest.dominio.modelo.Aprobacion;
import cl.angel.rest.dominio.modelo.Empleado;
import cl.angel.rest.dominio.vo.AprobacionVO;
import cl.angel.rest.excepcion.DatoMaloException;
import cl.angel.rest.excepcion.RepetidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cl.angel.rest.excepcion.SinDatosException;
import cl.angel.rest.utils.TextoUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/aprobacion",
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class AprobacionRest {
    private final AdministradorAprobacion administradorAprobacion;
    private final AdministradorEmpleado administradorEmpleado;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministradorAprobacion.class);
    
    @Autowired
    public AprobacionRest(AdministradorAprobacion administradorAprobacion, AdministradorEmpleado administradorEmpleado) {
        this.administradorAprobacion = administradorAprobacion;
        this.administradorEmpleado = administradorEmpleado;
    }
    
    @GetMapping(value = "/{rut}",
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<AprobacionVO>> consultarAprobacionPorRut(@PathVariable("rut") Long rut) {
        Empleado empleado = administradorEmpleado.consultar(rut);
        if (empleado == null) {
            throw new SinDatosException(String.format("No se ha encontrado empleado con rut %d", rut));
        }
        LOGGER.info("Encontré el empleado {}", empleado);
        
        List<Aprobacion> apr = administradorAprobacion.consultar(empleado);
        if(apr == null || apr.isEmpty()){
            throw new SinDatosException(String.format("No se han encontrado solicitudes de vacaciones con rut %d", rut));
        }
        
        //Convierte las Aprobaciones en AprobacionesVO
        List<AprobacionVO> aprVO = apr.stream().map(AprobacionVO::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(aprVO);
    }
    
    //CREACIÓN -> POST
    @PostMapping(value = "/{rut}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AprobacionVO> crearAprobacion(@PathVariable("rut") Long rut, @RequestBody AprobacionVO body) {
        
        final String motivo = TextoUtils.normalizar(body.getMotivo());
        if (StringUtils.isBlank(motivo)) {
            throw new DatoMaloException("El motivo no puede ser vacíos");
        }

        if (StringUtils.length(motivo) > 250) {
            throw new DatoMaloException("El motivo excede el largo permitido de 250");
        }

        Empleado emp = administradorEmpleado.consultar(rut);
        if (emp == null) {
            throw new SinDatosException(String.format("No se ha encontrado empleado con rut %d", rut));
        }
        
        Aprobacion apr = administradorAprobacion.consultar(motivo, emp);
        if( apr != null){
            throw new RepetidoException(String.format("El empleado con rut %d ya solicitó vacaciones con el motivo %s", rut, motivo));
        }

        apr = new Aprobacion();
        apr.setEmpleado(emp);
        apr.setMotivo(motivo);
        apr.setAceptado(false);
        Aprobacion creada = administradorAprobacion.guardar(apr);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AprobacionVO(creada));
    }
    
    //ACTUALIZACIÓN -> PUT
    @PutMapping(value = "/{rut}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AprobacionVO> actualizarAprobacion(@PathVariable("rut") Long rut, @RequestParam("motivoBusqueda") String motivoBusqueda ,@RequestBody AprobacionVO body) {
        
        String motivo = TextoUtils.normalizar(body.getMotivo());
        if (StringUtils.isBlank(motivo)) {
            throw new DatoMaloException("No hay datos para actualizar");
        }
        if (StringUtils.length(motivo) > 250) {
            throw new DatoMaloException("El motivo excede el largo permitido de 250");
        }

        Empleado emp = administradorEmpleado.consultar(rut);
        if (emp == null) {
            throw new SinDatosException(String.format("No existe un empleado con rut %d", rut));
        }

        Aprobacion apr = administradorAprobacion.consultar(motivoBusqueda, emp);
        if(apr == null){
            throw new SinDatosException(String.format("No existe una solicitud de vacacion del empleado con rut %d y con motivo %s", rut, motivo));
        }
        
        apr.setAceptado(body.isAceptado());
        apr.setActualizado(LocalDateTime.now());
        
        if (StringUtils.isNotBlank(motivo)) {
            apr.setMotivo(motivo);
        }

        Aprobacion actualizado = administradorAprobacion.guardar(apr);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new AprobacionVO(actualizado));
    }
    
    //ELIMINACIÓN -> DELETE
    @DeleteMapping(value = "/{rut}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> eliminarAprobacion(@PathVariable("rut") Long rut, @RequestBody AprobacionVO body) {
        String motivo = body.getMotivo();
        if(motivo == null || motivo.trim().isEmpty()){
            throw new IllegalArgumentException("El motivo no puede ser nulo o vacio");
        }
        
        Empleado empleado = administradorEmpleado.consultar(rut);
        if (empleado == null) {
            throw new SinDatosException(String.format("No se ha encontrado empleado con rut %d", rut));
        }
        
        Aprobacion apr = administradorAprobacion.consultar(motivo, empleado);
        if(apr == null){
            throw new SinDatosException(String.format("No hay solicitudes asociadas al empleado con rut %d", rut));
        }
        administradorAprobacion.eliminar(apr);
        return ResponseEntity.noContent().build();
    }
}
