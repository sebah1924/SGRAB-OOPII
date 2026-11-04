package controladores; // Paquete de los controladores.

import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import modelo.*;  // Importa modelo.*

/** Controlador de las excepciones legales al racionamiento (RF-12 y RF-13). */
public class ControladorExcepciones extends ControladorBase {  // Declaración de la clase ControladorExcepciones: Controlador de las excepciones legales al racionamiento (RF-12 y RF-13)

    /** Inyecta el contexto. */
    public ControladorExcepciones(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** Registra una excepción; el carácter constitucional se deriva de la categoría (regla del modelo). */
    public Resultado<Excepcion> registrar(String codigo, String descripcion, CategoriaExenta categoria, String justificacion) {  // Método registrar: Registra una excepción; el carácter constitucional se deriva de la categoría (regla del modelo)
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            Excepcion excepcion = new Excepcion(codigo, descripcion, categoria, justificacion, false);  // Declara «excepcion» (Excepcion) con el valor: new Excepcion(codigo, descripcion, categoria, j...
            excepcion.setEsProteccionConstitucional(excepcion.determinarProteccionConstitucional()); // Regla del modelo.
            contexto.getEmpresa().registrarExcepcion(excepcion);  // Ejecuta la operación «registrar excepcion» (definida en esta clase o en un objeto colaborador)
            return excepcion;  // Devuelve el valor de excepcion
        }, "Excepción registrada.");  // Ejecuta: }, "Excepción registrada.")
    }

    /** Asigna la excepción a un usuario si le es aplicable. */
    public Resultado<Usuario> asignar(Usuario usuario, Excepcion excepcion) {  // Método asignar: Asigna la excepción a un usuario si le es aplicable
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            if (!excepcion.esAplicableA(usuario)) { // Una excepción oficial solo protege a usuarios oficiales.
                throw new IllegalStateException("Esta excepción no es aplicable al tipo de usuario seleccionado.");  // Lanza IllegalStateException con un mensaje claro para el usuario
            }
            usuario.setExcepcion(excepcion);  // Ejecuta la operación «set excepcion» (definida en esta clase o en un objeto colaborador)
            return usuario;  // Devuelve el valor de usuario
        }, "Excepción asignada: el usuario queda protegido de cortes y suspensiones.");  // Ejecuta: }, "Excepción asignada: el usuario queda protegido de cor...
    }

    /** Retira la excepción de un usuario. */
    public Resultado<Usuario> quitar(Usuario usuario) {  // Método quitar: Retira la excepción de un usuario
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            usuario.setExcepcion(null);  // Ejecuta la operación «set excepcion» (definida en esta clase o en un objeto colaborador)
            return usuario;  // Devuelve el valor de usuario
        }, "Excepción retirada.");  // Ejecuta: }, "Excepción retirada.")
    }

    /** @return el catálogo de excepciones. */
    public List<Excepcion> listar() {  // Método listar (devuelve List<Excepcion>)
        return new ArrayList<>(contexto.getEmpresa().getListaExcepciones());  // Devuelve: new ArrayList<>(contexto.getEmpresa().getListaExcepciones())
    }
}
