package servicios;


// java.sql.*: API estándar JDBC (Connection, DriverManager, SQLException).
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión a la base de datos MySQL de XAMPP (eaab_sgrab).
 *
 * PATRÓN SINGLETON (variante "una sola conexión reutilizable"):
 *  - mantiene UNA instancia de Connection en el atributo estático "cnx";
 *  - si no existe o fue cerrada, la crea con DriverManager.getConnection();
 *  - así toda la aplicación comparte la misma conexión (válido para app de escritorio).
 *
 * BUENAS PRÁCTICAS POO:
 *  - Encapsulamiento: los datos de conexión son privados.
 *  - Bajo acoplamiento: los DAO solo llaman a Conexion.obtener()/cerrar(); no conocen credenciales.
 *  - Configuración centralizada: cambiar la URL es editar esta clase.
 *
 * SEGURIDAD (RNF): en producción real, usuario y contraseña NUNCA van en el código; aquí se
 * usan "root"/"" porque XAMPP trae MySQL sin contraseña por defecto.
 */
public class Conexion {

    // ------------------------------------------------------------------
    // CONFIGURACIÓN (XAMPP): si tu MySQL usa otro puerto, cámbialo aquí.
    // ------------------------------------------------------------------
    // jdbc:mysql://  -> protocolo JDBC de MySQL.
    // localhost:3306 -> servidor y puerto del MySQL de XAMPP.
    // eaab_sgrab     -> nombre de la base de datos creada con el script.
    // useSSL=false   -> en local no hace falta TLS.
    // serverTimezone=America/Bogota -> evita el error "Unknown time zone" con fechas.
    // allowPublicKeyRetrieval=true -> necesario con MySQL 8 en local.
    private static final String URL =
        "jdbc:mysql://localhost:3306/eaab_db"
        + "?useSSL=false"
        + "&serverTimezone=America/Bogota"
        + "&allowPublicKeyRetrieval=true"
        + "&characterEncoding=UTF-8";

    private static final String USUARIO = "root"; // Usuario por defecto de XAMPP.
    private static final String CLAVE   = "";     // XAMPP no exige contraseña por defecto.

    // Atributo estático: única conexión compartida (patrón Singleton).
    private static Connection cnx = null;

    /**
     * Constructor privado: nadie puede hacer "new Conexion()".
     * Todos los métodos son estáticos (de clase), no de instancia.
     */
    private Conexion() {
        // Sin instrucciones: clase de utilidades.
    }

    /**
     * Devuelve la conexión activa, creándola si no existe o si se cerró.
     *
     * @return Connection lista para usarse.
     * @throws SQLException si el servidor no está disponible o las credenciales fallan.
     */
    public static Connection obtener() throws SQLException {
        // isClosed() evita devolver una conexión que ya se cerró (evita "Connection is closed").
        if (cnx == null || cnx.isClosed()) {
            try {
                // Driver moderno de MySQL 8 (el antiguo com.mysql.jdbc.Driver está obsoleto).
                // Class.forName() carga la clase del driver en memoria para que DriverManager lo encuentre.
                Class.forName("com.mysql.cj.jdbc.Driver");

                // getConnection() abre la conexión con la URL, el usuario y la clave.
                cnx = DriverManager.getConnection(URL, USUARIO, CLAVE);
            } catch (ClassNotFoundException e) {
                // Se convierte a SQLException para que el DAO maneje un solo tipo de error.
                // Este error significa: "falta el .jar del conector en el proyecto".
                throw new SQLException("No se encontró el driver de MySQL. "
                        + "Agrega mysql-connector-j-*.jar a las Librerías del proyecto.", e);
            }
        }
        return cnx;
    }

    /**
     * Cierra la conexión (útil al salir de la aplicación o al cambiar de BD).
     * No falla si la conexión ya estaba cerrada.
     */
    public static void cerrar() throws SQLException {
        if (cnx != null && !cnx.isClosed()) {
            cnx.close();
        }
    }
}