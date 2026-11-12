package pronosticos.unitarias;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("Rechaza un umbral mayor que 100")
    void rechazaUnUmbralMayorQueCien() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.debeGenerarAlerta(12, 150));
    }
}
