package modelo; // Paquete del dominio.

import java.math.BigDecimal; // Dinero con precisión exacta.
import java.time.LocalDate;  // Fechas sin hora.
import java.util.Map;        // Mapa clave-valor con los datos del usuario a crear.

/**
 * PATRÓN FACTORY (Fábrica).
 *
 * Contexto: Factory centraliza la CREACIÓN de objetos. Quien necesita un usuario (p. ej. un formulario) solo
 * dice qué tipo quiere y entrega sus datos; la fábrica decide qué subclase instanciar, valida los datos
 * comunes y asigna AUTOMÁTICAMENTE la estrategia tarifaria correspondiente (Necesidad 1).
 * Así el resto del sistema no depende de las clases concretas (bajo acoplamiento) y agregar un tipo
 * nuevo de usuario solo exige modificar este punto (extensibilidad).
 *
 * Los datos llegan en un Map<String, Object>; las claves esperadas están definidas como constantes públicas
 * para evitar errores de digitación en los formularios y controladores.
 */
public class UsuarioFactory {  // Declaración de la clase UsuarioFactory: PATRÓN FACTORY (Fábrica)

    // Claves comunes a todos los tipos de usuario.
    public static final String IDENTIFICACION = "identificacion";  // Atributo «IDENTIFICACION» de tipo String
    public static final String NOMBRE = "nombre";  // Atributo «NOMBRE» de tipo String
    public static final String APELLIDO = "apellido";  // Atributo «APELLIDO» de tipo String
    public static final String FECHA_NACIMIENTO = "fechaNacimiento";      // LocalDate
    public static final String DIRECCION = "direccion";  // Atributo «DIRECCION» de tipo String
    public static final String TELEFONO = "telefono";  // Atributo «TELEFONO» de tipo String
    public static final String EMAIL = "email";  // Atributo «EMAIL» de tipo String
    public static final String CODIGO_SUSCRIPTOR = "codigoSuscriptor";  // Atributo «CODIGO_SUSCRIPTOR» de tipo String
    public static final String FECHA_REGISTRO = "fechaRegistro";          // LocalDate
    public static final String ESTRATO = "estrato";                       // Number
    public static final String CANAL_PREFERIDO = "canalPreferido";        // CanalNotificacion
    public static final String EXCEPCION = "excepcion";                   // Excepcion (opcional)
    // Claves propias de cada tipo.
    public static final String NUMERO_HABITANTES = "numeroHabitantes";    // Number (residencial)
    public static final String BENEFICIO_SUBSIDIO = "beneficioSubsidio";  // Boolean (residencial)
    public static final String NOMBRE_ESTABLECIMIENTO = "nombreEstablecimiento"; // String (comercial)
    public static final String ACTIVIDAD_ECONOMICA = "actividadEconomica";       // String (comercial)
    public static final String AREA_OPERACION_M2 = "areaOperacionM2";            // Number (comercial)
    public static final String CARGO_VERTIMIENTO = "cargoVertimiento";           // Number (comercial, opcional)
    public static final String CODIGO_INDUSTRIAL = "codigoIndustrial";           // String (industrial)
    public static final String PROCESO_PRODUCTIVO = "procesoProductivo";         // String (industrial)
    public static final String CARGA_CONTAMINANTE = "cargaContaminante";         // Number (industrial)
    public static final String VALOR_UNITARIO_CONTAMINACION = "valorUnitarioContaminacion"; // Number (industrial, opcional)
    public static final String CARGO_DEMANDA = "cargoDemanda";                   // Number (industrial, opcional)
    public static final String ENTIDAD_PUBLICA = "entidadPublica";               // String (oficial)
    public static final String ASIGNACION_PRESUPUESTAL = "asignacionPresupuestal"; // Number (oficial)

    /** Constructor sin parámetros: la fábrica no guarda estado. */
    public UsuarioFactory() {  // Constructor: Constructor sin parámetros
        // Sin instrucciones.
    }

