package modelo; // Paquete del dominio.

/**
 * Categorías de usuarios que NO pueden ser cortados ni suspendidos por su condición especial
 * (Necesidad 4 del enunciado, Decreto 137 de 2024).
 */
public enum CategoriaExenta {  // Declaración de la enumeración CategoriaExenta: Categorías de usuarios que NO pueden ser cortados ni suspendidos por su condición especial (Necesidad 4 del enunciado, Decreto 137 de 2024)
    HOSPITAL,                             // Protección del derecho a la salud.
    CENTRO_SALUD,                         // Protección del derecho a la salud.
    CENTRO_PENITENCIARIO,                 // Protección de los derechos humanos de los internos.
    INSTITUCION_EDUCATIVA,                // Protección de la niñez y la adolescencia.
    ESPECIAL_PROTECCION_CONSTITUCIONAL,   // Personas en debilidad manifiesta (adultos mayores, discapacidad...).
    USUARIO_OFICIAL                       // Continuidad de los servicios esenciales del Estado.
}
