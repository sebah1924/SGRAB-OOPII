package modelo;
                                   // Paquete único del modelo

/** Tipos de suscriptores reconocidos por la EAAB. */
public enum TipoUsuario {
    RESIDENCIAL,   // Usuarios residenciales (estratos 1-6)
    COMERCIAL,     // Usuarios comerciales (restaurante, panadería, supermercado)
    INDUSTRIAL,    // Usuarios industriales con carga contaminante
    OFICIAL        // Usuarios oficiales (entidades públicas)
}