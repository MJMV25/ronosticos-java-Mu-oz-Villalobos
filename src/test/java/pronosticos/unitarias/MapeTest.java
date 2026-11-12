package pronosticos.unitarias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pronosticos.CalculadoraPronosticos;

/** Pruebas unitarias del error porcentual absoluto medio (MAPE). */
class MapeTest {

    private static final double TOLERANCIA = 0.01;

    private CalculadoraPronosticos calculadora;

    @BeforeEach
    void prepararCalculadora() {
        calculadora = new CalculadoraPronosticos();
    }

    @Test
    @DisplayName("Calcula el error porcentual absoluto medio")
    void calculaElErrorPorcentualAbsolutoMedio() {
        double resultado = calculadora.mape(List.of(141.0, 138.0, 150.0), List.of(126.67, 133.67, 135.67));

        // (14.33/141 + 4.33/138 + 14.33/150) / 3 · 100 = (10.163 + 3.138 + 9.553) / 3 = 7.618 %.
        assertEquals(7.62, resultado, TOLERANCIA);
    }

    @Test
    @DisplayName("Excluye del promedio los periodos con demanda 0")
    void excluyeLosPeriodosConDemandaCero() {
        double resultado = calculadora.mape(List.of(80.0, 0.0, 95.0), List.of(80.0, 0.0, 100.0));

        // Periodo 1: 0 %. Periodo 2: demanda 0, se excluye. Periodo 3: 5/95 = 5.263 %.
        // Solo hay 2 periodos válidos: 5.263 / 2 = 2.63 %.
        assertEquals(2.63, resultado, TOLERANCIA);
    }

    @Test
    @DisplayName("Rechaza series donde todos los periodos tienen demanda 0")
    void rechazaSeriesSinPeriodosValidos() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculadora.mape(List.of(0.0, 0.0), List.of(10.0, 20.0)));

        assertEquals("El MAPE no está definido: todos los periodos tienen demanda 0", error.getMessage());
    }
}
