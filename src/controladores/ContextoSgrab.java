package controladores; // Paquete de los controladores.

import java.math.BigDecimal;  // Importa java.math.BigDecimal
import java.time.LocalDate;  // Importa java.time.LocalDate
import java.util.HashMap;  // Importa java.util.HashMap
import java.util.Map;  // Importa java.util.Map
import modelo.*; // Todas las clases del dominio.

/**
 * Raíz de composición de la aplicación (patrón Singleton + "composition root").
 *
 * Es el único lugar que CREA y CONECTA los objetos principales del modelo: la empresa (EAAB), el regulador, el
 * gestor de notificaciones y la fábrica de usuarios. Los controladores reciben este contexto por inyección de
 * dependencias, de modo que ninguna vista ni controlador hace "new" de las piezas del dominio.
 *
 * Cuando exista la capa DAO (MySQL en XAMPP), este será el sitio donde se creen los DAO y se carguen los datos.
 * Por ahora los datos viven en memoria; cargarDatosDemostracion() permite probar el sistema sin base de datos.
 */
public final class ContextoSgrab {  // Declaración de la clase ContextoSgrab: Raíz de composición de la aplicación (patrón Singleton + "composition root")

    private static ContextoSgrab instancia; // Única instancia (Singleton).

    private final EmpresaServicioPublico empresa; // La EAAB.
    private final Regulador regulador;            // La autoridad reguladora (a su vez Singleton).
    private final GestorNotificaciones gestor;    // Orquestador de notificaciones.
    private final UsuarioFactory fabrica;         // Fábrica de usuarios (patrón Factory).

    /** Constructor privado: nadie más puede crear contextos (esencia del Singleton). */
    private ContextoSgrab() {  // Constructor: Constructor privado:
        this.empresa = new EmpresaServicioPublico("899999094-1", "Empresa de Acueducto y Alcantarillado de Bogotá - EAAB");  // Asigna a empresa el valor: new EmpresaServicioPublico("899999094-1", "Empresa d...
        this.regulador = Regulador.getInstance(); // Autoridad única.
        this.gestor = new GestorNotificaciones(); // Canales aún sin registrar.
        this.fabrica = new UsuarioFactory();  // Asigna a fabrica el valor: new UsuarioFactory()
        configurarCanales();                      // Registra los cuatro canales oficiales.
        empresa.agregarObservador(gestor);        // El gestor escucha los eventos de la empresa (Observer).
        regulador.supervisarEmpresa(empresa);     // El regulador vigila a la empresa.
    }

    /** @return la instancia única del contexto (se crea la primera vez que se pide). */
    public static synchronized ContextoSgrab getInstancia() {  // Getter: devuelve instancia
        if (instancia == null) { // Creación perezosa.
            instancia = new ContextoSgrab();  // Actualiza «instancia»: new ContextoSgrab()
        }
        return instancia;  // Devuelve el valor de instancia
    }

    /** @return la empresa. */
    public EmpresaServicioPublico getEmpresa() {  // Getter: devuelve empresa
        return empresa;  // Devuelve el valor de empresa
    }

    /** @return el regulador. */
    public Regulador getRegulador() {  // Getter: devuelve regulador
        return regulador;  // Devuelve el valor de regulador
    }

    /** @return el gestor de notificaciones. */
    public GestorNotificaciones getGestor() {  // Getter: devuelve gestor
        return gestor;  // Devuelve el valor de gestor
    }

    /** @return la fábrica de usuarios. */
    public UsuarioFactory getFabrica() {  // Getter: devuelve fabrica
        return fabrica;  // Devuelve el valor de fabrica
    }

    /** Registra los cuatro canales oficiales de notificación con su configuración (envío simulado). */
    private void configurarCanales() {  // Método configurarCanales: Registra los cuatro canales oficiales de notificación con su configuración (envío simulado)
        gestor.registrarNotificador(CanalNotificacion.EMAIL, new NotificadorEmail("notificaciones@eaab.gov.co", "smtp.eaab.gov.co"));  // Ejecuta la operación «registrar notificador» (definida en esta clase o en un objeto colaborador)
        gestor.registrarNotificador(CanalNotificacion.SMS, new NotificadorSMS("Operador EAAB"));  // Ejecuta la operación «registrar notificador» (definida en esta clase o en un objeto colaborador)
        gestor.registrarNotificador(CanalNotificacion.PUSH, new NotificadorPush("clave-demostracion", "SGRAB Móvil"));  // Ejecuta la operación «registrar notificador» (definida en esta clase o en un objeto colaborador)
        gestor.registrarNotificador(CanalNotificacion.LLAMADA, new NotificadorLlamada("Central EAAB", "Voz Sofía"));  // Ejecuta la operación «registrar notificador» (definida en esta clase o en un objeto colaborador)
    }

