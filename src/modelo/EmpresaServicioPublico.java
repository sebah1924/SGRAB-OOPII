package modelo; // Paquete del dominio.

import java.math.BigDecimal;               // Dinero con precisión exacta.
import java.time.LocalDate;                // Fechas sin hora.
import java.time.LocalDateTime;            // Fecha y hora.
import java.time.format.DateTimeFormatter; // Formato de códigos.
import java.util.ArrayList;                // Lista dinámica.
import java.util.Collections;              // Vistas de solo lectura.
import java.util.List;                     // Interfaz de lista.
import java.text.Normalizer;  // Importa java.text.Normalizer
import java.util.Objects;                  // Utilidades de igualdad.
import java.util.UUID;                     // Identificadores únicos.

/**
 * Empresa de servicio público (la EAAB). Es la clase "raíz" del dominio: administra suscriptores, excepciones,
 * facturas y bajas, y actúa como OBSERVABLE (IObservable) de suspensiones, cortes y cambios de calendario.
 *
 * REGLAS DE NEGOCIO:
 *  - identificación y código de suscriptor ÚNICOS al registrar (Necesidad 1);
 *  - buscar por código, identificación o nombre; suspender, reactivar y cortar (Necesidad 2);
 *  - eliminar con fecha y motivo obligatorios, conservando el historial de bajas (trazabilidad);
 *  - un usuario EXENTO no se corta ni se suspende, aunque esté en zona de racionamiento (Necesidad 4);
 *  - generar reportes financieros (Necesidad 9).
 *
 * Nota: esta clase mantiene los datos EN MEMORIA. La persistencia en MySQL (XAMPP) la hará la capa DAO.
 */
public class EmpresaServicioPublico implements IObservable {  // Declaración de la clase EmpresaServicioPublico: Empresa de servicio público (la EAAB)

    /**
     * Días entre la emisión y el vencimiento de una factura. El enunciado exige que el vencimiento sea posterior a
     * la emisión pero no fija el plazo; se adopta 15 días como valor por defecto, ajustable aquí.
     */
    public static final int DIAS_PLAZO_PAGO = 15;  // Atributo «DIAS_PLAZO_PAGO» de tipo int

    private String nit;                                  // NIT de la empresa.
    private String razonSocial;                          // Nombre legal.
    private List<Usuario> listaSuscriptores = new ArrayList<>();      // Suscriptores activos en el sistema.
    private List<BajaUsuario> historialBajas = new ArrayList<>();     // Auditoría de eliminaciones (composición 1 a 0..*).
    private List<Excepcion> listaExcepciones = new ArrayList<>();     // Excepciones legales registradas.
    private List<Factura> listaFacturas = new ArrayList<>();          // Facturas emitidas.
    private List<ReporteFinanciero> listaReportes = new ArrayList<>(); // Reportes generados (relación "elabora" del modelo de datos).
    private List<String> omitidosUltimaFacturacion = new ArrayList<>(); // Usuarios no facturados en la última corrida y su motivo.
    private List<IObservador> observadores = new ArrayList<>();       // Suscriptores a los eventos de la empresa.

    /** Constructor vacío (para la capa DAO). */
    public EmpresaServicioPublico() {  // Constructor: Constructor vacío (para la capa DAO)
        // Sin instrucciones.
    }

    /** Constructor con NIT y razón social obligatorios. */
    public EmpresaServicioPublico(String nit, String razon) {  // Constructor: Constructor con NIT y razón social obligatorios
        setNit(nit);           // NIT obligatorio.
        setRazonSocial(razon); // Razón social obligatoria.
    }

    /** @return el NIT. */
    public String getNit() {  // Getter: devuelve nit
        return nit; // Lectura simple.
    }

