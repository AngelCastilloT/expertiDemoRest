package cl.angel.rest.excepcion;

/**
 *
 * @author angelexperti
 */
public class RepetidoException extends RuntimeException {

    public RepetidoException() {
        super("El valor ya existe en el sistema");
    }

    public RepetidoException(String message) {
        super(message);
    }

}
