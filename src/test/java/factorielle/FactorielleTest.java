package factorielle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FactorielleTest {

    private final Factorielle f = new Factorielle();

    @Test
    void factorialWith5Test() {
        long obs = f.fact(5);
        assertEquals(120, obs);     // oracle exact
    }

    @Test
    void factorialWith0Test() {
        assertEquals(1, f.fact(0));
    }
}
