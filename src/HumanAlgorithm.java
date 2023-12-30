import java.util.Scanner;

public class HumanAlgorithm {
    static int establishVerticalCoordinate() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj indeks kolumny:");
        Character verticalCoordinate = scanner.next().charAt(0);
        for (int i = 0; i < Game.gridLetters.length; i++) {
            if (verticalCoordinate.equals(Game.gridLetters[i])) {
                return i;
            }
        }
        System.out.println("Podano niepoprawny indeks kolumny. Spróbuj jeszcze raz.");
        return establishVerticalCoordinate();
    }

    static int establishHorizontalCoordinate() {
        System.out.println("Podaj indeks wiersza (pomiń \"0\" w wierszach 1-9):");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt() - 1;
    }
}
