package modelo;
                                              // Paquete único

/** Patrón FACTORY: crea estrategia según el tipo de usuario. */
public class EstrategiaFactory {

    public static EstrategiaTarifa crear(TipoUsuario tipo) {
        switch (tipo) {
            case RESIDENCIAL: return new EstrategiaResidencial();
            case COMERCIAL:   return new EstrategiaComercial();
            case INDUSTRIAL:  return new EstrategiaIndustrial();
            case OFICIAL:     return new EstrategiaOficial();
            default: throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        }
    }
}