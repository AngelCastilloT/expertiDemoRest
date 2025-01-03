package cl.angel.rest.dominio.vo;

import cl.angel.rest.dominio.modelo.Angel;
import cl.angel.rest.dominio.modelo.Empleado;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Objeto que representa los datos de un empleado")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EmpleadoVO extends Angel {

    @Schema(description = "Rut del trabajador", example = "12111111")
    private final Long rut;

    @Schema(description = "Nombres del trabajador", example = "JUAN ALEJANDRO")
    private final String nombres;

    @Schema(description = "Apellidos del trabajador", example = "PERÉZ GONZÁLEZ")
    private final String apellidos;

    @Schema(description = "Fecha de contratación", example = "2023-11-17")
    private final LocalDate fechaContratacion;

    public EmpleadoVO() {
        this.rut = null;
        this.nombres = null;
        this.apellidos = null;
        this.fechaContratacion = null;
    }

    public EmpleadoVO(Long rut, String nombres, String apellidos, LocalDate fechaContratacion) {
        this.rut = rut;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaContratacion = fechaContratacion;
    }

    public EmpleadoVO(Empleado emp) {
        this.rut = emp.getRut();
        this.nombres = emp.getNombres();
        this.apellidos = emp.getApellidos();
        this.fechaContratacion = emp.getContratacion();
    }

    public Long getRut() {
        return rut;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }
}
