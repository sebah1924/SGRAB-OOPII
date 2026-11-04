package controladores; // Paquete de los controladores.

import java.time.LocalDate;  // Importa java.time.LocalDate
import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import modelo.*;  // Importa modelo.*

/** Controlador de las lecturas de consumo (RF-15): registra consumos con la tarifa vigente detectada. */
public class ControladorConsumos extends ControladorBase {  // Declaración de la clase ControladorConsumos: Controlador de las lecturas de consumo (RF-15):

    /** Inyecta el contexto. */
    public ControladorConsumos(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** Registra una lectura; la empresa busca la tarifa vigente del tipo del usuario en esa fecha. */
    public Resultado<Consumo> registrar(Usuario usuario, String codigo, double volumenM3, LocalDate fecha, TipoLectura tipo) {  // Método registrar: Registra una lectura; la empresa busca la tarifa vigente del tipo del usuario en esa fecha
        return ejecutar(() -> contexto.getEmpresa().registrarConsumo(usuario, codigo, volumenM3, fecha, tipo),  // Devuelve: ejecutar(() - es mayor que contexto.getEmpresa().registrarConsumo(usuari...
                "Consumo registrado y consumo promedio actualizado.");  // Continúa el texto de la instrucción anterior.
    }

    /** @return la tarifa vigente del tipo del usuario en la fecha, o null si no existe (para avisar antes de registrar). */
    public Tarifa tarifaVigente(Usuario usuario, LocalDate fecha) {  // Método tarifaVigente (devuelve Tarifa)
        if (usuario == null || fecha == null) { // Sin datos no hay búsqueda posible.
            return null;  // Devuelve el valor de null
        }
        return contexto.getRegulador().buscarTarifaVigente(usuario.getTipoUsuario(), fecha);  // Devuelve: contexto.getRegulador().buscarTarifaVigente(usuario.getTipoUsuario(), fe...
    }

    /** @return los consumos del usuario. */
    public List<Consumo> listar(Usuario usuario) {  // Método listar (devuelve List<Consumo>)
        return usuario == null ? new ArrayList<Consumo>() : new ArrayList<>(usuario.getListaConsumos());  // Devuelve: usuario es igual a nulo ? new ArrayList<Consumo>() : new ArrayList<>(usu...
    }

    /**
     * Estima, SIN guardar nada, cuánto se cobraría por una lectura (con el subsidio o la contribución que le
     * corresponda al usuario). Sirve para mostrar una vista previa en vivo mientras el funcionario escribe.
     *
     * @return el valor estimado, o null si los datos aún no permiten calcularlo.
     */
    public java.math.BigDecimal estimarCobro(Usuario usuario, double volumenM3, LocalDate fecha, TipoLectura tipo) {  // Abre el bloque de código: public java.math.BigDecimal estimarCobro(Usuari...
        Tarifa tarifa = tarifaVigente(usuario, fecha); // Sin tarifa vigente no hay estimación.
        if (tarifa == null || tipo == null) {  // Condición: si tarifa es igual a nulo o tipo es igual a nulo
            return null;  // Devuelve el valor de null
        }
        try {  // Bloque protegido: si algo falla, se captura abajo
            Consumo temporal = new Consumo("ESTIMACION", volumenM3, fecha, tipo, tarifa, usuario); // Objeto desechable.
            return usuario.calcularTarifa(temporal); // Usa la estrategia tarifaria real del usuario.
        } catch (RuntimeException e) { // Datos incompletos o inválidos: simplemente no se estima.
            return null;  // Devuelve el valor de null
        }
    }
}
