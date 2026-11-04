package controladores; // Paquete de los controladores.

import java.time.LocalDateTime;  // Importa java.time.LocalDateTime
import java.time.format.DateTimeFormatter;  // Importa java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger;  // Importa java.util.concurrent.atomic.AtomicInteger
import java.util.function.Supplier; // Interfaz funcional: representa "una operación que devuelve un valor".

/**
 * Clase base abstracta de todos los controladores (patrón Template Method).
 *
 * Centraliza en UN solo lugar el manejo de errores (principio DRY: no repetirse): cada controlador concreto solo
 * escribe la operación de negocio y esta clase se encarga de ejecutarla, capturar las excepciones del modelo
 * (IllegalArgumentException por datos inválidos, IllegalStateException por operaciones no permitidas) y
 * convertirlas en un Resultado con un mensaje para el usuario.
 */
public abstract class ControladorBase {  // Declaración de la clase ControladorBase: Clase base abstracta de todos los controladores (patrón Template Method)

    private static final AtomicInteger CONSECUTIVO = new AtomicInteger(); // Contador seguro entre hilos para códigos únicos.

    protected final ContextoSgrab contexto; // Acceso a empresa, regulador, gestor y fábrica (inyectado por constructor).

    /** Inyección de dependencias por constructor: el controlador recibe lo que necesita, no lo busca por su cuenta. */
    protected ControladorBase(ContextoSgrab contexto) {  // Constructor: Inyección de dependencias por constructor:
        if (contexto == null) { // Sin contexto el controlador no puede operar.
            throw new IllegalArgumentException("El contexto de la aplicación es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.contexto = contexto;  // Asigna a contexto el valor: contexto
    }

    /**
     * Ejecuta una operación del dominio y traduce su desenlace a un Resultado.
     *
     * @param operacion lógica de negocio a ejecutar (se pasa como expresión lambda).
     * @param mensajeExito texto que verá el usuario si todo sale bien.
     */
    protected <T> Resultado<T> ejecutar(Supplier<T> operacion, String mensajeExito) {  // Método ejecutar: Ejecuta una operación del dominio y traduce su desenlace a un Resultado
        try {  // Bloque protegido: si algo falla, se captura abajo
            T valor = operacion.get(); // Ejecuta la operación del modelo.
            return Resultado.ok(valor, mensajeExito);  // Devuelve: Resultado.ok(valor, mensajeExito)
        } catch (IllegalArgumentException | IllegalStateException e) { // Reglas de negocio incumplidas.
            return Resultado.error(e.getMessage());  // Devuelve: Resultado.error(e.getMessage())
        } catch (RuntimeException e) { // Cualquier otro fallo (por ejemplo, error de disco al exportar).
            return Resultado.error("Ocurrió un error inesperado: " + e.getMessage());  // Devuelve: Resultado.error("Ocurrió un error inesperado: " + e.getMessage())
        }
    }

    /**
     * Genera un código sugerido y único (prefijo + fecha y hora + consecutivo). Ahorra digitación al funcionario
     * y evita códigos repetidos; el usuario puede reemplazarlo si lo desea.
     */
    public static String generarCodigo(String prefijo) {  // Método generarCodigo: Genera un código sugerido y único (prefijo + fecha y hora + consecutivo)
        String marca = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")); // Fecha y hora compactas.
        return prefijo + "-" + marca + String.format("%02d", CONSECUTIVO.incrementAndGet() % 100); // Consecutivo de dos cifras.
    }
}
