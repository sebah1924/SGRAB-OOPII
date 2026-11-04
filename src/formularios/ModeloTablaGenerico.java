package formularios; // Paquete de los formularios.

import java.util.ArrayList;  // Importa java.util.ArrayList
import java.util.List;  // Importa java.util.List
import java.util.function.Function;  // Importa java.util.function.Function
import javax.swing.table.AbstractTableModel;  // Importa javax.swing.table.AbstractTableModel

/**
 * Modelo de tabla GENÉRICO y reutilizable.
 *
 * En lugar de escribir un modelo de tabla distinto para Usuario, Factura, Consumo, Sanción... se escribe uno solo
 * parametrizado con el tipo de fila (T). Cada columna se define con su título y una función (expresión lambda) que
 * extrae el dato de la fila. Aplica genericidad, polimorfismo (funciones como parámetro) y el principio DRY.
 *
 * @param <T> tipo de objeto que representa cada fila.
 */
public class ModeloTablaGenerico<T> extends AbstractTableModel {  // Declaración de la clase ModeloTablaGenerico: Modelo de tabla GENÉRICO y reutilizable

    /** Definición de una columna: título y forma de obtener su valor desde la fila. */
    private static final class Columna<T> {  // Declaración de la clase Columna: Definición de una columna:
        private final String titulo;  // Atributo «titulo» de tipo String
        private final Function<T, Object> extractor;  // Atributo «extractor» de tipo Function<T, Object>

        private Columna(String titulo, Function<T, Object> extractor) {  // Constructor de la clase (inicializa el objeto)
            this.titulo = titulo;  // Asigna a titulo el valor: titulo
            this.extractor = extractor;  // Asigna a extractor el valor: extractor
        }
    }

    private final List<Columna<T>> columnas = new ArrayList<>(); // Columnas definidas.
    private List<T> filas = new ArrayList<>();                   // Datos actuales.

    /** Agrega una columna; devuelve el propio modelo para encadenar llamadas (interfaz fluida). */
    public ModeloTablaGenerico<T> columna(String titulo, Function<T, Object> extractor) {  // Método columna: Agrega una columna; devuelve el propio modelo para encadenar llamadas (interfaz fluida)
        columnas.add(new Columna<>(titulo, extractor));  // Agrega el componente al contenedor; su posición la decide el diseño
        return this;  // Devuelve el valor de this
    }

    /** Reemplaza los datos y avisa a la tabla para que se redibuje. */
    public void setFilas(List<T> nuevas) {  // Método setFilas: Reemplaza los datos y avisa a la tabla para que se redibuje
        this.filas = new ArrayList<>(nuevas); // Copia defensiva.
        fireTableDataChanged();  // Ejecuta la operación «fire table data changed» (definida en esta clase o en un objeto colaborador)
    }

    /** @return el objeto de la fila indicada (índice del MODELO, no de la vista ordenada). */
    public T getFila(int indice) {  // Getter: devuelve fila
        return filas.get(indice);  // Devuelve: filas.get(indice)
    }

    @Override
    public int getRowCount() {  // Getter: devuelve row count
        return filas.size();  // Devuelve: filas.size()
    }

    @Override
    public int getColumnCount() {  // Getter: devuelve column count
        return columnas.size();  // Devuelve: columnas.size()
    }

    @Override
    public String getColumnName(int columna) {  // Getter: devuelve column name
        return columnas.get(columna).titulo;  // Devuelve: columnas.get(columna).titulo
    }

    @Override
    public Object getValueAt(int fila, int columna) {  // Getter: devuelve value at
        return columnas.get(columna).extractor.apply(filas.get(fila));  // Devuelve: columnas.get(columna).extractor.apply(filas.get(fila))
    }
}
