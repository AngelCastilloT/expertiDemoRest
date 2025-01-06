package cl.angel.rest.dominio.vo;
import cl.angel.rest.dominio.modelo.Angel;
import cl.angel.rest.dominio.modelo.Aprobacion;
import cl.angel.rest.dominio.modelo.Vacacion;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Objeto que representa los datos de una vacación")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VacacionVO extends Angel{
    
    @Schema(description = "Aprobacion de la vacación", example = "1")
    private final Aprobacion aprobacion;
    
    @Schema(description = "Fecha del día libre", example = "2025-02-03")
    private final LocalDate fechaLibre;
    
    public VacacionVO(){
        this.aprobacion = null;
        this.fechaLibre = null;
    }
    
    public VacacionVO(Aprobacion aprobacion, LocalDate fechaLibre) {
        this.aprobacion = aprobacion;
        this.fechaLibre = fechaLibre;
    }
    
    public VacacionVO(Vacacion vac) {
        this.aprobacion = vac.getAprobacion();
        this.fechaLibre = vac.getFechaLibre();
    }
    
    public Aprobacion getAprobacion(){
        return  aprobacion;
    }
    
    public LocalDate getfechaLibre(){
        return fechaLibre;
    }
    
}
