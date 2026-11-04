package controladores; // Paquete de los controladores.

import java.math.BigDecimal;  // Importa java.math.BigDecimal
import modelo.*;  // Importa modelo.*

/** Controlador del panel principal: calcula los indicadores que muestra el tablero. */
public class ControladorPanel extends ControladorBase {  // Declaración de la clase ControladorPanel: Controlador del panel principal:

    /** Indicadores del tablero (objeto de valor inmutable). */
    public static final class Indicadores {  // Declaración de la clase Indicadores: Indicadores del tablero (objeto de valor inmutable)
        public final int usuarios, activos, enDeuda, suspendidos, cortados; // Usuarios por estado de servicio.
        public final int facturas, pendientes, vencidas, pagadas;           // Facturas por estado de pago.
        public final BigDecimal facturado, cartera;                         // Dinero facturado y por cobrar.

        /** Crea los indicadores. */
        public Indicadores(int usuarios, int activos, int enDeuda, int suspendidos, int cortados, int facturas,  // Constructor: Crea los indicadores
                           int pendientes, int vencidas, int pagadas, BigDecimal facturado, BigDecimal cartera) {  // Continuación de la instrucción de la línea anterior.
            this.usuarios = usuarios;  // Asigna a usuarios el valor: usuarios
            this.activos = activos;  // Asigna a activos el valor: activos
            this.enDeuda = enDeuda;  // Asigna a enDeuda el valor: enDeuda
            this.suspendidos = suspendidos;  // Asigna a suspendidos el valor: suspendidos
            this.cortados = cortados;  // Asigna a cortados el valor: cortados
            this.facturas = facturas;  // Asigna a facturas el valor: facturas
            this.pendientes = pendientes;  // Asigna a pendientes el valor: pendientes
            this.vencidas = vencidas;  // Asigna a vencidas el valor: vencidas
            this.pagadas = pagadas;  // Asigna a pagadas el valor: pagadas
            this.facturado = facturado;  // Asigna a facturado el valor: facturado
            this.cartera = cartera;  // Asigna a cartera el valor: cartera
        }
    }

    /** Inyecta el contexto. */
    public ControladorPanel(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** Recorre usuarios y facturas una sola vez y arma los indicadores. */
    public Indicadores indicadores() {  // Método indicadores: Recorre usuarios y facturas una sola vez y arma los indicadores
        int activos = 0, enDeuda = 0, suspendidos = 0, cortados = 0;  // Declara «activos» (int) con el valor: 0, enDeuda = 0, suspendidos = 0, cortados = 0
        for (Usuario u : contexto.getEmpresa().getListaSuscriptores()) { // Cuenta usuarios por estado.
            switch (u.getEstadoServicio()) {  // Selecciona el caso según el valor de u.getEstadoServicio()
                case ACTIVO: activos++; break;  // Ejecuta: case ACTIVO: activos++; break
                case EN_DEUDA: enDeuda++; break;  // Ejecuta: case EN_DEUDA: enDeuda++; break
                case SUSPENDIDO: suspendidos++; break;  // Ejecuta: case SUSPENDIDO: suspendidos++; break
                default: cortados++; break;  // Ejecuta: default: cortados++; break
            }
        }
        int pendientes = 0, vencidas = 0, pagadas = 0;  // Declara «pendientes» (int) con el valor: 0, vencidas = 0, pagadas = 0
        BigDecimal facturado = BigDecimal.ZERO, cartera = BigDecimal.ZERO;  // Declara «facturado» (BigDecimal) con el valor: BigDecimal.ZERO, cartera = BigDecimal.ZERO
        for (Factura f : contexto.getEmpresa().getListaFacturas()) { // Cuenta facturas y suma dinero.
            facturado = facturado.add(f.getValorTotal());  // Actualiza «facturado»: facturado.add(f.getValorTotal())
            if (f.calcularSaldoPendiente().signum() > 0) {  // Condición: si f.calcularSaldoPendiente() (signo) es mayor que 0
                cartera = cartera.add(f.calcularSaldoPendiente());  // Actualiza «cartera»: cartera.add(f.calcularSaldoPendiente())
            }
            switch (f.getEstadoPago()) {  // Selecciona el caso según el valor de f.getEstadoPago()
                case PAGADA: pagadas++; break;  // Ejecuta: case PAGADA: pagadas++; break
                case VENCIDA: vencidas++; break;  // Ejecuta: case VENCIDA: vencidas++; break
                default: pendientes++; break; // Pendiente o en acuerdo de pago.
            }
        }
        return new Indicadores(contexto.getEmpresa().contarSuscriptores(), activos, enDeuda, suspendidos, cortados,  // Devuelve: new Indicadores(contexto.getEmpresa().contarSuscriptores(), activos, enD...
                contexto.getEmpresa().getListaFacturas().size(), pendientes, vencidas, pagadas, facturado, cartera);  // Continuación de la instrucción de la línea anterior.
    }
}
