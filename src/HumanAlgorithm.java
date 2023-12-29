import java.util.Scanner;

public class HumanAlgorithm {
    static int ustalKolumne() {
        Scanner rysowanie = new Scanner(System.in);
        System.out.println("Podaj indeks kolumny:");
        Character kolumnaObiekt = rysowanie.next().charAt(0);
        for (int i = 0; i < Statki.zbiorAlfabetyczny.length; i++) {
            if (kolumnaObiekt.equals(Statki.zbiorAlfabetyczny[i])) {
                return i;
            }
        }
        System.out.println("Podano niepoprawny indeks kolumny. Spróbuj jeszcze raz.");
        return ustalKolumne();
    }

    static int ustalWiersz() {
        System.out.println("Podaj indeks wiersza (pomiń \"0\" w wierszach 1-9):");
        Scanner rysowanie = new Scanner(System.in);
        return rysowanie.nextInt() - 1;
    }
}
