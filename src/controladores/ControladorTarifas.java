package controladores; // Paquete de los controladores.

import java.math.BigDecimal;  // Importa java.math.BigDecimal
import java.time.LocalDate;  // Importa java.time.LocalDate
import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import modelo.*;  // Importa modelo.*

/** Controlador de las tarifas del regulador (RF-14). */
public class ControladorTarifas extends ControladorBase {  // Declaración de la clase ControladorTarifas: Controlador de las tarifas del regulador (RF-14)

    /** Inyecta el contexto. */
    public ControladorTarifas(ContextoSgrab contexto) {  // Constructor: Inyecta el contexto
        super(contexto);  // Llama al constructor de la clase base para iniciar la parte heredada
    }

    /** Registra una tarifa: el regulador verifica código único y que no se solape con otra del mismo tipo. */
    public Resultado<Tarifa> registrar(String codigo, TipoUsuario tipo, BigDecimal fijo, BigDecimal variable,  // Método registrar: Registra una tarifa:
                                       double subsidio, LocalDate inicio, LocalDate fin) {  // Continuación de la instrucción de la línea anterior.
        return ejecutar(() -> {  // Devuelve: ejecutar(() - es mayor que {
            Tarifa tarifa = new Tarifa(codigo, tipo, fijo, variable, subsidio, inicio, fin); // Valida cada dato.
            contexto.getRegulador().registrarTarifa(tarifa);                                   // Verifica no solapamiento.
            return tarifa;  // Devuelve el valor de tarifa
        }, "Tarifa registrada.");  // Ejecuta: }, "Tarifa registrada.")
    }

    /** @return todas las tarifas del catálogo. */
    public List<Tarifa> listar() {  // Método listar (devuelve List<Tarifa>)
        return new ArrayList<>(contexto.getRegulador().getListaTarifas());  // Devuelve: new ArrayList<>(contexto.getRegulador().getListaTarifas())
    }

    /** @return VIGENTE, PROGRAMADA o VENCIDA según la fecha de hoy (para mostrar un indicador de color). */
    public String estado(Tarifa tarifa) {  // Método estado (devuelve String)
        LocalDate hoy = LocalDate.now();  // Declara «hoy» (LocalDate) con el valor: LocalDate.now()
        if (hoy.isBefore(tarifa.getVigenciaInicio())) {  // Condición: si hoy.isBefore(tarifa.getVigenciaInicio())
            return "PROGRAMADA";  // Devuelve: "PROGRAMADA"
        }
        if (hoy.isAfter(tarifa.getVigenciaFin())) {  // Condición: si hoy.isAfter(tarifa.getVigenciaFin())
            return "VENCIDA";  // Devuelve: "VENCIDA"
        }
        return "VIGENTE";  // Devuelve: "VIGENTE"
    }
}