    /** Asigna el NIT (obligatorio). */
    public final void setNit(String nit) {  // Método setNit: Asigna el NIT (obligatorio)
        if (nit == null || nit.trim().isEmpty()) { // Regla: la empresa se identifica por su NIT.
            throw new IllegalArgumentException("El NIT es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.nit = nit.trim(); // Normaliza espacios.
    }

    /** @return la razón social. */
    public String getRazonSocial() {  // Getter: devuelve razon social
        return razonSocial; // Lectura simple.
    }

    /** Asigna la razón social (obligatoria). */
    public final void setRazonSocial(String razon) {  // Método setRazonSocial: Asigna la razón social (obligatoria)
        if (razon == null || razon.trim().isEmpty()) { // Regla: nombre legal obligatorio.
            throw new IllegalArgumentException("La razón social es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        this.razonSocial = razon.trim(); // Normaliza espacios.
    }

    /** @return los suscriptores (solo lectura). */
    public List<Usuario> getListaSuscriptores() {  // Getter: devuelve lista suscriptores
        return Collections.unmodifiableList(listaSuscriptores); // Copia defensiva: se altera solo con registrar/eliminar.
    }

    /** @return el historial de bajas (solo lectura). */
    public List<BajaUsuario> getHistorialBajas() {  // Getter: devuelve historial bajas
        return Collections.unmodifiableList(historialBajas); // Copia defensiva.
    }

    /** @return las excepciones registradas (solo lectura). */
    public List<Excepcion> getListaExcepciones() {  // Getter: devuelve lista excepciones
        return Collections.unmodifiableList(listaExcepciones); // Copia defensiva.
    }

    /** @return las facturas (solo lectura). */
    public List<Factura> getListaFacturas() {  // Getter: devuelve lista facturas
        return Collections.unmodifiableList(listaFacturas); // Copia defensiva.
    }

    /** @return los reportes financieros generados (solo lectura). */
    public List<ReporteFinanciero> getListaReportes() {  // Getter: devuelve lista reportes
        return Collections.unmodifiableList(listaReportes); // Copia defensiva: se agrega solo al generar un reporte.
    }

    /** @return los usuarios que no pudieron facturarse en la última corrida, cada uno con su motivo (solo lectura). */
    public List<String> getOmitidosUltimaFacturacion() {  // Getter: devuelve omitidos ultima facturacion
        return Collections.unmodifiableList(omitidosUltimaFacturacion); // Permite al funcionario revisar las excepciones.
    }

    /** @return los observadores suscritos (solo lectura). */
    public List<IObservador> getObservadores() {  // Getter: devuelve observadores
        return Collections.unmodifiableList(observadores); // Copia defensiva.
    }

    // ------------------------------------------------------------------ NECESIDAD 1 y 2: USUARIOS
    /** Registra (da de alta) a un suscriptor verificando que su identificación y su código sean ÚNICOS. */
    public void registrarUsuario(Usuario usuario) {  // Método registrarUsuario: Registra (da de alta) a un suscriptor verificando que su identificación y su código sean ÚNICOS
        if (usuario == null) { // No se registra "nada".
            throw new IllegalArgumentException("El usuario no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Usuario existente : listaSuscriptores) { // Recorre los suscriptores para verificar unicidad.
            if (existente.getIdentificacion().equals(usuario.getIdentificacion())) { // Identificación única.
                throw new IllegalArgumentException("Ya existe un usuario con la identificación " + usuario.getIdentificacion() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            if (existente.getCodigoSuscriptor().equals(usuario.getCodigoSuscriptor())) { // Código de suscriptor único.
                throw new IllegalArgumentException("Ya existe un usuario con el código " + usuario.getCodigoSuscriptor() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        listaSuscriptores.add(usuario); // Alta en la empresa.
    }

    /**
     * Elimina un usuario del sistema registrando OBLIGATORIAMENTE fecha y motivo en una BajaUsuario
     * (trazabilidad y auditoría). La baja queda en el historial; el usuario sale de la lista de suscriptores.
     */
    public void eliminarUsuario(Usuario usuario, String motivo) {  // Método eliminarUsuario: Elimina un usuario del sistema registrando OBLIGATORIAMENTE fecha y motivo en una BajaUsuario (trazabilidad y auditoría)
        if (usuario == null || !listaSuscriptores.contains(usuario)) { // Solo se elimina a quien está registrado.
            throw new IllegalArgumentException("El usuario no está registrado.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        String codigo = "BAJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Código único de la baja.
        BajaUsuario baja = new BajaUsuario(codigo, LocalDate.now(), motivo, usuario); // Valida el motivo obligatorio.
        historialBajas.add(baja);          // Primero se deja la evidencia...
        listaSuscriptores.remove(usuario); // ...y luego se retira al usuario.
    }

    /** @return el usuario con ese código de suscriptor, o null si no existe. */
    public Usuario buscarUsuarioPorCodigo(String codigo) {  // Método buscarUsuarioPorCodigo (devuelve Usuario)
        for (Usuario u : listaSuscriptores) { // Búsqueda lineal.
            if (u.getCodigoSuscriptor().equals(codigo)) {  // Condición: si u.getCodigoSuscriptor().equals(codigo)
                return u;  // Devuelve el valor de u
            }
        }
        return null;  // Devuelve el valor de null
    }

    /** @return el usuario con esa identificación, o null si no existe. */
    public Usuario buscarUsuarioPorIdentificacion(String id) {  // Método buscarUsuarioPorIdentificacion (devuelve Usuario)
        for (Usuario u : listaSuscriptores) { // Búsqueda lineal.
            if (u.getIdentificacion().equals(id)) {  // Condición: si u.getIdentificacion().equals(id)
                return u;  // Devuelve el valor de u
            }
        }
        return null;  // Devuelve el valor de null
    }

    /** Normaliza un texto para comparar: minúsculas y sin tildes ("María" y "maria" resultan iguales). */
    private static String normalizar(String texto) {  // Método normalizar: Normaliza un texto para comparar:
        String descompuesto = Normalizer.normalize(texto, Normalizer.Form.NFD); // Separa cada letra de su tilde.
        return descompuesto.replaceAll("\\p{M}", "").toLowerCase().trim();       // Quita las marcas de acento y pasa a minúsculas.
    }

    /** @return los usuarios cuyo nombre completo contiene el texto (sin distinguir mayúsculas ni tildes). */
    public List<Usuario> buscarUsuarioPorNombre(String nombre) {  // Método buscarUsuarioPorNombre (devuelve List<Usuario>)
        List<Usuario> resultado = new ArrayList<>(); // Coincidencias.
        if (nombre == null || nombre.trim().isEmpty()) { // Sin criterio no hay resultados.
            return resultado;  // Devuelve el valor de resultado
        }
        String criterio = normalizar(nombre); // Criterio comparable.
        for (Usuario u : listaSuscriptores) { // Recorre los suscriptores.
            if (normalizar(u.getNombreCompleto()).contains(criterio)) { // Coincidencia parcial.
                resultado.add(u);  // Agrega el componente al contenedor; su posición la decide el diseño
            }
        }
        return resultado;  // Devuelve el valor de resultado
    }

    /** @return cantidad de suscriptores. */
    public int contarSuscriptores() {  // Método contarSuscriptores (devuelve int)
        return listaSuscriptores.size(); // Tamaño de la lista.
    }

    /**
     * Suspende el servicio por deudas y notifica SUSPENSION_EFECTIVA (con instrucciones de pago y reactivación).
     * Si el usuario es exento NO se suspende (regla de excepciones).
     */
    public void suspenderServicio(Usuario usuario) {  // Método suspenderServicio: Suspende el servicio por deudas y notifica SUSPENSION_EFECTIVA (con instrucciones de pago y reactivación)
        verificarRegistrado(usuario); // Debe pertenecer a la empresa.
        if (usuarioEstaExento(usuario)) { // Protección legal: no se aplica.
            return;  // Termina el método sin devolver valor
        }
        usuario.suspender(); // Valida deuda (2+ vencidas) y cambia el estado.
        notificarObservadores(new Evento(TipoEvento.SUSPENSION_EFECTIVA, usuario, null)); // Aviso en tiempo real.
    }

    /** Reactiva el servicio cuando el usuario regulariza su situación. */
    public void reactivarServicio(Usuario usuario) {  // Método reactivarServicio: Reactiva el servicio cuando el usuario regulariza su situación
        verificarRegistrado(usuario); // Debe pertenecer a la empresa.
        usuario.reactivar(); // Aplica la regla de regularización.
    }

    /**
     * Corta el servicio por turno de racionamiento o sanción. REGLA CLAVE: si el usuario está exento
     * NO se le aplica el corte, aunque esté en zona de racionamiento (ej.: un hospital en turno de racionamiento).
     */
    public void cortarServicio(Usuario usuario) {  // Método cortarServicio: Corta el servicio por turno de racionamiento o sanción
        verificarRegistrado(usuario); // Debe pertenecer a la empresa.
        if (usuarioEstaExento(usuario)) { // Hospital, colegio, oficial...: no se corta.
            return;  // Termina el método sin devolver valor
        }
        usuario.cortar(); // Cambia el estado a CORTADO.
    }

    /** Verifica que el usuario no sea nulo y esté registrado en la empresa. */
    private void verificarRegistrado(Usuario usuario) {  // Método verificarRegistrado: Verifica que el usuario no sea nulo y esté registrado en la empresa
        if (usuario == null || !listaSuscriptores.contains(usuario)) { // contains usa Persona.equals (identificación).
            throw new IllegalArgumentException("El usuario no está registrado en la empresa.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
    }

    // ------------------------------------------------------------------ NECESIDAD 4: EXCEPCIONES
    /** Registra una excepción legal con código único. */
    public void registrarExcepcion(Excepcion excepcion) {  // Método registrarExcepcion: Registra una excepción legal con código único
        if (excepcion == null) { // No se registra "nada".
            throw new IllegalArgumentException("La excepción no puede ser nula.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!excepcion.validarJustificacion()) { // La justificación debe explicar la exención.
            throw new IllegalArgumentException("La excepción requiere una justificación legal.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Excepcion existente : listaExcepciones) { // Código único.
            if (existente.getCodigo().equals(excepcion.getCodigo())) {  // Condición: si existente.getCodigo().equals(excepcion.getCodigo())
                throw new IllegalArgumentException("Ya existe una excepción con el código " + excepcion.getCodigo() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        listaExcepciones.add(excepcion); // Se incorpora al catálogo.
    }

    /** Valida si un usuario pertenece a una categoría exenta ANTES de aplicarle un corte. */
    public boolean usuarioEstaExento(Usuario usuario) {  // Método usuarioEstaExento: Valida si un usuario pertenece a una categoría exenta ANTES de aplicarle un corte
        return usuario != null && usuario.esExento(); // Delegación: el usuario conoce su propia condición.
    }

    // ------------------------------------------------------------------ NECESIDADES 3, 5, 7, 8 y 9: CONSUMOS, PAGOS, SANCIONES, FACTURACIÓN Y AVISOS
    /**
     * Registra una lectura de consumo verificando que su CÓDIGO sea único en toda la empresa y delegando en el
     * usuario (que rechaza lecturas ajenas, tarifas de otro tipo y repetidos, y recalcula su consumo promedio).
     */
    public void registrarConsumo(Usuario usuario, Consumo consumo) {  // Método registrarConsumo: Registra una lectura de consumo verificando que su CÓDIGO sea único en toda la empresa y delegando en el usuario (que rechaza lecturas ajenas, tarifas de otro tipo y repetidos, y recalcula su consumo promedio)
        verificarRegistrado(usuario); // El usuario debe pertenecer a la empresa.
        if (consumo == null) { // No se registra "nada".
            throw new IllegalArgumentException("El consumo no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (existeCodigoConsumo(consumo.getCodigo())) { // Regla: el código del consumo es único.
            throw new IllegalArgumentException("Ya existe un consumo con el código " + consumo.getCodigo() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        usuario.agregarConsumo(consumo); // El usuario lo incorpora a su historial y recalcula el promedio.
    }

    /**
     * Crea y registra una lectura buscando AUTOMÁTICAMENTE la tarifa vigente del tipo del usuario en la fecha de
     * lectura (Necesidad 5: solo se aplican tarifas vigentes).
     *
     * @return el consumo registrado.
     * @throws IllegalStateException si no existe una tarifa vigente para ese tipo de usuario en esa fecha.
     */
    public Consumo registrarConsumo(Usuario usuario, String codigo, double volumenM3, LocalDate fechaLectura,  // Método registrarConsumo: Crea y registra una lectura buscando AUTOMÁTICAMENTE la tarifa vigente del tipo del usuario en la fecha de lectura (Necesidad 5
                                    TipoLectura tipoLectura) {  // Continuación de la instrucción de la línea anterior.
        verificarRegistrado(usuario); // El usuario debe pertenecer a la empresa.
        if (fechaLectura == null) { // Sin fecha no se puede buscar la tarifa vigente.
            throw new IllegalArgumentException("La fecha de lectura es obligatoria.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        Tarifa tarifa = Regulador.getInstance().buscarTarifaVigente(usuario.getTipoUsuario(), fechaLectura); // Tarifa del tipo y fecha.
        if (tarifa == null) { // El regulador no tiene una tarifa que cubra esa fecha.
            throw new IllegalStateException("No hay una tarifa vigente de tipo " + usuario.getTipoUsuario()  // Lanza IllegalStateException con un mensaje claro para el usuario
                    + " en la fecha " + fechaLectura + ".");  // Continúa el texto de la instrucción anterior.
        }
        Consumo consumo = new Consumo(codigo, volumenM3, fechaLectura, tipoLectura, tarifa, usuario); // Valida cada dato.
        registrarConsumo(usuario, consumo); // Verifica unicidad y lo incorpora al usuario.
        return consumo;  // Devuelve el valor de consumo
    }

    /** Registra un pago verificando que su CÓDIGO sea único en toda la empresa y que la factura sea de la empresa. */
    public void registrarPago(Factura factura, Pago pago) {  // Método registrarPago: Registra un pago verificando que su CÓDIGO sea único en toda la empresa y que la factura sea de la empresa
        if (factura == null || !listaFacturas.contains(factura)) { // La factura debe haber sido emitida por esta empresa.
            throw new IllegalArgumentException("La factura no pertenece a la empresa.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (pago == null) { // No se registra "nada".
            throw new IllegalArgumentException("El pago no puede ser nulo.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (existeCodigoPago(pago.getCodigo())) { // Regla: el código del pago es único.
            throw new IllegalArgumentException("Ya existe un pago con el código " + pago.getCodigo() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        factura.agregarPago(pago); // La factura valida el pago, lo suma y actualiza su estado.
    }

    /** Registra una sanción verificando que su CÓDIGO sea único en toda la empresa. */
    public void registrarSancion(Usuario usuario, Sancion sancion) {  // Método registrarSancion: Registra una sanción verificando que su CÓDIGO sea único en toda la empresa
        verificarRegistrado(usuario); // El usuario debe pertenecer a la empresa.
        if (sancion == null) { // No se registra "nada".
            throw new IllegalArgumentException("La sanción no puede ser nula.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (existeCodigoSancion(sancion.getCodigoSancion())) { // Regla: el código de la sanción es único.
            throw new IllegalArgumentException("Ya existe una sanción con el código " + sancion.getCodigoSancion() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        usuario.agregarSancion(sancion); // El usuario la incorpora a su historial (base de la reincidencia).
    }

    /** @return true si algún suscriptor ya tiene un consumo con ese código. */
    private boolean existeCodigoConsumo(String codigo) {  // Método existeCodigoConsumo (devuelve boolean)
        for (Usuario u : listaSuscriptores) { // Recorre los suscriptores.
            for (Consumo c : u.getListaConsumos()) { // Recorre sus consumos.
                if (c.getCodigo().equals(codigo)) { // Mismo código.
                    return true;  // Devuelve el valor de true
                }
            }
        }
        return false;  // Devuelve el valor de false
    }

    /** @return true si alguna factura ya tiene un pago con ese código. */
    private boolean existeCodigoPago(String codigo) {  // Método existeCodigoPago (devuelve boolean)
        for (Factura f : listaFacturas) { // Recorre las facturas.
            for (Pago p : f.getListaPagos()) { // Recorre sus pagos.
                if (p.getCodigo().equals(codigo)) { // Mismo código.
                    return true;  // Devuelve el valor de true
                }
            }
        }
        return false;  // Devuelve el valor de false
    }

    /** @return true si algún suscriptor ya tiene una sanción con ese código. */
    private boolean existeCodigoSancion(String codigo) {  // Método existeCodigoSancion (devuelve boolean)
        for (Usuario u : listaSuscriptores) { // Recorre los suscriptores.
            for (Sancion s : u.getListaSanciones()) { // Recorre sus sanciones.
                if (s.getCodigoSancion().equals(codigo)) { // Mismo código.
                    return true;  // Devuelve el valor de true
                }
            }
        }
        return false;  // Devuelve el valor de false
    }

    /** @return true si ya existe una factura emitida con ese número. */
    private boolean existeFactura(String numero) {  // Método existeFactura (devuelve boolean)
        for (Factura f : listaFacturas) { // Recorre las facturas de la empresa.
            if (f.getNumero().equals(numero)) { // Mismo número.
                return true;  // Devuelve el valor de true
            }
        }
        return false;  // Devuelve el valor de false
    }

    /** @return true si ya existe un reporte con ese código. */
    private boolean existeReporte(String codigo) {  // Método existeReporte (devuelve boolean)
        for (ReporteFinanciero r : listaReportes) { // Recorre los reportes generados.
            if (r.getCodigo().equals(codigo)) { // Mismo código.
                return true;  // Devuelve el valor de true
            }
        }
        return false;  // Devuelve el valor de false
    }

    /**
     * Factura a todos los suscriptores: toma los consumos que aún no han sido facturados y las sanciones
     * económicas activas aún no cobradas, arma cada factura con el FacturaBuilder, la asocia al usuario y a la
     * empresa y notifica FACTURA_EMITIDA. Los usuarios sin consumos pendientes se omiten. Si a un usuario no se le
     * puede facturar (por ejemplo, una tarifa no vigente en la fecha de facturación), se omite SIN detener a los
     * demás y el motivo queda en getOmitidosUltimaFacturacion().
     */
    public void facturarPeriodo() {  // Método facturarPeriodo: Factura a todos los suscriptores
        omitidosUltimaFacturacion.clear(); // Cada corrida empieza sin omitidos.
        LocalDate hoy = LocalDate.now(); // Fecha de emisión (y de facturación).
        FacturaBuilder builder = new FacturaBuilder(); // Un solo Builder reutilizable (build() lo reinicia).
        for (Usuario usuario : listaSuscriptores) { // Recorre cada suscriptor.
            List<Consumo> pendientes = consumosSinFacturar(usuario); // Lecturas aún no cobradas.
            if (pendientes.isEmpty()) { // Nada que facturar.
                continue;  // Salta a la siguiente iteración del ciclo
            }
            try {  // Bloque protegido: si algo falla, se captura abajo
                Factura factura = armarFactura(builder, usuario, pendientes, hoy); // Construye y calcula la factura.
                registrarFacturaEmitida(factura); // La asocia y notifica.
            } catch (IllegalStateException | IllegalArgumentException e) { // Dato inválido o regla incumplida.
                builder.reset(); // Se descarta lo construido a medias.
                omitidosUltimaFacturacion.add(usuario.getCodigoSuscriptor() + ": " + e.getMessage()); // Se deja constancia.
            }
        }
    }

    /** Arma la factura de un usuario con sus consumos pendientes, sus sanciones por cobrar y su desincentivo. */
    private Factura armarFactura(FacturaBuilder builder, Usuario usuario, List<Consumo> pendientes, LocalDate hoy) {  // Método armarFactura: Arma la factura de un usuario con sus consumos pendientes, sus sanciones por cobrar y su desincentivo
        LocalDate inicio = pendientes.get(0).getFechaLectura(); // Se busca la lectura más antigua...
        LocalDate fin = inicio;                                  // ...y la más reciente.
        for (Consumo c : pendientes) { // Recorre las lecturas pendientes.
            if (c.getFechaLectura().isBefore(inicio)) { // Lectura más antigua que la actual.
                inicio = c.getFechaLectura();  // Actualiza «inicio»: c.getFechaLectura()
            }
            if (c.getFechaLectura().isAfter(fin)) { // Lectura más reciente que la actual.
                fin = c.getFechaLectura();  // Actualiza «fin»: c.getFechaLectura()
            }
        }
        String numero = "FAC-" + hoy.format(DateTimeFormatter.BASIC_ISO_DATE) + "-"  // Declara «numero» (String) con el valor: "FAC-" + hoy.format(DateTimeFormatter.BASIC_ISO...
                + usuario.getCodigoSuscriptor() + "-" + (usuario.contarFacturas() + 1); // Único por usuario y fecha.
        builder.paraUsuario(usuario).conNumero(numero).conPeriodo(inicio, fin)  // Invoca builder.paraUsuario(...)
                .conFechaEmision(hoy).conFechaVencimiento(hoy.plusDays(DIAS_PLAZO_PAGO)); // Interfaz fluida del Builder.
        for (Consumo c : pendientes) { // Incorpora cada lectura al Builder.
            builder.agregarConsumo(c);  // Invoca builder.agregarConsumo(...)
        }
        for (Sancion s : sancionesSinCobrar(usuario)) { // Las sanciones económicas activas se cobran en esta factura.
            builder.agregarSancion(s);  // Invoca builder.agregarSancion(...)
        }
        if (usuario.getTipoUsuario() == TipoUsuario.RESIDENCIAL) { // El desincentivo aplica solo a residenciales.
            builder.conDesincentivo(new Desincentivo("DES-" + numero,  // Invoca builder.conDesincentivo(...)
                    Desincentivo.LIMITE_BIMESTRAL_POR_DEFECTO, Desincentivo.VALOR_M3_POR_DEFECTO));  // Continuación de la instrucción de la línea anterior.
        }
        return builder.build(); // Valida, calcula el valor total y entrega la factura.
    }

    /** Asocia la factura al usuario y a la empresa, la suscribe a los observadores y notifica su emisión. */
    private void registrarFacturaEmitida(Factura factura) {  // Método registrarFacturaEmitida: Asocia la factura al usuario y a la empresa, la suscribe a los observadores y notifica su emisión
        if (existeFactura(factura.getNumero())) { // Regla: el número de factura es único.
            throw new IllegalArgumentException("Ya existe una factura con el número " + factura.getNumero() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        factura.getUsuario().agregarFactura(factura); // Queda en el historial del usuario.
        listaFacturas.add(factura);                   // Queda en la empresa.
        for (IObservador obs : observadores) { // Los observadores de la empresa también siguen a la factura.
            factura.agregarObservador(obs);  // Invoca factura.agregarObservador(...)
        }
        factura.notificarObservadores(new Evento(TipoEvento.FACTURA_EMITIDA, factura.getUsuario(), factura)); // Aviso informativo.
    }

    /** @return los consumos del usuario que todavía no pertenecen a ninguna de sus facturas. */
    private List<Consumo> consumosSinFacturar(Usuario usuario) {  // Método consumosSinFacturar (devuelve List<Consumo>)
        List<Consumo> pendientes = new ArrayList<>(); // Resultado.
        for (Consumo c : usuario.getListaConsumos()) { // Recorre el historial de consumos.
            boolean facturado = false; // Bandera: ¿ya está en alguna factura?
            for (Factura f : usuario.getListaFacturas()) { // Recorre las facturas del usuario.
                for (Consumo cf : f.getListaConsumos()) { // Recorre los consumos de cada factura.
                    if (cf.getCodigo().equals(c.getCodigo())) { // Mismo consumo.
                        facturado = true;  // Actualiza «facturado»: true
                    }
                }
            }
            if (!facturado) { // Sigue pendiente.
                pendientes.add(c);  // Invoca pendientes.add(...)
            }
        }
        return pendientes;  // Devuelve el valor de pendientes
    }

    /** @return las sanciones ECONÓMICAS ACTIVAS del usuario que todavía no se cobraron en ninguna de sus facturas. */
    private List<Sancion> sancionesSinCobrar(Usuario usuario) {  // Método sancionesSinCobrar (devuelve List<Sancion>)
        List<Sancion> porCobrar = new ArrayList<>(); // Resultado.
        for (Sancion s : usuario.getListaSanciones()) { // Recorre las sanciones del usuario.
            if (s.estaActiva() && s.getTipo() == TipoSancion.ECONOMICA && !estaCobrada(usuario, s)) { // Viva, económica y no cobrada.
                porCobrar.add(s);  // Invoca porCobrar.add(...)
            }
        }
        return porCobrar;  // Devuelve el valor de porCobrar
    }

    /** @return true si la sanción ya figura en alguna factura del usuario. */
    private boolean estaCobrada(Usuario usuario, Sancion sancion) {  // Método estaCobrada (devuelve boolean)
        for (Factura f : usuario.getListaFacturas()) { // Recorre las facturas del usuario.
            for (Sancion sf : f.getListaSanciones()) { // Recorre las sanciones de cada factura.
                if (sf.getCodigoSancion().equals(sancion.getCodigoSancion())) { // Misma sanción.
                    return true;  // Devuelve el valor de true
                }
            }
        }
        return false;  // Devuelve el valor de false
    }

    /**
     * Revisa TODAS las facturas y actualiza su estado de pago: las que pasaron su vencimiento sin pago completo
     * pasan a VENCIDA (con sus notificaciones). Está pensado para ejecutarse periódicamente (por ejemplo, una
     * tarea programada diaria o al iniciar la aplicación), porque el modelo no tiene un reloj propio.
     *
     * @return cuántas facturas pasaron a VENCIDA en esta revisión.
     */
    public int actualizarEstadosDeFacturas() {  // Método actualizarEstadosDeFacturas: Revisa TODAS las facturas y actualiza su estado de pago
        int nuevasVencidas = 0; // Contador de facturas que cambiaron a VENCIDA.
        for (Factura f : listaFacturas) { // Recorre todas las facturas de la empresa.
            EstadoPago antes = f.getEstadoPago(); // Estado previo a la revisión.
            f.actualizarEstadoPago();             // Aplica la regla de estados (pagada, vencida o pendiente).
            if (antes != EstadoPago.VENCIDA && f.getEstadoPago() == EstadoPago.VENCIDA) { // Cambió a vencida ahora.
                nuevasVencidas++;  // Incrementa el contador nuevasVencidas
            }
        }
        return nuevasVencidas;  // Devuelve el valor de nuevasVencidas
    }

    /** Consulta las facturas de un usuario con filtros opcionales por fecha de emisión y estado (Necesidad 9). */
    public List<Factura> consultarFacturasDeUsuario(Usuario usuario, LocalDate desde, LocalDate hasta, EstadoPago estado) {  // Método consultarFacturasDeUsuario: Consulta las facturas de un usuario con filtros opcionales por fecha de emisión y estado (Necesidad 9)
        verificarRegistrado(usuario); // El usuario debe pertenecer a la empresa.
        return usuario.consultarFacturas(desde, hasta, estado); // El usuario conoce su propio historial.
    }

    /**
     * Notifica la PROGRAMACIÓN DE UN CORTE a los usuarios de un sector afectado por un turno de racionamiento
     * (Necesidad 3). Cada usuario recibe un evento CORTE_PROGRAMADO; el gestor omite a los usuarios exentos.
     *
     * @param usuariosDelSector usuarios del sector afectado.
     * @param detalle texto con el sector y el turno (por ejemplo "lunes de 8:00 a.m. a 12:00 m.").
     */
    public void notificarCorteProgramado(List<Usuario> usuariosDelSector, String detalle) {  // Método notificarCorteProgramado: Notifica la PROGRAMACIÓN DE UN CORTE a los usuarios de un sector afectado por un turno de racionamiento (Necesidad 3)
        emitirParaUsuarios(TipoEvento.CORTE_PROGRAMADO, usuariosDelSector, detalle); // Un evento por usuario.
    }

    /**
     * Notifica una MODIFICACIÓN DEL CALENDARIO de racionamiento (nuevo turno, cambio de restricción o suspensión
     * del racionamiento) a los usuarios indicados (Necesidad 3).
     */
    public void notificarCambioCalendario(List<Usuario> usuarios, String detalle) {  // Método notificarCambioCalendario: Notifica una MODIFICACIÓN DEL CALENDARIO de racionamiento (nuevo turno, cambio de restricción o suspensión del racionamiento) a los usuarios indicados (Necesidad 3)
        emitirParaUsuarios(TipoEvento.CALENDARIO_MODIFICADO, usuarios, detalle); // Un evento por usuario.
    }

    /** Crea un evento por usuario con el detalle como dato y lo entrega a los observadores de la empresa. */
    private void emitirParaUsuarios(TipoEvento tipo, List<Usuario> usuarios, String detalle) {  // Método emitirParaUsuarios: Crea un evento por usuario con el detalle como dato y lo entrega a los observadores de la empresa
        if (usuarios == null || usuarios.isEmpty()) { // Debe haber a quién avisar.
            throw new IllegalArgumentException("Debe indicar al menos un usuario.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (detalle == null || detalle.trim().isEmpty()) { // El aviso debe explicar el sector, el turno o el cambio.
            throw new IllegalArgumentException("El detalle del aviso es obligatorio.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Usuario u : usuarios) { // Recorre los usuarios afectados.
            verificarRegistrado(u); // Cada uno debe pertenecer a la empresa.
            notificarObservadores(new Evento(tipo, u, detalle.trim())); // El gestor envía por el canal adecuado.
        }
    }

    /** Genera el reporte financiero con un autor por defecto (la propia razón social). */
    public ReporteFinanciero generarReporteFinanciero() {  // Método generarReporteFinanciero: Genera el reporte financiero con un autor por defecto (la propia razón social)
        return generarReporteFinanciero("Sistema SGRAB - " + razonSocial); // Delegación al método con autor.
    }

    /**
     * Genera el reporte financiero con: ingresos por facturación (total facturado), cartera pendiente (saldos por
     * cobrar), subsidios otorgados (descuento concedido a residenciales respecto al costo base) y multas recaudadas
     * (sanciones económicas ejecutadas). El código del reporte es único y el reporte queda en listaReportes.
     *
     * @param autor funcionario de la empresa que genera el reporte.
     */
    public ReporteFinanciero generarReporteFinanciero(String autor) {  // Método generarReporteFinanciero: Genera el reporte financiero con
        BigDecimal ingresos = BigDecimal.ZERO;   // Total facturado.
        BigDecimal cartera = BigDecimal.ZERO;    // Saldos pendientes.
        BigDecimal subsidios = BigDecimal.ZERO;  // Descuentos concedidos.
        BigDecimal multas = BigDecimal.ZERO;     // Multas cobradas.
        for (Factura f : listaFacturas) { // Recorre todas las facturas.
            ingresos = ingresos.add(f.getValorTotal()); // Suma lo facturado.
            BigDecimal saldo = f.calcularSaldoPendiente(); // Lo que aún se debe de esta factura.
            if (saldo.signum() > 0) { // Solo cuenta lo que realmente falta por cobrar.
                cartera = cartera.add(saldo);  // Actualiza «cartera»: cartera.add(saldo)
            }
            if (f.getUsuario() instanceof UsuarioResidencial && f.getUsuario().getEstrategiaTarifa() != null) { // Solo hogares.
                for (Consumo c : f.getListaConsumos()) { // Subsidio = costo base - costo con subsidio (si es positivo).
                    BigDecimal descuento = c.calcularCosto().subtract(f.getUsuario().calcularTarifa(c));  // Declara «descuento» (BigDecimal) con el valor: c.calcularCosto().subtract(f.getUsuario().calcu...
                    if (descuento.signum() > 0) { // Hubo descuento (no contribución).
                        subsidios = subsidios.add(descuento);  // Actualiza «subsidios»: subsidios.add(descuento)
                    }
                }
            }
            for (Sancion s : f.getListaSanciones()) { // Multas económicas ya ejecutadas.
                if (s.getTipo() == TipoSancion.ECONOMICA && s.getEstado() == EstadoSancion.EJECUTADA) {  // Condición: si s.getTipo() es igual a TipoSancion.ECONOMICA y s.getEstado() es igual a ...
                    multas = multas.add(s.getValorMulta());  // Actualiza «multas»: multas.add(s.getValorMulta())
                }
            }
        }
        String base = "REP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); // Código base por segundo.
        String codigo = base; // Código candidato.
        int consecutivo = 1;  // Se usa si ya existe otro reporte en el mismo segundo.
        while (existeReporte(codigo)) { // Regla: el código del reporte es único.
            consecutivo++;  // Incrementa el contador consecutivo
            codigo = base + "-" + consecutivo;  // Actualiza «codigo»: base + "-" + consecutivo
        }
        ReporteFinanciero reporte = new ReporteFinanciero(codigo, LocalDateTime.now(), autor, ingresos, cartera, subsidios, multas);  // Declara «reporte» (ReporteFinanciero) con el valor: new ReporteFinanciero(codigo, LocalDateTime.now...
        listaReportes.add(reporte); // La empresa conserva el reporte generado.
        return reporte;  // Devuelve el valor de reporte
    }

    // ------------------------------------------------------------------ OBSERVER (rol Observable)
    /** Suscribe un observador (sin duplicados). */
    @Override
    public void agregarObservador(IObservador obs) {  // Método agregarObservador: Suscribe un observador (sin duplicados)
        if (obs != null && !observadores.contains(obs)) { // Ignora nulos y repetidos.
            observadores.add(obs);  // Invoca observadores.add(...)
        }
    }

    /** Cancela la suscripción de un observador. */
    @Override
    public void eliminarObservador(IObservador obs) {  // Método eliminarObservador: Cancela la suscripción de un observador
        observadores.remove(obs); // No falla si no estaba.
    }

    /** Avisa a todos los observadores (copia defensiva de la lista). */
    @Override
    public void notificarObservadores(Evento evento) {  // Método notificarObservadores: Avisa a todos los observadores (copia defensiva de la lista)
        for (IObservador obs : new ArrayList<>(observadores)) { // Copia para tolerar cambios durante la iteración.
            obs.actualizar(evento);  // Invoca obs.actualizar(...)
        }
    }

    // ------------------------------------------------------------------ IDENTIDAD
    /** Dos empresas son la misma si tienen el mismo NIT. */
    @Override
    public boolean equals(Object obj) {  // Método equals: Dos empresas son la misma si tienen el mismo NIT
        if (this == obj) return true; // Mismo objeto.
        if (!(obj instanceof EmpresaServicioPublico)) return false; // Otro tipo o null.
        return Objects.equals(nit, ((EmpresaServicioPublico) obj).nit); // Igualdad por NIT.
    }

    @Override
    public int hashCode() {  // Calcula el código hash coherente con equals()
        return Objects.hash(nit); // Coherente con equals.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "EmpresaServicioPublico [nit=" + nit + ", razonSocial=" + razonSocial  // Devuelve: "EmpresaServicioPublico [nit=" + nit + ", razonSocial=" + razonSocial
                + ", suscriptores=" + listaSuscriptores.size() + ", facturas=" + listaFacturas.size() + "]"; // Resumen.
    }
}
