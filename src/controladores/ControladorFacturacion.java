package controladores; // Paquete de los controladores.

import java.math.BigDecimal;  // Importa java.math.BigDecimal
import java.time.LocalDate;  // Importa java.time.LocalDate
import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import modelo.*;  // Importa modelo.*

/** Controlador de la facturación y los pagos (RF-23 a RF-27). */
public class ControladorFacturacion extends ControladorBase {  // Declaración de la clase ControladorFacturacion: Controlador de la facturación y los pagos (RF-23 a RF-27)

    /** Resumen de una corrida de facturación: cuántas facturas se emitieron y a quiénes se omitió y por qué. */
    public static final class ResumenFacturacion {  // Declaración de la clase ResumenFacturacion: Resumen de una corrida de facturación:
        private final int emitidas;  // Atributo «emitidas» de tipo int
        private final List<String> omitidos;  // Atributo «omitidos» de tipo List<String>

        /** Crea el resumen. */
        public ResumenFacturacion(int emitidas, List<String> omitidos) {  // Constructor: Crea el resumen
            this.emitidas = emitidas;  // Asigna a emitidas el valor: emitidas
            this.omitidos = omitidos;  // Asigna a omitidos el valor: omitidos
        }

        /** @return facturas emitidas. */
        public int getEmitidas() {  // Getter: devuelve emitidas
            return emitidas;  // Devuelve el valor de emitidas
        }

        /** @return usuarios omitidos con su motivo. */
        public List<String> getOmitidos() {  // Getter: devuelve omitidos
            return omitidos;  // Devuelve el valor de omitidos
        }
    }

    /** Inyecta el contexto. */
    public ControladorFacturacion(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** Emite las facturas del periodo (RF-25). */
    public Resultado<ResumenFacturacion> emitir() {  // Método emitir: Emite las facturas del periodo (RF-25)
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            int antes = contexto.getEmpresa().getListaFacturas().size(); // Cantidad previa.
            contexto.getEmpresa().facturarPeriodo();  // Ejecuta la operación «facturar periodo» (definida en esta clase o en un objeto colaborador)
            int emitidas = contexto.getEmpresa().getListaFacturas().size() - antes; // Diferencia = emitidas ahora.
            return new ResumenFacturacion(emitidas, new ArrayList<>(contexto.getEmpresa().getOmitidosUltimaFacturacion()));  // Devuelve: new ResumenFacturacion(emitidas, new ArrayList<>(contexto.getEmpresa().g...
        }, "Facturación del periodo completada.");  // Ejecuta: }, "Facturación del periodo completada.")
    }

    /** @return todas las facturas de la empresa. */
    public List<Factura> listarTodas() {  // Método listarTodas (devuelve List<Factura>)
        return new ArrayList<>(contexto.getEmpresa().getListaFacturas());  // Devuelve: new ArrayList<>(contexto.getEmpresa().getListaFacturas())
    }

    /** Historial de un usuario con filtros opcionales por fecha de emisión y estado (RF-27). */
    public Resultado<List<Factura>> consultar(Usuario usuario, LocalDate desde, LocalDate hasta, EstadoPago estado) {  // Método consultar: Historial de un usuario con filtros opcionales por fecha de emisión y estado (RF-27)
        return ejecutar(() -> contexto.getEmpresa().consultarFacturasDeUsuario(usuario, desde, hasta, estado), "Consulta realizada.");  // Devuelve: ejecutar(() - es mayor que contexto.getEmpresa().consultarFacturasDeUsua...
    }

    /** Registra un pago (RF-23): la empresa verifica código único, y la factura valida valor y saldo. */
    public Resultado<Pago> registrarPago(Factura factura, String codigo, LocalDate fecha, BigDecimal valor,  // Método registrarPago: Registra un pago (RF-23):
                                         MetodoPago metodo, String referencia) {  // Continuación de la instrucción de la línea anterior.
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            Pago pago = new Pago(codigo, fecha, valor, metodo, referencia, factura); // Valida cada dato.
            contexto.getEmpresa().registrarPago(factura, pago);                       // Verifica unicidad y saldo.
            return pago;  // Devuelve el valor de pago
        }, "Pago registrado.");  // Ejecuta: }, "Pago registrado.")
    }

    /** Marca la factura como en acuerdo de pago (el usuario pacta cuotas). */
    public Resultado<Factura> pactarAcuerdo(Factura factura) {  // Método pactarAcuerdo: Marca la factura como en acuerdo de pago (el usuario pacta cuotas)
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            if (factura.estaPagada()) { // Una factura saldada no necesita acuerdo.
                throw new IllegalStateException("La factura ya está pagada.");  // Lanza IllegalStateException con un mensaje claro para el usuario
            }
            factura.setEstadoPago(EstadoPago.EN_ACUERDO_PAGO);  // Ejecuta la operación «set estado pago» (definida en esta clase o en un objeto colaborador)
            return factura;  // Devuelve el valor de factura
        }, "Acuerdo de pago registrado.");  // Ejecuta: }, "Acuerdo de pago registrado.")
    }

    /** Revisa los vencimientos de todas las facturas (RF-26) y devuelve cuántas pasaron a VENCIDA. */
    public Resultado<Integer> revisarVencimientos() {  // Método revisarVencimientos: Revisa los vencimientos de todas las facturas (RF-26) y devuelve cuántas pasaron a VENCIDA
        return ejecutar(() -> contexto.getEmpresa().actualizarEstadosDeFacturas(), "Vencimientos revisados.");  // Devuelve: ejecutar(() - es mayor que contexto.getEmpresa().actualizarEstadosDeFact...
    }
}
