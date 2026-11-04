package modelo; // Paquete del dominio.

import java.time.LocalDate; // LocalDate: fecha sin hora (año-mes-día). Reemplaza a java.util.Date, que es mutable y confuso.
import java.time.Period;    // Period: diferencia entre dos fechas en años, meses y días (sirve para calcular la edad).
import java.util.Objects;   // Objects: utilidades para equals/hashCode seguros frente a null.

/**
 * Clase ABSTRACTA que representa a toda persona registrada en el sistema.
 *
 * PILARES DE LA POO APLICADOS
 *  - Abstracción: modela solo lo esencial de una persona para la EAAB (datos de identificación y contacto).
 *    Es "abstract" porque en el sistema nunca existe una persona "genérica": siempre es un Usuario (suscriptor).
 *  - Encapsulamiento: los atributos NO son públicos; se accede con getters/setters que validan las reglas
 *    del enunciado. Se usa "protected" (diagrama de clases) para que las subclases hereden el acceso directo.
 *  - Herencia: Usuario extiende Persona y reutiliza todos estos datos y validaciones.
 *  - Polimorfismo: obtenerTipoPersona() es un contrato que cada subclase concreta implementa a su manera.
 *
 * REGLAS DE NEGOCIO (enunciado, sección "Las personas y los usuarios del servicio"):
 *  - identificación obligatoria y única (la unicidad la verifica EmpresaServicioPublico al registrar).
 *  - nombre, apellido, dirección y correo son obligatorios.
 *  - fecha de nacimiento NO puede ser futura.
 *  - teléfono con entre 7 y 10 dígitos.
 */
public abstract class Persona {  // Declaración de la clase Persona: Clase ABSTRACTA que representa a toda persona registrada en el sistema

    // ------------------------------------------------------------------ ATRIBUTOS (protected: visibles a subclases)
    protected String identificacion;     // Documento de identidad: obligatorio y único en el sistema.
    protected String nombre;             // Nombres de la persona.
    protected String apellido;           // Apellidos de la persona.
    protected LocalDate fechaNacimiento; // Fecha de nacimiento (no futura).
    protected String direccion;          // Dirección de residencia o de prestación del servicio.
    protected String telefono;           // Teléfono de contacto: 7 a 10 dígitos.
    protected String email;              // Correo electrónico de contacto.

    // ------------------------------------------------------------------ CONSTRUCTORES
    /**
     * Constructor vacío. Lo necesitan las subclases y, más adelante, la capa DAO para crear el objeto
     * y llenarlo con los setters al leer una fila de la base de datos.
     */
    public Persona() {  // Constructor: Constructor vacío
        // Sin instrucciones: los atributos quedan en null hasta que se asignen con los setters.
    }

    /**
     * Constructor completo. Asigna cada dato usando los setters para que TODA asignación pase por
     * las validaciones de negocio (nunca se asigna directamente sin validar).
     *
     * Nota de buena práctica: los setters son "final" para que ninguna subclase los sobrescriba;
     * llamar métodos sobrescribibles desde un constructor es peligroso porque la subclase podría
     * ejecutarse antes de estar completamente construida.
     */
    public Persona(String identificacion, String nombre, String apellido, LocalDate fechaNacimiento,  // Constructor: Constructor completo
                   String direccion, String telefono, String email) {  // Continuación de la instrucción de la línea anterior.
        setIdentificacion(identificacion);     // Valida que no sea nula ni vacía.
        setNombre(nombre);                     // Valida que no sea nulo ni vacío.
        setApellido(apellido);                 // Valida que no sea nulo ni vacío.
        setFechaNacimiento(fechaNacimiento);   // Valida que no sea nula ni futura.
        setDireccion(direccion);               // Valida que no sea nula ni vacía.
        setTelefono(telefono);                 // Valida 7 a 10 dígitos.
        setEmail(email);                       // Valida formato básico de correo.
    }

    // ------------------------------------------------------------------ VALIDADORES ESTÁTICOS (reutilizados por setters y validar*)
    /** Devuelve true si el texto tiene contenido (no es null ni solo espacios). */
    private static boolean tieneTexto(String valor) {  // Método tieneTexto: Devuelve true si el texto tiene contenido (no es null ni solo espacios)
        return valor != null && !valor.trim().isEmpty(); // trim() quita espacios de los extremos; isEmpty() detecta cadena vacía.
    }

