package modelo; // Paquete del dominio.

/**
 * Forma en que se obtuvo el volumen de un consumo.
 */
public enum TipoLectura {  // Declaración de la enumeración TipoLectura: Forma en que se obtuvo el volumen de un consumo
    REAL,      // Lectura tomada en sitio, directamente del medidor.
    ESTIMADA   // Lectura calculada por promedio cuando no se pudo medir.
}
