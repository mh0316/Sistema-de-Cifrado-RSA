import java.util.Scanner;

public class SistemaDeCifradoRSA {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        // Paso 1: Generación de Claves RSA
        int[] clavesPublicas = generarClavesRSA(teclado);

        // Paso 2: Cifrado de Mensajes
        System.out.print("\nIngrese el mensaje a cifrar: ");
        String mensaje = teclado.nextLine();
        System.out.println();
        int[] mensajeCifrado = cifrarMensaje(mensaje, clavesPublicas[0], clavesPublicas[1]);

        System.out.print("Mensaje cifrado: ");
        for (int bloque : mensajeCifrado) {
            System.out.print(bloque + " ");
        }

        // Paso 3: Descifrado de Mensajes
        String mensajeDescifrado = descifrarMensaje(mensajeCifrado, clavesPublicas[2], clavesPublicas[1]);
        System.out.println("\nMensaje descifrado: " + mensajeDescifrado);

        teclado.close();
    }

    public static int ingresar(Scanner teclado) {
        int entrada;
        do {
            try {
                entrada = teclado.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Error, ingreso inválido, inténtelo nuevamente.");
                teclado.nextLine();
            }
        } while (true);
        return entrada;
    }

    // Paso 1: Generación de Claves RSA
    public static int[] generarClavesRSA(Scanner teclado) {
        System.out.println("Generación de Claves RSA");

        // Pedir al usuario que ingrese dos números primos grandes p y q
        int p = generarNumeroPrimo(teclado);
        int q;
        do {
            System.out.print("Ingrese otro número primo grande distinto de " + p);
            System.out.println();
            q = generarNumeroPrimo(teclado);
            if (q == p) {
                System.out.println("El número debe ser distinto de " + p + ". Inténtelo nuevamente.");
            }
        } while (q == p);

        // Calcular n y φ(n)
        int n = p * q;
        int phiN = (p - 1) * (q - 1);

        // Elegir un número e coprimo con φ(n)
        int e = seleccionarE(phiN, teclado);

        // Calcular d, el inverso multiplicativo de e modulo φ(n)
        int d = calcularD(e, phiN);

        // Devolver las claves públicas y privadas
        return new int[]{e, n, d};
    }

    public static int generarNumeroPrimo(Scanner teclado) {
        int primo;
        do {
            System.out.print("Ingrese un número primo grande: ");
            primo = ingresar(teclado);
            if (!esPrimo(primo)) {
                System.out.println(primo + " no es un número primo. Inténtelo nuevamente.");
            } else {
                break;
            }
        } while (true);
        teclado.nextLine();
        return primo;
    }

    public static boolean esPrimo(int num) {
        if (num <= 1) {
            return false;
        }
        if (num == 2) {
            return true;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int seleccionarE(int phiN, Scanner teclado) {
        int e;
        do {
            System.out.print("Ingrese un número e que sea coprimo con " + phiN + ": ");
            e = ingresar(teclado);
            if (esCoprimo(e, phiN)) {
                break;
            } else {
                System.out.println(e + " no es coprimo con " + phiN + ". Inténtelo nuevamente.");
            }
        } while (true);
        teclado.nextLine();
        return e;
    }

    public static boolean esCoprimo(int a, int b) {
        if (a == 0 || b == 0) {
            return false;
        }
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a) == 1;
    }

    public static int calcularD(int e, int phiN) {
        int d = 0;
        int x = 0;
        int y = 1;
        int m = phiN;
        while (e > 0) {
            int q = m / e;
            int t = e;
            e = m % e;
            m = t;
            t = d;
            d = x - q * d;
            x = t;
            t = y;
            y = x;
            y = t - q * y;
        }
        if (x < 0) {
            x += phiN;
        }
        return x;
    }

    // Paso 2: Cifrado de Mensajes
    private static int[] cifrarMensaje(String mensaje, int clave, int n) {
        int[] mensajeCifrado = new int[mensaje.length()];
        int indice = 0;
        for (char caracter : mensaje.toCharArray()) {
            int valor = (int) caracter;
            int cifrado = squareAndMultiply(valor, clave, n);
            mensajeCifrado[indice] = cifrado;
            indice++;
        }
        return mensajeCifrado;
    }

    // Paso 3: Descifrado de Mensajes
    private static String descifrarMensaje(int[] mensajeCifrado, int clave, int n) {
        StringBuilder descifrado = new StringBuilder();
        for (int bloque : mensajeCifrado) {
            int descifradoNum = squareAndMultiply(bloque, clave, n);
            if (descifradoNum >= 32 && descifradoNum <= 126) {
                char caracter = (char) descifradoNum;
                descifrado.append(caracter);
            } else {
                descifrado.append('?'); // Caracter de reemplazo para valores no imprimibles
            }
        }
        return descifrado.toString();
    }

    public static int squareAndMultiply(int base, int exponente, int modulo) {
        long result = 1;
        base %= modulo; // Reducimos la base al módulo para mejorar la eficiencia

        while (exponente > 0) {
            if (exponente % 2 == 1) {
                result = (result * base) % modulo;
            }
            exponente >>= 1;
            base = (base * base) % modulo;
        }

        return (int) result;
    }
}
