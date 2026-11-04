package modelo; // Paquete del dominio.

import java.time.LocalDate; // Fecha de consulta de la tarifa.
import java.util.ArrayList; // Lista dinámica.
import java.util.Collections; // Vistas de solo lectura.
import java.util.List;       // Interfaz de lista.

/**
 * Entidad reguladora (CRA / Superintendencia de Servicios Públicos). Aplica el patrón SINGLETON.
 *
 * Contexto: Singleton garantiza que exista UNA sola instancia de la clase en toda la aplicación y ofrece un
 * punto de acceso global (getInstance). Aquí tiene sentido porque la autoridad reguladora y su catálogo de
 * tarifas deben ser únicos: si hubiera dos reguladores podrían existir tarifas contradictorias.
 * Para lograrlo: constructor PRIVADO (nadie más puede hacer "new"), atributo estático "instancia" y un
 * método estático que la crea la primera vez. Se marca "synchronized" para que sea seguro si varios hilos
 * (threads) la piden a la vez.
 *
 * REGLAS DE NEGOCIO:
 *  - define las fórmulas y vigencias tarifarias (Resolución CRA 688 de 2014): registra y busca Tarifas;
 *  - no puede haber dos tarifas vigentes al mismo tiempo para el mismo tipo de usuario;
 *  - supervisa a las empresas de servicio público (EAAB).
 */
public class Regulador {  // Declaración de la clase Regulador: Entidad reguladora (CRA / Superintendencia de Servicios Públicos)

    private static final String NOMBRE_POR_DEFECTO = "Comisión de Regulación de Agua Potable y Saneamiento Básico"; // Nombre de la CRA.
    private static final String TIPO_POR_DEFECTO = "CRA";           // Tipo de entidad por defecto.
    private static final String NIT_MARCADOR = "000000000-0";       // MARCADOR: reemplazar por el NIT oficial de la entidad.

    private static Regulador instancia; // Única instancia (null hasta que se pida por primera vez).

    private String nit;                                   // NIT de la entidad reguladora.
    private String nombre;                                // Nombre de la entidad.
    private String tipoEntidad;                           // CRA o Superintendencia.
    private List<Tarifa> listaTarifas = new ArrayList<>(); // Catálogo de tarifas.
    private List<EmpresaServicioPublico> listaEmpresasSupervisadas = new ArrayList<>(); // Empresas vigiladas.

    /** Constructor PRIVADO: impide crear reguladores desde fuera (esencia del Singleton). */
    private Regulador(String nombre, String tipoEntidad) {  // Constructor: Constructor PRIVADO
        this.nit = NIT_MARCADOR;         // NIT por defecto.
        this.nombre = nombre;            // Nombre de la entidad.
        this.tipoEntidad = tipoEntidad;  // Tipo de entidad.
    }

    /** @return la instancia única; si no existe la crea con los datos por defecto de la CRA. */
    public static synchronized Regulador getInstance() {  // Getter: devuelve instance
        if (instancia == null) { // Creación perezosa ("lazy"): solo cuando se necesita por primera vez.
            instancia = new Regulador(NOMBRE_POR_DEFECTO, TIPO_POR_DEFECTO);  // Actualiza «instancia»: new Regulador(NOMBRE_POR_DEFECTO, TIPO_POR_DEFECTO)
        }
        return instancia;  // Devuelve el valor de instancia
    }

