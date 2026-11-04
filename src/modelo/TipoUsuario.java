package modelo; // Paquete del dominio.

/**
 * Los cuatro tipos de usuario del servicio. Cada tipo se cobra con una fórmula tarifaria distinta
 * (Resolución CRA 688 de 2014) y se modela como una subclase de Usuario.
 */
public enum TipoUsuario {  // Declaración de la enumeración TipoUsuario: Los cuatro tipos de usuario del servicio
    RESIDENCIAL, // Hogares: estrato 1-6, pueden recibir subsidio (1-3) o pagar contribución (5-6).
    COMERCIAL,   // Establecimientos (restaurante, panadería...): sin subsidio ni contribución.
    INDUSTRIAL,  // Fábricas: pagan además cargo por carga contaminante (Decreto 1076 de 2015).
    OFICIAL      // Entidades públicas: sin recargos especiales y exentas de cortes.
}
