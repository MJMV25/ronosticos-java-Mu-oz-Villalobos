package pronosticos.unitarias;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import pronosticos.CalculadoraPronosticos;

/** Pruebas unitarias de debeGenerarAlerta: alerta cuando el error es mayor o igual al umbral. */
class AlertasTest {

    private static final double UMBRAL = 10;

    private CalculadoraPronosticos calculadora;

    @BeforeEach
    void prepararCalculadora() {
        calculadora = new CalculadoraPronosticos();
    }

    @Test
    @DisplayName("Alerta cuando el error supera el umbral")
    void alertaCuandoElErrorSuperaElUmbral() {
        assertTrue(calculadora.debeGenerarAlerta(12, UMBRAL));
    }

    @Test
    @DisplayName("Alerta cuando el error es igual al umbral (caso límite)")
    void alertaCuandoElErrorEsIgualAlUmbral() {
        assertTrue(calculadora.debeGenerarAlerta(10, UMBRAL));
    }

    @Test
    @DisplayName("No alerta cuando el error no alcanza el umbral")
    void noAlertaCuandoElErrorNoAlcanzaElUmbral() {
        assertFalse(calculadora.debeGenerarAlerta(7, UMBRAL));
    }

    // Se prueban los dos lados del rango para recorrer las dos condiciones del if (rama completa).
    @ParameterizedTest(name = "umbral = {0}")
    @ValueSource(doubles = {150, -5})
    @DisplayName("Rechaza un umbral fuera del rango de 0 a 100")
    void rechazaUnUmbralFueraDeRango(double umbralInvalido) {
        assertThrows(IllegalArgumentException.class, () -> calculadora.debeGenerarAlerta(12, umbralInvalido));
    }
}
