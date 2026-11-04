package formularios; // Paquete de los formularios.

import java.math.BigDecimal;  // Importa java.math.BigDecimal
import java.text.NumberFormat;  // Importa java.text.NumberFormat
import java.time.LocalDate;  // Importa java.time.LocalDate
import java.time.LocalDateTime;  // Importa java.time.LocalDateTime
import java.time.format.DateTimeFormatter;  // Importa java.time.format.DateTimeFormatter
import java.util.Locale;  // Importa java.util.Locale

/** Utilidades de presentación: dinero y fechas con el formato colombiano. Clase estática, no se instancia. */
public final class Formato {  // Declaración de la clase Formato: Utilidades de presentación:

    private static final Locale COLOMBIA = new Locale("es", "CO"); // Punto para miles y coma para decimales.
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");  // Atributo «FECHA_HORA» de tipo DateTimeFormatter

    /** Constructor privado: clase de utilidades. */
    private Formato() {  // Constructor: Constructor privado:
    }

    /** @return el valor como moneda: "$ 1.916.731,76". */
    public static String moneda(BigDecimal valor) {  // Método moneda (devuelve String)
        NumberFormat nf = NumberFormat.getNumberInstance(COLOMBIA);  // Declara «nf» (NumberFormat) con el valor: NumberFormat.getNumberInstance(COLOMBIA)
        nf.setMinimumFractionDigits(2);  // Define los decimales mínimos
        nf.setMaximumFractionDigits(2);  // Define los decimales máximos
        return "$ " + nf.format(valor == null ? BigDecimal.ZERO : valor);  // Devuelve: "$ " + nf.format(valor es igual a nulo ? BigDecimal.ZERO : valor)
    }

    /** @return la fecha como AAAA-MM-DD, o guion si es null. */
    public static String fecha(LocalDate fecha) {  // Método fecha (devuelve String)
        return fecha == null ? "-" : fecha.toString();  // Devuelve: fecha es igual a nulo ? "-" : fecha.toString()
    }

    /** @return fecha y hora legibles. */
    public static String fechaHora(LocalDateTime fecha) {  // Método fechaHora (devuelve String)
        return fecha == null ? "-" : fecha.format(FECHA_HORA);  // Devuelve: fecha es igual a nulo ? "-" : fecha.format(FECHA_HORA)
    }
}
