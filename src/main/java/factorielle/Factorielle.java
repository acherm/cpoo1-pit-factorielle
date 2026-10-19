package factorielle;

public class Factorielle {

    public long fact(int n) {
        if (n == 0) {
            return 1;
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result = result * i;
        }
        return result;
    }
}
