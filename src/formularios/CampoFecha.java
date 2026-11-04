package formularios; // Paquete de los formularios.

import java.time.LocalDate;  // Importa java.time.LocalDate
import java.time.format.DateTimeParseException;  // Importa java.time.format.DateTimeParseException
import javax.swing.JTextField;  // Importa javax.swing.JTextField

/**
 * Campo de texto para fechas en formato AAAA-MM-DD que devuelve directamente un LocalDate.
 * Encapsula la conversión y su validación para que los formularios no repitan el código de parseo.
 */
public class CampoFecha extends JTextField {  // Declaración de la clase CampoFecha: Campo de texto para fechas en formato AAAA-MM-DD que devuelve directamente un LocalDate

    /** Crea el campo con una fecha inicial opcional. */
    public CampoFecha(LocalDate inicial) {  // Constructor: Crea el campo con una fecha inicial opcional
        super(10);  // Llama al constructor de la clase base para iniciar la parte heredada
        setToolTipText("Formato AAAA-MM-DD, por ejemplo 2026-03-15");  // Define el texto de ayuda que aparece al dejar el ratón encima
        setFecha(inicial);  // Ejecuta la operación «set fecha» (definida en esta clase o en un objeto colaborador)
    }

    /** @return la fecha escrita, o null si el texto no es una fecha válida. */
    public LocalDate getFecha() {  // Getter: devuelve fecha
        try {  // Bloque protegido: si algo falla, se captura abajo
            return LocalDate.parse(getText().trim()); // Formato ISO: AAAA-MM-DD.
        } catch (DateTimeParseException e) {  // Captura el error de tipo DateTimeParseException e
            return null;  // Devuelve el valor de null
        }
    }

    /** Escribe una fecha en el campo (vacío si es null). */
    public void setFecha(LocalDate fecha) {  // Método setFecha: Escribe una fecha en el campo (vacío si es null)
        setText(fecha == null ? "" : fecha.toString());  // Escribe el texto que muestra el componente
    }
}
