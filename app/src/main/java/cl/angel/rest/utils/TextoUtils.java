package cl.angel.rest.utils;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author angelexperti
 */
public class TextoUtils {

    public static String normalizar(final String texto) {
        String resultado = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(texto)) {
            resultado = StringUtils.upperCase(StringUtils.normalizeSpace(StringUtils.trimToEmpty(texto)));
        }
        return resultado;
    }

}
