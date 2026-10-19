package factorielle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FactorielleTest {

    private final Factorielle f = new Factorielle();

    @Test
    void factorialWith5Test() {
        long obs = f.fact(5);
        assertTrue(5 < obs);        // oracle (très) faible
    }

    @Test
    void factorialWith0Test() {
        assertEquals(1, f.fact(0));
    }
}
