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

    /**
     * Pronostica el siguiente periodo con suavización exponencial simple.
     *
     * <p>Fórmula: F<sub>t+1</sub> = F<sub>t</sub> + α · (D<sub>t</sub> − F<sub>t</sub>),
     * empezando con F<sub>2</sub> = D<sub>1</sub>. F es el pronóstico, D la demanda real y
     * α (alfa) el peso que se le da a la demanda más reciente frente al pronóstico anterior:
     * con alfa 0 el pronóstico nunca cambia y con alfa 1 es igual a la última demanda.
     *
     * <p>Ejemplo: serie [120, 132, 128, 141, 138, 150] y alfa 0.3 → 137.5546.
     *
     * @param serie demanda real de cada periodo, del más antiguo al más reciente
     * @param alfa  constante de suavización, entre 0 y 1 (ambos incluidos)
     * @return el pronóstico del periodo siguiente al último de la serie
     */
    public double suavizacionExponencial(List<Double> serie, double alfa) {
        validarAlfa(alfa);
        if (serie.isEmpty()) {
            throw new IllegalArgumentException("Se necesita al menos un periodo");
        }

        double pronostico = serie.get(0);
        for (double demanda : serie.subList(1, serie.size())) {
            pronostico = pronostico + alfa * (demanda - pronostico);
        }
        return pronostico;
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

    private void validarAlfa(double alfa) {
        if (alfa < 0 || alfa > 1) {
            throw new IllegalArgumentException("Alfa debe estar entre 0 y 1");
        }
    }

    private double sumar(List<Double> valores) {
        double suma = 0;
        for (double valor : valores) {
            suma += valor;
        }
        return suma;
    }
}
