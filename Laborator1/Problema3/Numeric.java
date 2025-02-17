import java.util.Scanner;

public class Numeric {
    public static int hexToDecimal(String hex) {
        int decimal = 0;
        for (int i = 0; i < hex.length(); i++) {
            char ch = hex.charAt(i);
            int val = Character.getNumericValue(ch);
            decimal = decimal * 16 + val;
        }
        return decimal;
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("hex: ");
        String hex = scanner.nextLine();
        scanner.close();

        int decimal = hexToDecimal(hex);
        System.out.println("decimal: " + decimal);
    }
}
