package cl.angel.rest.excepcion;

/**
 *
 * @author angelexperti
 */
public class DatoMaloException extends RuntimeException {

    public DatoMaloException() {
        super("Los datos proporcionados son incorrectos");
    }

    public DatoMaloException(String message) {
        super(message);
    }

}