    /**
     * Crea el usuario del tipo pedido. Es el único método que el resto del sistema necesita conocer.
     *
     * @param tipo tipo de usuario a crear.
     * @param datos datos del usuario (ver constantes de esta clase).
     * @throws IllegalArgumentException si el tipo es nulo o faltan/son inválidos los datos.
     */
    public Usuario crearUsuario(TipoUsuario tipo, Map<String, Object> datos) {  // Método crearUsuario: Crea el usuario del tipo pedido
        if (tipo == null) { // Sin tipo no se sabe qué crear.
            throw new IllegalArgumentException("El tipo de usuario es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        switch (tipo) { // Un caso por cada subclase de Usuario.
            case RESIDENCIAL:  // Caso RESIDENCIAL
                return crearResidencial(datos);  // Devuelve: crearResidencial(datos)
            case COMERCIAL:  // Caso COMERCIAL
                return crearComercial(datos);  // Devuelve: crearComercial(datos)
            case INDUSTRIAL:  // Caso INDUSTRIAL
                return crearIndustrial(datos);  // Devuelve: crearIndustrial(datos)
            case OFICIAL:  // Caso OFICIAL
                return crearOficial(datos);  // Devuelve: crearOficial(datos)
            default:  // Caso por defecto: cualquier otro valor
                throw new IllegalArgumentException("Tipo de usuario no soportado: " + tipo);  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** Crea un usuario residencial (habitantes >= 1 y subsidio según estrato 1, 2 o 3). */
    public UsuarioResidencial crearResidencial(Map<String, Object> datos) {  // Método crearResidencial: Crea un usuario residencial (habitantes >= 1 y subsidio según estrato 1, 2 o 3)
        validarDatosComunes(datos); // Valida los datos de Persona y Usuario.
        UsuarioResidencial u = new UsuarioResidencial(  // Declara «u» (UsuarioResidencial) con el valor: new UsuarioResidencial(
                texto(datos, IDENTIFICACION), texto(datos, NOMBRE), texto(datos, APELLIDO),  // Continuación de la instrucción de la línea anterior.
                dato(datos, FECHA_NACIMIENTO, LocalDate.class), texto(datos, DIRECCION), texto(datos, TELEFONO),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                texto(datos, EMAIL), texto(datos, CODIGO_SUSCRIPTOR), dato(datos, FECHA_REGISTRO, LocalDate.class),  // Ejecuta la operación «texto» (definida en esta clase o en un objeto colaborador)
                dato(datos, ESTRATO, Number.class).intValue(), dato(datos, CANAL_PREFERIDO, CanalNotificacion.class),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                dato(datos, NUMERO_HABITANTES, Number.class).intValue(),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                dato(datos, BENEFICIO_SUBSIDIO, Boolean.class)); // El constructor aplica las reglas del tipo.
        finalizar(u, datos); // Estrategia + excepción opcional.
        return u;  // Devuelve el valor de u
    }

    /** Crea un usuario comercial (establecimiento, actividad válida y área > 0). */
    public UsuarioComercial crearComercial(Map<String, Object> datos) {  // Método crearComercial: Crea un usuario comercial (establecimiento, actividad válida y área > 0)
        validarDatosComunes(datos);  // Ejecuta la operación «validar datos comunes» (definida en esta clase o en un objeto colaborador)
        UsuarioComercial u = new UsuarioComercial(  // Declara «u» (UsuarioComercial) con el valor: new UsuarioComercial(
                texto(datos, IDENTIFICACION), texto(datos, NOMBRE), texto(datos, APELLIDO),  // Continuación de la instrucción de la línea anterior.
                dato(datos, FECHA_NACIMIENTO, LocalDate.class), texto(datos, DIRECCION), texto(datos, TELEFONO),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                texto(datos, EMAIL), texto(datos, CODIGO_SUSCRIPTOR), dato(datos, FECHA_REGISTRO, LocalDate.class),  // Ejecuta la operación «texto» (definida en esta clase o en un objeto colaborador)
                dato(datos, ESTRATO, Number.class).intValue(), dato(datos, CANAL_PREFERIDO, CanalNotificacion.class),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                texto(datos, NOMBRE_ESTABLECIMIENTO), texto(datos, ACTIVIDAD_ECONOMICA),  // Ejecuta la operación «texto» (definida en esta clase o en un objeto colaborador)
                dato(datos, AREA_OPERACION_M2, Number.class).doubleValue());  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
        finalizar(u, datos);  // Ejecuta la operación «finalizar» (definida en esta clase o en un objeto colaborador)
        if (datos.get(CARGO_VERTIMIENTO) != null) { // Cargo por vertimiento opcional: se inyecta en la estrategia.
            u.setEstrategiaTarifa(new TarifaComercialStrategy(numeroADecimal(datos.get(CARGO_VERTIMIENTO))));  // Invoca u.setEstrategiaTarifa(...)
        }
        return u;  // Devuelve el valor de u
    }

    /** Crea un usuario industrial (código, proceso y carga contaminante >= 0). */
    public UsuarioIndustrial crearIndustrial(Map<String, Object> datos) {  // Método crearIndustrial: Crea un usuario industrial (código, proceso y carga contaminante >= 0)
        validarDatosComunes(datos);  // Ejecuta la operación «validar datos comunes» (definida en esta clase o en un objeto colaborador)
        UsuarioIndustrial u = new UsuarioIndustrial(  // Declara «u» (UsuarioIndustrial) con el valor: new UsuarioIndustrial(
                texto(datos, IDENTIFICACION), texto(datos, NOMBRE), texto(datos, APELLIDO),  // Continuación de la instrucción de la línea anterior.
                dato(datos, FECHA_NACIMIENTO, LocalDate.class), texto(datos, DIRECCION), texto(datos, TELEFONO),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                texto(datos, EMAIL), texto(datos, CODIGO_SUSCRIPTOR), dato(datos, FECHA_REGISTRO, LocalDate.class),  // Ejecuta la operación «texto» (definida en esta clase o en un objeto colaborador)
                dato(datos, ESTRATO, Number.class).intValue(), dato(datos, CANAL_PREFERIDO, CanalNotificacion.class),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                texto(datos, CODIGO_INDUSTRIAL), texto(datos, PROCESO_PRODUCTIVO),  // Ejecuta la operación «texto» (definida en esta clase o en un objeto colaborador)
                dato(datos, CARGA_CONTAMINANTE, Number.class).doubleValue());  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
        finalizar(u, datos);  // Ejecuta la operación «finalizar» (definida en esta clase o en un objeto colaborador)
        if (datos.get(VALOR_UNITARIO_CONTAMINACION) != null || datos.get(CARGO_DEMANDA) != null) { // Valores opcionales.
            u.setEstrategiaTarifa(new TarifaIndustrialStrategy(  // Invoca u.setEstrategiaTarifa(...)
                    numeroADecimal(datos.get(VALOR_UNITARIO_CONTAMINACION)), numeroADecimal(datos.get(CARGO_DEMANDA))));  // Continuación de la instrucción de la línea anterior.
        }
        return u;  // Devuelve el valor de u
    }

    /** Crea un usuario oficial (entidad pública y asignación presupuestal > 0; exento de cortes). */
    public UsuarioOficial crearOficial(Map<String, Object> datos) {  // Método crearOficial: Crea un usuario oficial (entidad pública y asignación presupuestal > 0; exento de cortes)
        validarDatosComunes(datos);  // Ejecuta la operación «validar datos comunes» (definida en esta clase o en un objeto colaborador)
        UsuarioOficial u = new UsuarioOficial(  // Declara «u» (UsuarioOficial) con el valor: new UsuarioOficial(
                texto(datos, IDENTIFICACION), texto(datos, NOMBRE), texto(datos, APELLIDO),  // Continuación de la instrucción de la línea anterior.
                dato(datos, FECHA_NACIMIENTO, LocalDate.class), texto(datos, DIRECCION), texto(datos, TELEFONO),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                texto(datos, EMAIL), texto(datos, CODIGO_SUSCRIPTOR), dato(datos, FECHA_REGISTRO, LocalDate.class),  // Ejecuta la operación «texto» (definida en esta clase o en un objeto colaborador)
                dato(datos, ESTRATO, Number.class).intValue(), dato(datos, CANAL_PREFERIDO, CanalNotificacion.class),  // Ejecuta la operación «dato» (definida en esta clase o en un objeto colaborador)
                texto(datos, ENTIDAD_PUBLICA), numeroADecimal(dato(datos, ASIGNACION_PRESUPUESTAL, Number.class)));  // Ejecuta la operación «texto» (definida en esta clase o en un objeto colaborador)
        finalizar(u, datos);  // Ejecuta la operación «finalizar» (definida en esta clase o en un objeto colaborador)
        return u;  // Devuelve el valor de u
    }

    /**
     * Verifica que existan y tengan el tipo correcto los datos comunes a todo usuario. El detalle de cada regla
     * (teléfono 7 a 10 dígitos, fecha no futura, estrato 1 a 6...) lo aplican los setters de Persona y Usuario.
     */
    public void validarDatosComunes(Map<String, Object> datos) {  // Método validarDatosComunes: Verifica que existan y tengan el tipo correcto los datos comunes a todo usuario
        if (datos == null) { // Sin datos no se puede crear nada.
            throw new IllegalArgumentException("Los datos del usuario son obligatorios.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        texto(datos, IDENTIFICACION);                         // Obligatorio.
        texto(datos, NOMBRE);                                 // Obligatorio.
        texto(datos, APELLIDO);                               // Obligatorio.
        dato(datos, FECHA_NACIMIENTO, LocalDate.class);       // Obligatorio.
        texto(datos, DIRECCION);                              // Obligatorio.
        texto(datos, TELEFONO);                               // Obligatorio.
        texto(datos, EMAIL);                                  // Obligatorio.
        texto(datos, CODIGO_SUSCRIPTOR);                      // Obligatorio.
        dato(datos, FECHA_REGISTRO, LocalDate.class);         // Obligatorio.
        dato(datos, ESTRATO, Number.class);                   // Obligatorio.
        dato(datos, CANAL_PREFERIDO, CanalNotificacion.class); // Obligatorio.
    }

    /**
     * Asigna AUTOMÁTICAMENTE la estrategia de cálculo tarifario según el tipo de usuario (Necesidad 1).
     * Las estrategias con parámetros (vertimiento, contaminación) quedan con valores por defecto, que
     * crearComercial/crearIndustrial reemplazan si los datos los traen.
     */
    public void asignarEstrategiaTarifa(Usuario usuario) {  // Método asignarEstrategiaTarifa: Asigna AUTOMÁTICAMENTE la estrategia de cálculo tarifario según el tipo de usuario (Necesidad 1)
        if (usuario == null || usuario.getTipoUsuario() == null) { // Se necesita el tipo para decidir.
            throw new IllegalArgumentException("El usuario y su tipo son obligatorios.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        switch (usuario.getTipoUsuario()) { // Strategy según el tipo.
            case RESIDENCIAL:  // Caso RESIDENCIAL
                usuario.setEstrategiaTarifa(new TarifaResidencialStrategy());  // Invoca usuario.setEstrategiaTarifa(...)
                break;  // Termina el caso del switch
            case COMERCIAL:  // Caso COMERCIAL
                usuario.setEstrategiaTarifa(new TarifaComercialStrategy());  // Invoca usuario.setEstrategiaTarifa(...)
                break;  // Termina el caso del switch
            case INDUSTRIAL:  // Caso INDUSTRIAL
                usuario.setEstrategiaTarifa(new TarifaIndustrialStrategy());  // Invoca usuario.setEstrategiaTarifa(...)
                break;  // Termina el caso del switch
            case OFICIAL:  // Caso OFICIAL
                usuario.setEstrategiaTarifa(new TarifaOficialStrategy());  // Invoca usuario.setEstrategiaTarifa(...)
                break;  // Termina el caso del switch
            default:  // Caso por defecto: cualquier otro valor
                throw new IllegalArgumentException("Tipo de usuario no soportado.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    /** Pasos finales comunes: estrategia automática y excepción legal opcional. */
    private void finalizar(Usuario usuario, Map<String, Object> datos) {  // Método finalizar: Pasos finales comunes
        asignarEstrategiaTarifa(usuario); // Estrategia según el tipo.
        Object excepcion = datos.get(EXCEPCION); // Excepción legal opcional.
        if (excepcion != null) {  // Condición: si excepcion es distinto de nulo
            if (!(excepcion instanceof Excepcion)) { // Verifica el tipo del dato opcional.
                throw new IllegalArgumentException("El dato '" + EXCEPCION + "' debe ser una Excepcion.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            usuario.setExcepcion((Excepcion) excepcion);  // Invoca usuario.setExcepcion(...)
        }
    }

    /**
     * Extrae un dato obligatorio del mapa verificando su tipo.
     * Es un método GENÉRICO (<T>): "T" es el tipo que se pide en el parámetro "tipo", lo que evita conversiones
     * (casts) manuales y errores de tipo en tiempo de ejecución.
     */
    private <T> T dato(Map<String, Object> datos, String clave, Class<T> tipo) {  // Método dato: Extrae un dato obligatorio del mapa verificando su tipo
        Object valor = datos.get(clave); // Lee el valor.
        if (valor == null) { // Dato faltante.
            throw new IllegalArgumentException("Falta el dato obligatorio '" + clave + "'.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!tipo.isInstance(valor)) { // Dato de tipo incorrecto.
            throw new IllegalArgumentException("El dato '" + clave + "' debe ser de tipo " + tipo.getSimpleName() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        return tipo.cast(valor); // Conversión segura.
    }

    /** Extrae un texto obligatorio. */
    private String texto(Map<String, Object> datos, String clave) {  // Método texto: Extrae un texto obligatorio
        return dato(datos, clave, String.class); // Reutiliza el método genérico.
    }

    /** Convierte un Number (o null) a BigDecimal exacto usando su representación en texto. */
    private BigDecimal numeroADecimal(Object valor) {  // Método numeroADecimal: Convierte un Number (o null) a BigDecimal exacto usando su representación en texto
        if (valor == null) { // Valores opcionales ausentes equivalen a cero.
            return BigDecimal.ZERO;  // Devuelve el valor de BigDecimal.ZERO
        }
        if (!(valor instanceof Number)) { // Solo se aceptan números.
            throw new IllegalArgumentException("Se esperaba un valor numérico.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        return new BigDecimal(valor.toString()); // toString evita los errores binarios de double.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "UsuarioFactory []"; // Sin estado que mostrar.
    }
}