    /** Regla de negocio: el teléfono debe tener entre 7 y 10 dígitos numéricos. */
    private static boolean esTelefonoValido(String tel) {  // Método esTelefonoValido: Regla de negocio
        // Expresión regular (regex): "\\d" = un dígito; "{7,10}" = entre 7 y 10 repeticiones; ^ y $ = todo el texto.
        return tel != null && tel.matches("^\\d{7,10}$");  // Devuelve: tel es distinto de nulo y tel.matches("^\\d{7,10}$")
    }

    /** Regla de negocio: la fecha de nacimiento debe existir y no puede ser futura. */
    private static boolean esFechaNacimientoValida(LocalDate fecha) {  // Método esFechaNacimientoValida: Regla de negocio
        return fecha != null && !fecha.isAfter(LocalDate.now()); // isAfter(hoy) == true significa que es futura.
    }

    // ------------------------------------------------------------------ GETTERS Y SETTERS (encapsulamiento)
    /** @return la identificación de la persona. */
    public String getIdentificacion() {  // Getter: devuelve identificacion
        return identificacion; // Lectura simple del atributo.
    }

    /** Asigna la identificación (obligatoria). Es final: ver nota del constructor. */
    public final void setIdentificacion(String id) {  // Método setIdentificacion: Asigna la identificación (obligatoria)
        if (!tieneTexto(id)) { // Regla: identificación obligatoria.
            throw new IllegalArgumentException("La identificación es obligatoria."); // Excepción no verificada: dato inválido.
        }
        this.identificacion = id.trim(); // Se guarda sin espacios sobrantes para que la unicidad sea confiable.
    }

    /** @return el nombre. */
    public String getNombre() {  // Getter: devuelve nombre
        return nombre; // Lectura simple.
    }

