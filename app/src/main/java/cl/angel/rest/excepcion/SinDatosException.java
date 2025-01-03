package cl.angel.rest.excepcion;

/**
 *
 * @author angelexperti
 */
public class SinDatosException extends RuntimeException {

    public SinDatosException() {
        super("No hay datos para la solicitud");
    }

    public SinDatosException(String message) {
        super(message);
    }

}
