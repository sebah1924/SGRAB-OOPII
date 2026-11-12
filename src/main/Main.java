package main;
 // Paquete raíz del proyecto

import servicios.Conexion; // Importa el Singleton de conexión
import vista.MenuPrincipal; // Importa la ventana principal (Menú)

import javax.swing.*; // Componentes Swing (Ventanas, paneles, botones)
import java.awt.*;    // Clases gráficas (Dimensiones, Colores)
import java.sql.Connection; // Interfaz de conexión JDBC
import java.sql.SQLException; // Excepciones de SQL

/**
 * Clase principal que arranca todo el sistema.
 * Verifica la conexión, configura la apariencia y lanza el menú principal.
 */
public class Main {

    public static void main(String[] args) {
        
        // 1. Configurar la apariencia (Look & Feel) para que se vea como en Windows
        try {
            // Intenta usar el estilo nativo del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla (ej. en Linux), usa el estilo Nimbus por defecto
            System.err.println("No se pudo cargar el Look & Feel del sistema. Usando por defecto.");
        }

        // 2. Configurar el manejo global de excepciones (para que no se cierre la app de golpe)
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Ocurrió un error inesperado:\n" + e.getMessage(), 
                "Error del Sistema", 
                JOptionPane.ERROR_MESSAGE);
        });

        // 3. Verificar que la base de datos esté conectada ANTES de abrir la interfaz
        if (!verificarConexionBaseDatos()) {
            // Si falla, mostramos un mensaje claro y cerramos la aplicación
            JOptionPane.showMessageDialog(null, 
                "❌ No se pudo conectar a la base de datos MySQL.\n\n" +
                "Verifica lo siguiente:\n" +
                "1. Que XAMPP esté encendido (MySQL en verde).\n" +
                "2. Que hayas creado la base de datos 'eaab_db' en phpMyAdmin.\n" +
                "3. Que el archivo Conexion.java tenga la URL correcta.", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Cierra el programa con código de error
        }

        // 4. Lanzar la interfaz gráfica (Menú Principal) en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Creamos y mostramos el menú principal
                MenuPrincipal menu = new MenuPrincipal();
                menu.setVisible(true); // Lo hace visible
                menu.setLocationRelativeTo(null); // Lo centra en la pantalla
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Error al iniciar la interfaz: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    /**
     * Método auxiliar para verificar la conexión a la base de datos.
     * Usa un solo catch (Exception) para evitar errores de compilación.
     * @return true si la conexión es exitosa, false si falla.
     */
    private static boolean verificarConexionBaseDatos() {
        try {
            // Intenta obtener la conexión usando el Singleton
            Connection c = Conexion.obtener();
            
            // Si no es nula y está abierta, todo está OK
            if (c != null && !c.isClosed()) {
                System.out.println("✔ Conexión a MySQL establecida correctamente.");
                System.out.println("  URL: " + c.getMetaData().getURL());
                return true;
            }
        } catch (Exception ex) {
            // Si ocurre cualquier error (SQLException, ClassNotFoundException, etc.)
            System.err.println("✘ Error al conectar: " + ex.getMessage());
            
            // Imprimimos un mensaje personalizado según el tipo de error
            if (ex instanceof SQLException) {
                System.err.println("  Causa: Error de SQL. Revisa XAMPP y el nombre de la BD.");
            } else if (ex instanceof ClassNotFoundException) {
                System.err.println("  Causa: Falta el Driver JDBC de MySQL en las librerías.");
            }
        }
        return false; // Si falla, retorna falso
    }
}