    /** Datos comunes de una persona para armar el mapa que espera UsuarioFactory. */
    private Map<String, Object> datos(String id, String nombre, String apellido, String cod, int estrato, CanalNotificacion canal) {  // Método datos: Datos comunes de una persona para armar el mapa que espera UsuarioFactory
        Map<String, Object> d = new HashMap<>();  // Declara «d» (Map<String, Object>) con el valor: new HashMap<>()
        d.put(UsuarioFactory.IDENTIFICACION, id);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.NOMBRE, nombre);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.APELLIDO, apellido);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.FECHA_NACIMIENTO, LocalDate.of(1985, 6, 15));  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.DIRECCION, "Carrera 7 # 32-16, Bogotá");  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.TELEFONO, "3001234567");  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.EMAIL, cod.toLowerCase() + "@correo.com"); // Correo derivado del código (sin tildes ni espacios).
        d.put(UsuarioFactory.CODIGO_SUSCRIPTOR, cod);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.FECHA_REGISTRO, LocalDate.now().minusYears(1));  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.ESTRATO, estrato);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.CANAL_PREFERIDO, canal);  // Guarda el valor asociado a la clave
        return d;  // Devuelve el valor de d
    }

    /**
     * Carga tarifas, usuarios de los cuatro tipos, una excepción, consumos y una primera facturación para poder
     * explorar el sistema. Solo se puede ejecutar una vez y únicamente si la empresa aún no tiene usuarios.
     */
    public void cargarDatosDemostracion() {  // Método cargarDatosDemostracion: Carga tarifas, usuarios de los cuatro tipos, una excepción, consumos y una primera facturación para poder explorar el sistema
        if (empresa.contarSuscriptores() > 0) { // Evita duplicar datos.
            throw new IllegalStateException("Los datos de demostración solo se cargan en un sistema vacío.");  // Lanza IllegalStateException con un mensaje claro para el usuario
        }
        LocalDate ini = LocalDate.now().minusYears(1); // Vigencia desde hace un año...
        LocalDate fin = LocalDate.now().plusYears(3);  // ...hasta dentro de tres.
        regulador.registrarTarifa(new Tarifa("TAR-RES", TipoUsuario.RESIDENCIAL, new BigDecimal("10000"), new BigDecimal("3000"), 0.40, ini, fin));  // Ejecuta la operación «registrar tarifa» (definida en esta clase o en un objeto colaborador)
        regulador.registrarTarifa(new Tarifa("TAR-COM", TipoUsuario.COMERCIAL, new BigDecimal("10000"), new BigDecimal("3000"), 0, ini, fin));  // Ejecuta la operación «registrar tarifa» (definida en esta clase o en un objeto colaborador)
        regulador.registrarTarifa(new Tarifa("TAR-IND", TipoUsuario.INDUSTRIAL, new BigDecimal("15000"), new BigDecimal("3500"), 0, ini, fin));  // Ejecuta la operación «registrar tarifa» (definida en esta clase o en un objeto colaborador)
        regulador.registrarTarifa(new Tarifa("TAR-OFI", TipoUsuario.OFICIAL, new BigDecimal("10000"), new BigDecimal("2800"), 0, ini, fin));  // Ejecuta la operación «registrar tarifa» (definida en esta clase o en un objeto colaborador)

        Excepcion educativa = new Excepcion("EXC-001", "Instituciones educativas", CategoriaExenta.INSTITUCION_EDUCATIVA,  // Declara «educativa» (Excepcion) con el valor: new Excepcion("EXC-001", "Instituciones educati...
                "Protección de la niñez y la adolescencia (Decreto 137 de 2024)", true);  // Continúa el texto de la instrucción anterior.
        empresa.registrarExcepcion(educativa);  // Ejecuta la operación «registrar excepcion» (definida en esta clase o en un objeto colaborador)

        Map<String, Object> d = datos("1010101010", "María Fernanda", "Rojas", "SUS-001", 2, CanalNotificacion.SMS);  // Declara «d» (Map<String, Object>) con el valor: datos("1010101010", "María Fernanda", "Rojas", ...
        d.put(UsuarioFactory.NUMERO_HABITANTES, 4);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.BENEFICIO_SUBSIDIO, true);  // Guarda el valor asociado a la clave
        Usuario maria = fabrica.crearUsuario(TipoUsuario.RESIDENCIAL, d);  // Declara «maria» (Usuario) con el valor: fabrica.crearUsuario(TipoUsuario.RESIDENCIAL, d)

        d = datos("2020202020", "Carlos Andrés", "Pérez", "SUS-002", 5, CanalNotificacion.EMAIL);  // Actualiza «d»: datos("2020202020", "Carlos Andrés", "Pérez", "SUS-0...
        d.put(UsuarioFactory.NUMERO_HABITANTES, 3);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.BENEFICIO_SUBSIDIO, false);  // Guarda el valor asociado a la clave
        Usuario carlos = fabrica.crearUsuario(TipoUsuario.RESIDENCIAL, d);  // Declara «carlos» (Usuario) con el valor: fabrica.crearUsuario(TipoUsuario.RESIDENCIAL, d)

        d = datos("3030303030", "Luis", "Torres", "SUS-003", 3, CanalNotificacion.PUSH);  // Actualiza «d»: datos("3030303030", "Luis", "Torres", "SUS-003", 3, ...
        d.put(UsuarioFactory.NOMBRE_ESTABLECIMIENTO, "Panadería La Espiga");  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.ACTIVIDAD_ECONOMICA, "panadería");  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.AREA_OPERACION_M2, 85);  // Guarda el valor asociado a la clave
        Usuario panaderia = fabrica.crearUsuario(TipoUsuario.COMERCIAL, d);  // Declara «panaderia» (Usuario) con el valor: fabrica.crearUsuario(TipoUsuario.COMERCIAL, d)

        d = datos("4040404040", "Ana", "Villamizar", "SUS-004", 4, CanalNotificacion.LLAMADA);  // Actualiza «d»: datos("4040404040", "Ana", "Villamizar", "SUS-004", ...
        d.put(UsuarioFactory.CODIGO_INDUSTRIAL, "IND-077");  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.PROCESO_PRODUCTIVO, "Pasteurización de lácteos");  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.CARGA_CONTAMINANTE, 12.5);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.VALOR_UNITARIO_CONTAMINACION, 1800);  // Guarda el valor asociado a la clave
        Usuario lacteos = fabrica.crearUsuario(TipoUsuario.INDUSTRIAL, d);  // Declara «lacteos» (Usuario) con el valor: fabrica.crearUsuario(TipoUsuario.INDUSTRIAL, d)

        d = datos("5050505050", "Rosa", "Camargo", "SUS-005", 3, CanalNotificacion.EMAIL);  // Actualiza «d»: datos("5050505050", "Rosa", "Camargo", "SUS-005", 3,...
        d.put(UsuarioFactory.ENTIDAD_PUBLICA, "Colegio Distrital Los Andes");  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.ASIGNACION_PRESUPUESTAL, 1500000000);  // Guarda el valor asociado a la clave
        d.put(UsuarioFactory.EXCEPCION, educativa);  // Guarda el valor asociado a la clave
        Usuario colegio = fabrica.crearUsuario(TipoUsuario.OFICIAL, d);  // Declara «colegio» (Usuario) con el valor: fabrica.crearUsuario(TipoUsuario.OFICIAL, d)

        Usuario[] todos = {maria, carlos, panaderia, lacteos, colegio};  // Declara «todos» (Usuario[]) con el valor: {maria, carlos, panaderia, lacteos, colegio}
        double[][] lecturas = {{14, 18}, {30, 28}, {40, 45}, {120, 130}, {60, 55}}; // Dos lecturas mensuales por usuario.
        for (int i = 0; i < todos.length; i++) { // Registra usuarios y consumos.
            empresa.registrarUsuario(todos[i]);  // Ejecuta la operación «registrar usuario» (definida en esta clase o en un objeto colaborador)
            empresa.registrarConsumo(todos[i], "CON-" + (i + 1) + "A", lecturas[i][0], LocalDate.now().minusDays(35), TipoLectura.REAL);  // Ejecuta la operación «registrar consumo» (definida en esta clase o en un objeto colaborador)
            empresa.registrarConsumo(todos[i], "CON-" + (i + 1) + "B", lecturas[i][1], LocalDate.now().minusDays(5), TipoLectura.REAL);  // Ejecuta la operación «registrar consumo» (definida en esta clase o en un objeto colaborador)
        }
        empresa.facturarPeriodo(); // Primera facturación del periodo.
    }
}
