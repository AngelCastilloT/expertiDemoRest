package cl.angel.rest.dominio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author angelexperti
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Angel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}
