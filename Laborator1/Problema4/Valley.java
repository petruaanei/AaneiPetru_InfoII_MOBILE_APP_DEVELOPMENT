import java.util.Scanner;

public class Valley {
    public static int valley(String valley, int ValleyNr) {
        int cnt = 0;
        for (int i = 0; i < valley.length(); i++) {
            char ch = valley.charAt(i);
            if (ch == 'D') {
                cnt--;
            } else if (ch == 'U') {
                cnt++;
                if (cnt > 0)
                    ValleyNr++;
            } else break;
        }
        return ValleyNr;
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("secventa: ");
        String Valley = sc.next();
        sc.close();

        System.out.println(valley(Valley, 0));
    }
}