    /** @return la instancia única; si aún no existe, la crea con el nombre y tipo indicados (si ya existe, los ignora). */
    public static synchronized Regulador getInstance(String nombre, String tipoEntidad) {  // Getter: devuelve instance
        if (instancia == null) { // Solo la primera llamada define los datos.
            if (nombre == null || nombre.trim().isEmpty() || tipoEntidad == null || tipoEntidad.trim().isEmpty()) {  // Condición: si nombre es igual a nulo o nombre.trim() está vacío o tipoEntidad es igual...
                throw new IllegalArgumentException("El nombre y el tipo de entidad son obligatorios.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
            instancia = new Regulador(nombre.trim(), tipoEntidad.trim());  // Actualiza «instancia»: new Regulador(nombre.trim(), tipoEntidad.trim())
        }
        return instancia;  // Devuelve el valor de instancia
    }

    /** @return el NIT. */
    public String getNit() {  // Getter: devuelve nit
        return nit; // Lectura simple.
    }

    /** @return el nombre. */
    public String getNombre() {  // Getter: devuelve nombre
        return nombre; // Lectura simple.
    }

    /** @return el tipo de entidad. */
    public String getTipoEntidad() {  // Getter: devuelve tipo entidad
        return tipoEntidad; // Lectura simple.
    }

    /** @return el catálogo de tarifas (solo lectura). */
    public List<Tarifa> getListaTarifas() {  // Getter: devuelve lista tarifas
        return Collections.unmodifiableList(listaTarifas); // Copia defensiva: se agrega solo con registrarTarifa().
    }

    /**
     * Registra una tarifa. Reglas: no nula, código no repetido y SIN solaparse con otra del mismo tipo de usuario.
     */
    public void registrarTarifa(Tarifa tarifa) {  // Método registrarTarifa: Registra una tarifa
        if (tarifa == null) { // No se registra "nada".
            throw new IllegalArgumentException("La tarifa no puede ser nula.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        for (Tarifa t : listaTarifas) { // Código único.
            if (t.getCodigo().equals(tarifa.getCodigo())) {  // Condición: si t.getCodigo().equals(tarifa.getCodigo())
                throw new IllegalArgumentException("Ya existe una tarifa con el código " + tarifa.getCodigo() + ".");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
            }
        }
        if (!validarVigenciaUnica(tarifa)) { // Regla: no puede haber dos tarifas vigentes a la vez para el mismo tipo.
            throw new IllegalArgumentException("Ya existe una tarifa vigente en ese rango de fechas para el tipo "  // Lanza IllegalArgumentException con un mensaje claro para el usuario
                    + tarifa.getTipoUsuario() + ".");  // Continúa el texto de la instrucción anterior.
        }
        listaTarifas.add(tarifa); // Se incorpora al catálogo.
    }

    /**
     * @return la tarifa del tipo de usuario vigente en la fecha dada, o null si no existe ninguna.
     * (El llamador debe verificar el null antes de usarla.)
     */
    public Tarifa buscarTarifaVigente(TipoUsuario tipo, LocalDate fecha) {  // Método buscarTarifaVigente (devuelve Tarifa)
        for (Tarifa t : listaTarifas) { // Recorre el catálogo.
            if (t.getTipoUsuario() == tipo && t.estaVigenteEn(fecha)) { // Mismo tipo y fecha dentro del rango.
                return t; // Por la regla de unicidad solo puede haber una.
            }
        }
        return null; // Ninguna vigente.
    }

    /** @return true si la tarifa NO se solapa con ninguna ya registrada del mismo tipo de usuario. */
    public boolean validarVigenciaUnica(Tarifa tarifa) {  // Método validarVigenciaUnica (devuelve boolean)
        for (Tarifa t : listaTarifas) { // Compara contra cada tarifa existente.
            if (t.seSolapaCon(tarifa)) { // Rangos que se cruzan para el mismo tipo.
                return false;  // Devuelve el valor de false
            }
        }
        return true;  // Devuelve el valor de true
    }

    /** Agrega una empresa a la lista de empresas supervisadas (sin duplicar). */
    public void supervisarEmpresa(EmpresaServicioPublico empresa) {  // Método supervisarEmpresa: Agrega una empresa a la lista de empresas supervisadas (sin duplicar)
        if (empresa == null) { // No se supervisa "nada".
            throw new IllegalArgumentException("La empresa no puede ser nula.");  // Lanza IllegalArgumentException con un mensaje claro para el usuario
        }
        if (!listaEmpresasSupervisadas.contains(empresa)) { // Evita duplicados (usa equals de la empresa).
            listaEmpresasSupervisadas.add(empresa);  // Invoca listaEmpresasSupervisadas.add(...)
        }
    }

    /** @return cuántas empresas supervisa. */
    public int contarEmpresasSupervisadas() {  // Método contarEmpresasSupervisadas (devuelve int)
        return listaEmpresasSupervisadas.size(); // Tamaño de la lista.
    }

    @Override
    public String toString() {  // Devuelve una representación textual del objeto (útil para trazabilidad)
        return "Regulador [nit=" + nit + ", nombre=" + nombre + ", tipoEntidad=" + tipoEntidad  // Devuelve: "Regulador [nit=" + nit + ", nombre=" + nombre + ", tipoEntidad=" + tipo...
                + ", tarifas=" + listaTarifas.size() + ", empresasSupervisadas=" + listaEmpresasSupervisadas.size() + "]"; // Resumen.
    }
}
