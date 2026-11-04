package modelo; // Paquete del dominio.

/**
 * Excepción legal al racionamiento: protege a ciertos usuarios de los cortes y suspensiones.
 *
 * REGLAS DE NEGOCIO (Necesidad 4, Decreto 137 de 2024):
 *  - Código obligatorio y único (la unicidad la verifica EmpresaServicioPublico.registrarExcepcion).
 *  - La justificación legal debe explicar por qué la categoría está exenta (ej.: "protección del derecho a la salud").
 *  - Si el usuario está en zona de racionamiento pero goza de excepción, NO se le aplica el corte.
 *  - Un usuario exento que derrocha agua puede recibir sanción económica, pero NUNCA suspensión.
 *
 * Encapsulamiento: atributos privados con acceso controlado por getters/setters validados.
 */
public class Excepcion {  // Declaración de la clase Excepcion: Excepción legal al racionamiento

    private String codigo;                      // Identificador único de la excepción.
    private String descripcion;                 // Texto que describe la excepción.
    private CategoriaExenta categoriaExenta;    // Categoría protegida (hospital, colegio, etc.).
    private String justificacion;               // Fundamento legal que explica la exención.
    private boolean esProteccionConstitucional; // true si corresponde a una protección de rango constitucional.

    /** Constructor vacío (útil para la capa DAO al reconstruir el objeto desde la base de datos). */
    public Excepcion() {  // Constructor: Constructor vacío (útil para la capa DAO al reconstruir el objeto desde la base de datos)
        // Sin instrucciones: atributos en su valor por defecto.
    }

    /** Constructor completo: cada dato pasa por su setter para aplicar las validaciones. */
    public Excepcion(String codigo, String descripcion, CategoriaExenta categoria, String justificacion,  // Constructor: Constructor completo
                     boolean esProteccion) {  // Continuación de la instrucción de la línea anterior.
        setCodigo(codigo);                      // Valida código obligatorio.
        setDescripcion(descripcion);            // Valida descripción obligatoria.
        setCategoriaExenta(categoria);          // Valida categoría obligatoria.
        setJustificacion(justificacion);        // Valida justificación obligatoria.
        setEsProteccionConstitucional(esProteccion); // Asigna la marca constitucional.
    }

    /** @return el código de la excepción. */
    public String getCodigo() {  // Getter: devuelve codigo
        return codigo; // Lectura simple.
    }

