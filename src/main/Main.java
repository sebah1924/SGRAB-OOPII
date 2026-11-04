package main; // Paquete del punto de entrada de la aplicación.

import controladores.ContextoSgrab;  // Importa controladores.ContextoSgrab
import formularios.Estilo;  // Importa formularios.Estilo
import formularios.FrmPrincipal;  // Importa formularios.FrmPrincipal
import javax.swing.SwingUtilities;  // Importa javax.swing.SwingUtilities

/**
 * Punto de entrada de SGRAB. Su única responsabilidad es arrancar la aplicación: instalar el aspecto visual,
 * crear el contexto (raíz de composición) y abrir la ventana principal.
 *
 * Todo lo que toca componentes Swing se ejecuta en el "hilo de eventos" (EDT) mediante invokeLater, que es la
 * regla de oro de Swing: así la interfaz nunca se bloquea ni se corrompe por accesos desde otros hilos.
 *
 * Uso: sin argumentos arranca el sistema vacío; con el argumento "--demo" carga además los datos de demostración.
 */
public class Main {  // Declaración de la clase Main: Punto de entrada de SGRAB

    /** Método de arranque de Java. */
    public static void main(String[] args) {  // Método main: Método de arranque de Java
        boolean demostracion = args.length > 0 && "--demo".equals(args[0]); // ¿Se pidió cargar datos de ejemplo?
        SwingUtilities.invokeLater(() -> {  // Ejecuta la operación «invoke later» (definida en esta clase o en un objeto colaborador)
            Estilo.instalar();                                      // Aspecto visual de toda la aplicación.
            ContextoSgrab contexto = ContextoSgrab.getInstancia();  // Crea empresa, regulador, gestor y fábrica.
            if (demostracion) {  // Condición: si demostracion
                contexto.cargarDatosDemostracion();  // Ejecuta la operación «cargar datos demostracion» (definida en esta clase o en un objeto colaborador)
            }
            new FrmPrincipal(contexto).setVisible(true);            // Abre la ventana principal.
        });  // Cierra la llamada y el bloque (manejador o expresión lambda) abierto en las líneas anteriores
    }
}
