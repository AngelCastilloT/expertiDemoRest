/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.angel.rest.dominio.vo;

import cl.angel.rest.dominio.modelo.Angel;
import cl.angel.rest.dominio.modelo.Empleado;
import cl.angel.rest.dominio.modelo.Aprobacion;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author angelexperti
 */
public class AprobacionVO extends Angel {
    @Schema(description = "Empleado de la aprobación", example = "12111111")
    private final Empleado empleado;

    @Schema(description = "Motivo de las vacaciones solicitadas", example = "DESCANSAR")
    private final String motivo;

    @Schema(description = "Aprobación de las vacaciones solicitadas", example = "true")
    private final Boolean aceptado;
    
    public AprobacionVO() {
        this.empleado = null;
        this.motivo = null;
        this.aceptado = false;
    }

    public AprobacionVO(Empleado empleado, String motivo, Boolean aceptado) {
        this.empleado = empleado;
        this.motivo = motivo;
        this.aceptado = aceptado;
    }

    public AprobacionVO(Aprobacion apr) {
        this.empleado = apr.getEmpleado();
        this.motivo = apr.getMotivo();
        this.aceptado = apr.isAceptado();
    }
    
    public Empleado empleado() {
        return empleado;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isAceptado() {
        return aceptado;
    }
    
    
}
