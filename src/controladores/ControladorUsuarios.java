package controladores; // Paquete de los controladores.

import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.LinkedHashSet;  // Importa java.util.LinkedHashSet
import java.util.List;  // Importa java.util.List
import java.util.Map;  // Importa java.util.Map
import java.util.Set;  // Importa java.util.Set
import modelo.*;  // Importa modelo.*

/**
 * Controlador del módulo de usuarios (Necesidades 1 y 2): registrar, buscar, editar, suspender, reactivar,
 * cortar, eliminar y consultar historiales. Recibe peticiones de las vistas y las traduce a llamadas al modelo.
 */
public class ControladorUsuarios extends ControladorBase {  // Declaración de la clase ControladorUsuarios: Controlador del módulo de usuarios (Necesidades 1 y 2):

    /** Inyecta el contexto de la aplicación. */
    public ControladorUsuarios(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto de la aplicación
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** Registra un usuario del tipo indicado: la fábrica lo crea y valida; la empresa verifica la unicidad. */
    public Resultado<Usuario> registrar(TipoUsuario tipo, Map<String, Object> datos) {  // Método registrar: Registra un usuario del tipo indicado:
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            Usuario usuario = contexto.getFabrica().crearUsuario(tipo, datos); // Crea y valida (RF-01).
            contexto.getEmpresa().registrarUsuario(usuario);                    // Verifica identificación y código únicos.
            return usuario;  // Devuelve el valor de usuario
        }, "Usuario registrado correctamente.");  // Ejecuta: }, "Usuario registrado correctamente.")
    }

    /** @return todos los suscriptores (copia, para que la vista no pueda alterar la lista de la empresa). */
    public List<Usuario> listar() {  // Método listar (devuelve List<Usuario>)
        return new ArrayList<>(contexto.getEmpresa().getListaSuscriptores());  // Devuelve: new ArrayList<>(contexto.getEmpresa().getListaSuscriptores())
    }

    /**
     * Búsqueda unificada (RF-02): si el texto está vacío devuelve todos; si no, junta sin repetir el usuario con ese
     * código, el de esa identificación y los cuyo nombre contiene el texto.
     */
    public List<Usuario> buscar(String texto) {  // Método buscar: Búsqueda unificada (RF-02):
        if (texto == null || texto.trim().isEmpty()) { // Sin criterio: todos.
            return listar();  // Devuelve: listar()
        }
        String criterio = texto.trim();  // Declara «criterio» (String) con el valor: texto.trim()
        Set<Usuario> resultado = new LinkedHashSet<>(); // Conjunto ordenado: evita duplicados.
        Usuario porCodigo = contexto.getEmpresa().buscarUsuarioPorCodigo(criterio);  // Declara «porCodigo» (Usuario) con el valor: contexto.getEmpresa().buscarUsuarioPorCodigo(cr...
        if (porCodigo != null) {  // Condición: si porCodigo es distinto de nulo
            resultado.add(porCodigo);  // Agrega el componente al contenedor; su posición la decide el diseño
        }
        Usuario porId = contexto.getEmpresa().buscarUsuarioPorIdentificacion(criterio);  // Declara «porId» (Usuario) con el valor: contexto.getEmpresa().buscarUsuarioPorIdentific...
        if (porId != null) {  // Condición: si porId es distinto de nulo
            resultado.add(porId);  // Agrega el componente al contenedor; su posición la decide el diseño
        }
        resultado.addAll(contexto.getEmpresa().buscarUsuarioPorNombre(criterio));  // Ejecuta la operación «buscar usuario por nombre» (definida en esta clase o en un objeto colaborador)
        return new ArrayList<>(resultado);  // Devuelve: new ArrayList<>(resultado)
    }

    /**
     * Edita dirección, teléfono, correo, estrato y canal de forma ATÓMICA (RF-03): si algún dato es inválido se
     * restauran los valores anteriores y no queda el usuario a medio modificar.
     */
    public Resultado<Usuario> editar(Usuario usuario, String direccion, String telefono, String email, int estrato,  // Método editar: Edita dirección, teléfono, correo, estrato y canal de forma ATÓMICA (RF-03):
                                     CanalNotificacion canal) {  // Continuación de la instrucción de la línea anterior.
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            String dirAnterior = usuario.getDireccion();   // Copia de seguridad de los valores actuales.
            String telAnterior = usuario.getTelefono();  // Declara «telAnterior» (String) con el valor: usuario.getTelefono()
            String emailAnterior = usuario.getEmail();  // Declara «emailAnterior» (String) con el valor: usuario.getEmail()
            int estratoAnterior = usuario.getEstrato();  // Declara «estratoAnterior» (int) con el valor: usuario.getEstrato()
            CanalNotificacion canalAnterior = usuario.getCanalPreferido();  // Declara «canalAnterior» (CanalNotificacion) con el valor: usuario.getCanalPreferido()
            try {  // Bloque protegido: si algo falla, se captura abajo
                usuario.setDireccion(direccion);  // Cada setter valida su regla.
                usuario.setTelefono(telefono);  // Ejecuta la operación «set telefono» (definida en esta clase o en un objeto colaborador)
                usuario.setEmail(email);  // Ejecuta la operación «set email» (definida en esta clase o en un objeto colaborador)
                usuario.setEstrato(estrato);  // Ejecuta la operación «set estrato» (definida en esta clase o en un objeto colaborador)
                usuario.setCanalPreferido(canal);  // Ejecuta la operación «set canal preferido» (definida en esta clase o en un objeto colaborador)
            } catch (RuntimeException e) {  // Captura el error de tipo RuntimeException e
                usuario.setDireccion(dirAnterior); // Rollback: se restauran los valores previos.
                usuario.setTelefono(telAnterior);  // Ejecuta la operación «set telefono» (definida en esta clase o en un objeto colaborador)
                usuario.setEmail(emailAnterior);  // Ejecuta la operación «set email» (definida en esta clase o en un objeto colaborador)
                usuario.setEstrato(estratoAnterior);  // Ejecuta la operación «set estrato» (definida en esta clase o en un objeto colaborador)
                usuario.setCanalPreferido(canalAnterior);  // Ejecuta la operación «set canal preferido» (definida en esta clase o en un objeto colaborador)
                throw e; // Se relanza para que ejecutar() lo convierta en mensaje.
            }
            return usuario;  // Devuelve el valor de usuario
        }, "Datos actualizados.");  // Ejecuta: }, "Datos actualizados.")
    }

    /** Suspende el servicio (RF-04). Informa claramente si el usuario es exento en lugar de no hacer nada. */
    public Resultado<Usuario> suspender(Usuario usuario) {  // Método suspender: Suspende el servicio (RF-04)
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            if (contexto.getEmpresa().usuarioEstaExento(usuario)) { // Protección legal.
                throw new IllegalStateException("El usuario está exento y no puede ser suspendido.");  // Lanza IllegalStateException con un mensaje claro para el usuario
            }
            contexto.getEmpresa().suspenderServicio(usuario);  // Ejecuta la operación «suspender servicio» (definida en esta clase o en un objeto colaborador)
            return usuario;  // Devuelve el valor de usuario
        }, "Servicio suspendido y usuario notificado.");  // Ejecuta: }, "Servicio suspendido y usuario notificado.")
    }

    /** Reactiva el servicio (RF-05). */
    public Resultado<Usuario> reactivar(Usuario usuario) {  // Método reactivar: Reactiva el servicio (RF-05)
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            contexto.getEmpresa().reactivarServicio(usuario);  // Ejecuta la operación «reactivar servicio» (definida en esta clase o en un objeto colaborador)
            return usuario;  // Devuelve el valor de usuario
        }, "Servicio reactivado.");  // Ejecuta: }, "Servicio reactivado.")
    }

    /** Corta el servicio por racionamiento o sanción (RF-06); informa si el usuario es exento. */
    public Resultado<Usuario> cortar(Usuario usuario) {  // Método cortar: Corta el servicio por racionamiento o sanción (RF-06); informa si el usuario es exento
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            if (contexto.getEmpresa().usuarioEstaExento(usuario)) { // Protección legal.
                throw new IllegalStateException("El usuario está exento: no se aplica el corte.");  // Lanza IllegalStateException con un mensaje claro para el usuario
            }
            contexto.getEmpresa().cortarServicio(usuario);  // Ejecuta la operación «cortar servicio» (definida en esta clase o en un objeto colaborador)
            return usuario;  // Devuelve el valor de usuario
        }, "Servicio cortado.");  // Ejecuta: }, "Servicio cortado.")
    }

    /** Elimina al usuario dejando constancia de fecha y motivo (RF-07). */
    public Resultado<Usuario> eliminar(Usuario usuario, String motivo) {  // Método eliminar: Elimina al usuario dejando constancia de fecha y motivo (RF-07)
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            contexto.getEmpresa().eliminarUsuario(usuario, motivo);  // Ejecuta la operación «eliminar usuario» (definida en esta clase o en un objeto colaborador)
            return usuario;  // Devuelve el valor de usuario
        }, "Usuario dado de baja con registro de auditoría.");  // Ejecuta: }, "Usuario dado de baja con registro de auditoría.")
    }

    /** @return true si el usuario está protegido de cortes y suspensiones. */
    public boolean esExento(Usuario usuario) {  // Método esExento (devuelve boolean)
        return contexto.getEmpresa().usuarioEstaExento(usuario);  // Devuelve: contexto.getEmpresa().usuarioEstaExento(usuario)
    }

    /** @return el historial de consumos del usuario (RF-08). */
    public List<Consumo> consumosDe(Usuario usuario) {  // Método consumosDe (devuelve List<Consumo>)
        return new ArrayList<>(usuario.getListaConsumos());  // Devuelve: new ArrayList<>(usuario.getListaConsumos())
    }

    /** @return el historial de facturas del usuario (RF-08). */
    public List<Factura> facturasDe(Usuario usuario) {  // Método facturasDe (devuelve List<Factura>)
        return new ArrayList<>(usuario.getListaFacturas());  // Devuelve: new ArrayList<>(usuario.getListaFacturas())
    }

    /** @return las sanciones del usuario. */
    public List<Sancion> sancionesDe(Usuario usuario) {  // Método sancionesDe (devuelve List<Sancion>)
        return new ArrayList<>(usuario.getListaSanciones());  // Devuelve: new ArrayList<>(usuario.getListaSanciones())
    }
}
