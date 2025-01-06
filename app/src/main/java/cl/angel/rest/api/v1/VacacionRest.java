package cl.angel.rest.api.v1;

import cl.angel.rest.administrador.AdministradorAprobacion;
import cl.angel.rest.administrador.AdministradorEmpleado;
import cl.angel.rest.administrador.AdministradorVacacion;
import cl.angel.rest.dominio.modelo.Aprobacion;
import cl.angel.rest.dominio.modelo.Empleado;
import cl.angel.rest.dominio.modelo.Vacacion;
import cl.angel.rest.dominio.vo.AprobacionVO;
import cl.angel.rest.dominio.vo.VacacionVO;
import cl.angel.rest.excepcion.DatoMaloException;
import cl.angel.rest.excepcion.RepetidoException;
import cl.angel.rest.excepcion.SinDatosException;
import cl.angel.rest.utils.TextoUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/vacacion",
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class VacacionRest {
    private final AdministradorVacacion administradorVacacion;
    private final AdministradorAprobacion administradorAprobacion;
    private final AdministradorEmpleado administradorEmpleado;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministradorVacacion.class);
    
    @Autowired
    public VacacionRest(AdministradorVacacion administradorVacacion, AdministradorAprobacion administradorAprobacion, AdministradorEmpleado administradorEmpleado){
        this.administradorVacacion = administradorVacacion;
        this.administradorAprobacion = administradorAprobacion;
        this.administradorEmpleado = administradorEmpleado;
    }
    
    @GetMapping(value = "/{rut}",
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VacacionVO>> consultarVacacionPorRut(@PathVariable("rut") Long rut){
        Empleado empleado = administradorEmpleado.consultar(rut);
        if (empleado == null) {
            throw new SinDatosException(String.format("No se ha encontrado empleado con rut %d", rut));
        }
        LOGGER.info("Encontré el empleado {}", empleado);
       
        List<Vacacion> vac = administradorVacacion.consultar(empleado);
        //Convierte las Aprobaciones en AprobacionesVO
        List<VacacionVO> vacVO = vac.stream().map(VacacionVO::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(vacVO);
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
        
        List <Vacacion> vac = administradorVacacion.consultar(motivo, empleado);
        for(Vacacion i : vac){
            administradorVacacion.eliminar(i);
        }
        
        return ResponseEntity.noContent().build();
    }

}
