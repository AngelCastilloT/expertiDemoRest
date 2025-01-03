package cl.angel.rest.api;

import cl.angel.rest.excepcion.DatoMaloException;
import cl.angel.rest.excepcion.RepetidoException;
import cl.angel.rest.excepcion.SinDatosException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author angelexperti
 */
@RestControllerAdvice
public class ManejadorExcepcion {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManejadorExcepcion.class);

    private ProblemDetail makeProblemDetail(HttpServletRequest request, HttpStatus status, Exception e) {
        final URI type = URI.create("https://developer.mozilla.org/es/docs/Web/HTTP/Status/" + status.value());
        final String detail = StringUtils.trimToEmpty(e.getLocalizedMessage());

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(status, detail);
        pb.setType(type);
        pb.setTitle(status.getReasonPhrase());
        if (request != null) {
            pb.setInstance(URI.create(request.getRequestURI()));
        }
        return pb;
    }

    @ExceptionHandler({RepetidoException.class})
    public ResponseEntity<ProblemDetail> malaCosa(HttpServletRequest request, RepetidoException e) {
        LOGGER.error("Información repetida: {}", e.getLocalizedMessage());
        LOGGER.debug("Información repetida: {}", e.getMessage(), e);

        final HttpStatus status = HttpStatus.CONFLICT;
        final ProblemDetail error = makeProblemDetail(request, status, e);

        return ResponseEntity.status(status).body(error);
    }
    
    @ExceptionHandler({DatoMaloException.class})
    public ResponseEntity<ProblemDetail> malaCosa(HttpServletRequest request, DatoMaloException e) {
        LOGGER.error("Error de sintaxis: {}", e.getLocalizedMessage());
        LOGGER.debug("Error de sintaxis: {}", e.getMessage(), e);

        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final ProblemDetail error = makeProblemDetail(request, status, e);

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({SinDatosException.class})
    public ResponseEntity<ProblemDetail> malaCosa(HttpServletRequest request, SinDatosException e) {
        LOGGER.error("No hay datos para la solicitud: {}", e.getLocalizedMessage());
        LOGGER.debug("No hay datos para la solicitud: {}", e.getMessage(), e);

        final HttpStatus status = HttpStatus.NOT_FOUND;
        final ProblemDetail error = makeProblemDetail(request, status, e);

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ProblemDetail> malaCosa(HttpServletRequest request, Exception e) {
        LOGGER.error("Ha sucedido un error desconocido: {}", e.getLocalizedMessage());
        LOGGER.debug("Ha sucedido un error desconocido: {}", e.getMessage(), e);

        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final ProblemDetail error = makeProblemDetail(request, status, e);

        return ResponseEntity.status(status).body(error);
    }
}
