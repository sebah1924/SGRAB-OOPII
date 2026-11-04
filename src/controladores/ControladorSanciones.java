package controladores; // Paquete de los controladores.

import java.math.BigDecimal;  // Importa java.math.BigDecimal
import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import modelo.*;  // Importa modelo.*

/** Controlador de las sanciones por derroche de agua (RF-21 y RF-22). */
public class ControladorSanciones extends ControladorBase {  // Declaración de la clase ControladorSanciones: Controlador de las sanciones por derroche de agua (RF-21 y RF-22)

    /** Inyecta el contexto. */
    public ControladorSanciones(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** @return el valor de una multa de n salarios mínimos diarios con el valor de referencia del modelo. */
    public BigDecimal calcularMulta(int numeroSmdlv) {  // Método calcularMulta (devuelve BigDecimal)
        return new Sancion().calcularMulta(numeroSmdlv, Sancion.SMDLV_REFERENCIA); // Usa la regla del modelo, sin duplicarla.
    }

    /** Impone una sanción; si es económica, la multa se calcula con el número de salarios mínimos diarios. */
    public Resultado<Sancion> imponer(Usuario usuario, TipoSancion tipo, int numeroSmdlv, String descripcion) {  // Método imponer: Impone una sanción; si es económica, la multa se calcula con el número de salarios mínimos diarios
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            BigDecimal valor = (tipo == TipoSancion.ECONOMICA) ? calcularMulta(numeroSmdlv) : null; // Solo la económica lleva valor.
            Sancion sancion = new Sancion(generarCodigo("SAN"), tipo, valor, descripcion, usuario); // Valida reglas del tipo.
            contexto.getEmpresa().registrarSancion(usuario, sancion);                                 // Verifica código único.
            return sancion;  // Devuelve el valor de sancion
        }, "Sanción impuesta.");  // Ejecuta: }, "Sanción impuesta.")
    }

    /** El usuario apela la sanción. */
    public Resultado<Sancion> apelar(Sancion sancion) {  // Método apelar: El usuario apela la sanción
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            sancion.apelar();  // Ejecuta la operación «apelar» (definida en esta clase o en un objeto colaborador)
            return sancion;  // Devuelve el valor de sancion
        }, "Apelación radicada.");  // Ejecuta: }, "Apelación radicada.")
    }

    /** La EAAB rechaza la apelación. */
    public Resultado<Sancion> confirmar(Sancion sancion) {  // Método confirmar: La EAAB rechaza la apelación
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            sancion.confirmar();  // Ejecuta la operación «confirmar» (definida en esta clase o en un objeto colaborador)
            return sancion;  // Devuelve el valor de sancion
        }, "Sanción confirmada.");  // Ejecuta: }, "Sanción confirmada.")
    }

    /** La EAAB acepta la apelación y anula la sanción. */
    public Resultado<Sancion> revocar(Sancion sancion) {  // Método revocar: La EAAB acepta la apelación y anula la sanción
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            sancion.revocar();  // Ejecuta la operación «revocar» (definida en esta clase o en un objeto colaborador)
            return sancion;  // Devuelve el valor de sancion
        }, "Sanción revocada.");  // Ejecuta: }, "Sanción revocada.")
    }

    /** Hace efectiva la sanción. */
    public Resultado<Sancion> ejecutarSancion(Sancion sancion) {  // Método ejecutarSancion: Hace efectiva la sanción
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            sancion.ejecutar();  // Ejecuta la operación «ejecutar» (definida en esta clase o en un objeto colaborador)
            return sancion;  // Devuelve el valor de sancion
        }, "Sanción ejecutada.");  // Ejecuta: }, "Sanción ejecutada.")
    }

    /** @return todas las sanciones de todos los usuarios, para mostrarlas en una sola tabla. */
    public List<Sancion> listarTodas() {  // Método listarTodas (devuelve List<Sancion>)
        List<Sancion> todas = new ArrayList<>();  // Declara «todas» (List<Sancion>) con el valor: new ArrayList<>()
        for (Usuario u : contexto.getEmpresa().getListaSuscriptores()) {  // Recorre cada elemento «u» de contexto.getEmpresa().getListaSuscriptores()
            todas.addAll(u.getListaSanciones());  // Ejecuta la operación «add all» (definida en esta clase o en un objeto colaborador)
        }
        return todas;  // Devuelve el valor de todas
    }
}