    /** Asigna el nombre (obligatorio). */
    public final void setNombre(String nombre) {  // Método setNombre: Asigna el nombre (obligatorio)
        if (!tieneTexto(nombre)) { // Regla: nombre obligatorio.
            throw new IllegalArgumentException("El nombre es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.nombre = nombre.trim(); // Normaliza espacios.
    }

    /** @return el apellido. */
    public String getApellido() {  // Getter: devuelve apellido
        return apellido; // Lectura simple.
    }

    /** Asigna el apellido (obligatorio). */
    public final void setApellido(String apellido) {  // Método setApellido: Asigna el apellido (obligatorio)
        if (!tieneTexto(apellido)) { // Regla: apellido obligatorio.
            throw new IllegalArgumentException("El apellido es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.apellido = apellido.trim(); // Normaliza espacios.
    }

    /** @return la fecha de nacimiento. */
    public LocalDate getFechaNacimiento() {  // Getter: devuelve fecha nacimiento
        return fechaNacimiento; // LocalDate es inmutable, por eso es seguro devolverla directamente.
    }

    /** Asigna la fecha de nacimiento (no futura). */
    public final void setFechaNacimiento(LocalDate fecha) {  // Método setFechaNacimiento: Asigna la fecha de nacimiento (no futura)
        if (!esFechaNacimientoValida(fecha)) { // Regla: la fecha de nacimiento no puede ser futura.
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria y no puede ser futura.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.fechaNacimiento = fecha; // Asignación tras validar.
    }

    /** @return la dirección. */
    public String getDireccion() {  // Getter: devuelve direccion
        return direccion; // Lectura simple.
    }

    /** Asigna la dirección (obligatoria). El enunciado permite editarla (Necesidad 2). */
    public final void setDireccion(String dir) {  // Método setDireccion: Asigna la dirección (obligatoria)
        if (!tieneTexto(dir)) { // Regla: dirección obligatoria.
            throw new IllegalArgumentException("La dirección es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.direccion = dir.trim(); // Normaliza espacios.
    }

    /** @return el teléfono. */
    public String getTelefono() {  // Getter: devuelve telefono
        return telefono; // Lectura simple.
    }

    /** Asigna el teléfono (7 a 10 dígitos). El enunciado permite editarlo (Necesidad 2). */
    public final void setTelefono(String tel) {  // Método setTelefono: Asigna el teléfono (7 a 10 dígitos)
        if (!esTelefonoValido(tel)) { // Regla: teléfono con entre 7 y 10 dígitos.
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 10 dígitos numéricos.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.telefono = tel; // Asignación tras validar.
    }

    /** @return el correo electrónico. */
    public String getEmail() {  // Getter: devuelve email
        return email; // Lectura simple.
    }

    /** Asigna el correo (obligatorio, con formato básico usuario@dominio.ext). El enunciado permite editarlo. */
    public final void setEmail(String email) {  // Método setEmail: Asigna el correo (obligatorio, con formato básico usuario
        // Regex: caracteres permitidos + "@" + dominio + al menos un ".extensión". Es una validación básica, no exhaustiva.
        if (email == null || !email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {  // Condición: si email es igual a nulo o no email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[...
            throw new IllegalArgumentException("El correo electrónico no es válido.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.email = email.trim(); // Normaliza espacios.
    }

    // ------------------------------------------------------------------ COMPORTAMIENTO
    /** @return nombre y apellido unidos, útil para listas y formularios. */
    public String getNombreCompleto() {  // Getter: devuelve nombre completo
        return nombre + " " + apellido; // Concatenación con un espacio.
    }

    /** @return la edad en años cumplidos a la fecha de hoy. */
    public int calcularEdad() {  // Método calcularEdad (devuelve int)
        // Period.between calcula la diferencia calendario exacta; getYears() devuelve solo los años completos.
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();  // Devuelve: Period.between(fechaNacimiento, LocalDate.now()).getYears()
    }

    /** @return true si la persona tiene 18 años o más (mayoría de edad en Colombia). */
    public boolean esMayorDeEdad() {  // Método esMayorDeEdad (devuelve boolean)
        return calcularEdad() >= 18; // La mayoría de edad en Colombia es a los 18 años.
    }

    /** @return true si el teléfono almacenado cumple la regla de 7 a 10 dígitos. */
    public boolean validarTelefono() {  // Método validarTelefono (devuelve boolean)
        return esTelefonoValido(telefono); // Reutiliza el validador estático (una sola fuente de la regla).
    }

    /** @return true si la fecha de nacimiento almacenada existe y no es futura. */
    public boolean validarFechaNacimiento() {  // Método validarFechaNacimiento (devuelve boolean)
        return esFechaNacimientoValida(fechaNacimiento); // Reutiliza el validador estático.
    }

    /**
     * POLIMORFISMO: método abstracto (contrato). No tiene cuerpo; cada subclase concreta
     * está obligada a implementarlo.
     *
     * @return una descripción del tipo de persona (ej.: "Usuario residencial").
     */
    public abstract String obtenerTipoPersona();  // Método obtenerTipoPersona: POLIMORFISMO

    // ------------------------------------------------------------------ IDENTIDAD SEMÁNTICA Y TRAZABILIDAD
    /**
     * Dos personas son "la misma" si tienen la misma identificación (identidad semántica).
     * Por defecto Java compara referencias de memoria (==); aquí se redefine para comparar por el dato de negocio.
     */
    @Override
    public boolean equals(Object obj) {  // Método equals: Dos personas son "la misma" si tienen la misma identificación (identidad semántica)
        if (this == obj) {                    // Si es el mismo objeto en memoria, son iguales.
            return true;  // Devuelve el valor de true
        }
        if (!(obj instanceof Persona)) {      // Si el otro objeto no es una Persona (o es null), no son iguales.
            return false;  // Devuelve el valor de false
        }
        Persona otra = (Persona) obj;         // Conversión (cast) segura gracias al instanceof anterior.
        return Objects.equals(identificacion, otra.identificacion); // Igualdad por identificación.
    }

    /**
     * Contrato de Java: si dos objetos son equals(), deben tener el mismo hashCode().
     * Es indispensable para usar personas en HashSet/HashMap sin duplicados.
     */
    @Override
    public int hashCode() {  // Método hashCode: Contrato de Java
        return Objects.hash(identificacion); // Se calcula con el mismo atributo que usa equals().
    }

    /** @return texto legible para depuración, listas y trazabilidad. */
    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return obtenerTipoPersona() + " [identificación=" + identificacion + ", nombre=" + getNombreCompleto() + "]";  // Devuelve: obtenerTipoPersona() + " [identificación=" + identificacion + ", nombre=...
    }
}
