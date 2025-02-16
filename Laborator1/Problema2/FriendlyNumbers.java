
import java.util.Scanner;

public class FriendlyNumbers {
    public static int suma(int nr) {
        int suma = 1;
        for (int i = 2; i <= nr / 2; i++) {
            if (nr % i == 0) {
                suma += i;
            }
        }
        return suma;
    }
    public static boolean adevarat(int a, int b) {
        return suma(a) == b && suma(b) == a;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Nr1: ");
        int nr1 = sc.nextInt();

        System.out.print("Nr2: ");
        int nr = sc.nextInt();

        if (adevarat(nr1, nr)) {
            System.out.println(nr1 + " si " + nr + " sunt numere prietene");
        } else {
            System.out.println(nr1 + " si " + nr + " nu sunt numere prietene ");
        }

        sc.close();
    }
}