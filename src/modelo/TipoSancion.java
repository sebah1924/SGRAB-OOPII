package modelo; // Paquete del dominio.

/**
 * Tipos de sanción por derroche de agua (Decreto 137 de 2024).
 */
public enum TipoSancion {  // Declaración de la enumeración TipoSancion: Tipos de sanción por derroche de agua (Decreto 137 de 2024)
    ECONOMICA,           // Multa de hasta 16 salarios mínimos diarios (lavar carros, regar jardines, etc.).
    SUSPENSION_PARCIAL,  // Para empresas reincidentes con sistemas de alto consumo.
    CURSO_PEDAGOGICO     // Capacitación obligatoria sobre uso eficiente del agua.
}
