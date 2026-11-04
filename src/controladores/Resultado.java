package controladores; // Paquete de los controladores: puente entre las vistas (formularios) y el modelo.

/**
 * Objeto de valor (value object) que comunica a la vista el resultado de una operación del controlador.
 *
 * Buena práctica: el controlador NO propaga las excepciones del modelo hacia la vista; las traduce a un Resultado
 * con un mensaje claro. Así la vista no necesita conocer qué excepciones lanza el dominio (bajo acoplamiento) y
 * muestra siempre mensajes amigables. Es inmutable (todos sus atributos son final): una vez creado no cambia.
 *
 * @param <T> tipo del valor que devuelve la operación cuando tiene éxito (por ejemplo, el Usuario registrado).
 */
public final class Resultado<T> {  // Declaración de la clase Resultado: Objeto de valor (value object) que comunica a la vista el resultado de una operación del controlador

    private final boolean exito;   // true si la operación se completó correctamente.
    private final String mensaje;  // Texto para mostrar al usuario (éxito o motivo del error).
    private final T valor;         // Valor producido por la operación (null cuando hubo error).

    /** Constructor privado: se crea solo con las fábricas estáticas ok() y error(). */
    private Resultado(boolean exito, String mensaje, T valor) {  // Constructor: Constructor privado:
        this.exito = exito;  // Asigna a exito el valor: exito
        this.mensaje = mensaje;  // Asigna a mensaje el valor: mensaje
        this.valor = valor;  // Asigna a valor el valor: valor
    }

    /** Fábrica de un resultado exitoso. */
    public static <T> Resultado<T> ok(T valor, String mensaje) {  // Método ok: Fábrica de un resultado exitoso
        return new Resultado<>(true, mensaje, valor);  // Devuelve: new Resultado<>(true, mensaje, valor)
    }

    /** Fábrica de un resultado fallido. */
    public static <T> Resultado<T> error(String mensaje) {  // Método error: Fábrica de un resultado fallido
        return new Resultado<>(false, mensaje, null);  // Devuelve: new Resultado<>(false, mensaje, nulo)
    }

    /** @return true si la operación tuvo éxito. */
    public boolean isExito() {  // Getter booleano: indica exito
        return exito;  // Devuelve el valor de exito
    }

    /** @return el mensaje para el usuario. */
    public String getMensaje() {  // Getter: devuelve mensaje
        return mensaje;  // Devuelve el valor de mensaje
    }

    /** @return el valor producido (null si hubo error). */
    public T getValor() {  // Getter: devuelve valor
        return valor;  // Devuelve el valor de valor
    }
}