    /** Asigna el código (obligatorio). */
    public void setCodigo(String codigo) {  // Método setCodigo: Asigna el código (obligatorio)
        if (codigo == null || codigo.trim().isEmpty()) { // Regla: código obligatorio.
            throw new IllegalArgumentException("El código de la excepción es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.codigo = codigo.trim(); // Normaliza espacios.
    }

    /** @return la descripción. */
    public String getDescripcion() {  // Getter: devuelve descripcion
        return descripcion; // Lectura simple.
    }

    /** Asigna la descripción (obligatoria). */
    public void setDescripcion(String desc) {  // Método setDescripcion: Asigna la descripción (obligatoria)
        if (desc == null || desc.trim().isEmpty()) { // Regla: toda excepción se describe.
            throw new IllegalArgumentException("La descripción de la excepción es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.descripcion = desc.trim(); // Normaliza espacios.
    }

    /** @return la categoría exenta. */
    public CategoriaExenta getCategoriaExenta() {  // Getter: devuelve categoria exenta
        return categoriaExenta; // Lectura simple.
    }

    /** Asigna la categoría exenta (obligatoria). */
    public void setCategoriaExenta(CategoriaExenta cat) {  // Método setCategoriaExenta: Asigna la categoría exenta (obligatoria)
        if (cat == null) { // Regla: toda excepción pertenece a una categoría.
            throw new IllegalArgumentException("La categoría exenta es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.categoriaExenta = cat; // Asignación tras validar.
    }

    /** @return la justificación legal. */
    public String getJustificacion() {  // Getter: devuelve justificacion
        return justificacion; // Lectura simple.
    }

    /** Asigna la justificación legal (obligatoria: debe explicar por qué la categoría está exenta). */
    public void setJustificacion(String just) {  // Método setJustificacion: Asigna la justificación legal (obligatoria
        if (just == null || just.trim().isEmpty()) { // Regla: justificación obligatoria.
            throw new IllegalArgumentException("La justificación legal es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.justificacion = just.trim(); // Normaliza espacios.
    }

    /** @return true si es una protección constitucional. (El nombre "isEs..." respeta el diagrama de clases.) */
    public boolean isEsProteccionConstitucional() {  // Getter booleano: indica es proteccion constitucional
        return esProteccionConstitucional; // Los getters de boolean usan el prefijo "is" por convención JavaBeans.
    }

    /** Asigna la marca de protección constitucional. */
    public void setEsProteccionConstitucional(boolean esProt) {  // Método setEsProteccionConstitucional: Asigna la marca de protección constitucional
        this.esProteccionConstitucional = esProt; // boolean no requiere validación: cualquier valor es legal.
    }

    /**
     * Indica si esta excepción protege al usuario indicado.
     * Regla: una excepción de categoría USUARIO_OFICIAL solo aplica a usuarios de tipo OFICIAL;
     * las demás categorías (hospital, colegio...) se asignan por la entidad a cualquier usuario.
     */
    public boolean esAplicableA(Usuario usuario) {  // Método esAplicableA: Indica si esta excepción protege al usuario indicado
        if (usuario == null) { // Sin usuario no hay a quién aplicar.
            return false;  // Devuelve el valor de false
        }
        if (categoriaExenta == CategoriaExenta.USUARIO_OFICIAL) { // Caso especial de coherencia con el tipo de usuario.
            return usuario.getTipoUsuario() == TipoUsuario.OFICIAL;  // Devuelve: usuario.getTipoUsuario() es igual a TipoUsuario.OFICIAL
        }
        return true; // El resto de categorías protege al usuario al que fueron asignadas.
    }

    /**
     * Determina por la categoría si la exención tiene rango de protección constitucional.
     * Según el enunciado, salud, derechos de internos, niñez y personas en debilidad manifiesta
     * son protecciones constitucionales; la de los usuarios oficiales es de continuidad del servicio del Estado.
     */
    public boolean determinarProteccionConstitucional() {  // Método determinarProteccionConstitucional: Determina por la categoría si la exención tiene rango de protección constitucional
        return categoriaExenta != null && categoriaExenta != CategoriaExenta.USUARIO_OFICIAL; // Todas menos la oficial.
    }

    /** @return true si la justificación legal no está vacía (debe explicar la exención). */
    public boolean validarJustificacion() {  // Método validarJustificacion (devuelve boolean)
        return justificacion != null && !justificacion.trim().isEmpty(); // Texto presente.
    }

    /** @return el fundamento legal resumido según la categoría (Decreto 137 de 2024 y jurisprudencia). */
    public String obtenerFundamentoLegal() {  // Método obtenerFundamentoLegal (devuelve String)
        if (categoriaExenta == null) { // Sin categoría no hay fundamento que mostrar.
            return "Sin categoría asignada.";  // Devuelve: "Sin categoría asignada."
        }
        switch (categoriaExenta) { // Un switch sobre enum: el compilador avisa si falta algún caso.
            case HOSPITAL:  // Caso HOSPITAL
            case CENTRO_SALUD:  // Caso CENTRO_SALUD
                return "Decreto 137 de 2024 - Protección del derecho a la salud.";  // Devuelve: "Decreto 137 de 2024 - Protección del derecho a la salud."
            case CENTRO_PENITENCIARIO:  // Caso CENTRO_PENITENCIARIO
                return "Decreto 137 de 2024 - Protección de los derechos humanos de los internos.";  // Devuelve: "Decreto 137 de 2024 - Protección de los derechos humanos de los internos."
            case INSTITUCION_EDUCATIVA:  // Caso INSTITUCION_EDUCATIVA
                return "Decreto 137 de 2024 - Protección de la niñez y la adolescencia.";  // Devuelve: "Decreto 137 de 2024 - Protección de la niñez y la adolescencia."
            case ESPECIAL_PROTECCION_CONSTITUCIONAL:  // Caso ESPECIAL_PROTECCION_CONSTITUCIONAL
                return "Jurisprudencia de la Corte Constitucional - Personas en situación de debilidad manifiesta.";  // Devuelve: "Jurisprudencia de la Corte Constitucional - Personas en situación de de...
            case USUARIO_OFICIAL:  // Caso USUARIO_OFICIAL
                return "Decreto 137 de 2024 - Continuidad de los servicios esenciales del Estado.";  // Devuelve: "Decreto 137 de 2024 - Continuidad de los servicios esenciales del Estado."
            default:  // Caso por defecto: cualquier otro valor
                return "Sin fundamento definido."; // Rama de seguridad para valores futuros del enum.
        }
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Excepcion [codigo=" + codigo + ", categoria=" + categoriaExenta  // Devuelve: "Excepcion [codigo=" + codigo + ", categoria=" + categoriaExenta
                + ", proteccionConstitucional=" + esProteccionConstitucional + "]"; // Resumen para trazabilidad.
    }
}
