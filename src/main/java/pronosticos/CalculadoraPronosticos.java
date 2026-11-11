package pronosticos;

import java.util.List;

/**
 * Pronósticos de demanda y medidas de error.
 *
 * <p>Reúne las reglas de negocio del sistema de predicción de demanda: cómo se calcula
 * un pronóstico y cómo se mide qué tan lejos quedó de la demanda real. Es una clase de
 * Java puro, sin anotaciones de Spring ni acceso a bases de datos, por eso se puede
 * probar con pruebas unitarias rápidas y aisladas.
 */
public class CalculadoraPronosticos {

    /**
     * Pronostica el siguiente periodo con el promedio de los últimos {@code ventana} valores.
     * Ejemplo: serie [120, 132, 128, 141, 138, 150] y ventana 3 → (141 + 138 + 150) / 3 = 143.
     */
    public double promedioMovil(List<Double> serie, int ventana) {
        if (ventana <= 0) {
            throw new IllegalArgumentException("La ventana debe ser un entero positivo");
        }
        if (serie.size() < ventana) {
            throw new IllegalArgumentException("La serie tiene menos periodos que la ventana configurada");
        }

        List<Double> ultimos = serie.subList(serie.size() - ventana, serie.size());
        return sumar(ultimos) / ventana;
    }

    /** Error absoluto medio (MAD): promedio de las diferencias absolutas, en unidades. */
    public double mad(List<Double> real, List<Double> pronostico) {
        validarSeries(real, pronostico);

        double sumaErrores = 0;
        for (int i = 0; i < real.size(); i++) {
            sumaErrores += Math.abs(real.get(i) - pronostico.get(i));
        }
        return sumaErrores / real.size();
    }

    /**
     * Error porcentual absoluto medio (MAPE), expresado en porcentaje.
     *
     * <p>Un periodo con demanda real igual a 0 no se puede expresar en porcentaje
     * (dividiría entre cero), así que se excluye del cálculo. Si todos los periodos
     * tienen demanda 0, el MAPE no está definido y se lanza una excepción.
     */
    public double mape(List<Double> real, List<Double> pronostico) {
        validarSeries(real, pronostico);

        double sumaErrores = 0;
        int periodosValidos = 0;
        for (int i = 0; i < real.size(); i++) {
            double valorReal = real.get(i);
            if (valorReal == 0) {
                continue;
            }
            sumaErrores += Math.abs(valorReal - pronostico.get(i)) / valorReal * 100;
            periodosValidos++;
        }

        if (periodosValidos == 0) {
            throw new IllegalArgumentException("El MAPE no está definido: todos los periodos tienen demanda 0");
        }
        return sumaErrores / periodosValidos;
    }

    /**
     * Indica si un error de pronóstico debe disparar una alerta.
     *
     * <p>Regla acordada con Producción: la alerta se activa cuando el error es mayor
     * o igual al umbral. Un error exactamente igual al umbral sí alerta.
     */
    public boolean debeGenerarAlerta(double errorPorcentual, double umbral) {
        if (umbral < 0 || umbral > 100) {
            throw new IllegalArgumentException("El umbral debe estar entre 0 y 100");
        }
        return errorPorcentual >= umbral;
    }

    private void validarSeries(List<Double> real, List<Double> pronostico) {
        if (real.size() != pronostico.size()) {
            throw new IllegalArgumentException("Las series deben tener el mismo número de periodos");
        }
        if (real.isEmpty()) {
            throw new IllegalArgumentException("Se necesita al menos un periodo");
        }
    }

    private double sumar(List<Double> valores) {
        double suma = 0;
        for (double valor : valores) {
            suma += valor;
        }
        return suma;
    }

    public double suavizacionExponencial(List<Double> serie, double alfa) {
        if (alfa < 0 || alfa > 1) {
            throw new IllegalArgumentException("El parámetro alfa debe estar entre 0 y 1");
        }
        if (serie.isEmpty()) {
            throw new IllegalArgumentException("La serie no puede estar vacía");
        }

        double pronostico = serie.get(0); // Inicializa con el primer valor de la serie
        for (int i = 1; i < serie.size(); i++) {
            pronostico = alfa * serie.get(i) + (1 - alfa) * pronostico;
        }
        return pronostico;
    }

}
