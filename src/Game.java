import java.util.Scanner;

public class Game {
    static char[] zbiorAlfabetyczny = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
    static int wielkoscStatku;
    public static final int PUSTE = 0;
    public static final int STATEK = 1;
    public static final int TRAFIONY = 2;
    public static final int PUDLO = 3;
    public static final int ROBOCZY_STATEK = 4;
    public static final char PUSTE_SYMBOL = ' ';
    public static final char STATEK_SYMBOL = '\u25A1';
    public static final char TRAFIONY_SYMBOL = 'X';
    public static final char PUDLO_SYMBOL = '*';
    public static final char ROBOCZY_STATEK_SYMBOL = '\u2713';

    public static void main(String[] args) {
        przygotujObiePlanszeDoGry();
        System.out.println("Zaczynamy gre!");
        do {
            System.out.println();
            System.out.println(Gracze.dajNazweAktualnegoGracza() + " twój ruch.");
            wydrukujPlanszePrzeciwnika();
            int[] ruchGracza = pobierzRuchGracza();
            int wierszStatku = ruchGracza[0];
            int kolumnaStatku = ruchGracza[1];
            if (sprawdzCzyTrafionyMaszt(ruchGracza)) {
                Gracze.wpiszSymbolWpolePrzeciwnika(wierszStatku, kolumnaStatku, TRAFIONY);
                if (czyCalyStatekTrafiony(wierszStatku, kolumnaStatku, 'X')) {
                    System.out.println("Trafiony - zatopiony!");
                } else {
                    System.out.println("Trafiony!");
                }
                if (!Gracze.sprawdzCzyZostalyStatkiDoTrafienia()){
                    Gracze.wyswietlJesliCzlowiek("Koniec gry! Wygrałeś. Gratulacje!");
                    Gracze.wyswietlJesliKomputer("Koniec gry! Wygrał " + Gracze.dajNazweAktualnegoGracza());
                    return;
                }
                wydrukujPlanszePrzeciwnika();
                if (Gracze.czyTuraCzlowieka()) {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Wciśnij \"Enter\", żeby kontynuować grę.");
                    sc.nextLine();
                }
            } else {
                Gracze.wpiszSymbolWpolePrzeciwnika(wierszStatku, kolumnaStatku, PUDLO);
                System.out.println("Pudło!");
                wydrukujPlanszePrzeciwnika();
                if (Gracze.czyTuraCzlowieka()) {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Wciśnij \"Enter\", żeby kontynuować grę.");
                    sc.nextLine();
                }
            }
            Gracze.zmienGracza();
        } while (Gracze.sprawdzCzyZostalyStatkiDoTrafienia());
    }

    static void przygotujObiePlanszeDoGry() {
        for (int i = 0; i < 2; i++) {
            Gracze.wyswietlJesliCzlowiek(Gracze.dajNazweAktualnegoGracza() + ", witaj w grze w statki!\nOto twoja plansza:");
            if (Gracze.czyTuraCzlowieka()) {
                wydrukujPlansze();
            }
            Gracze.wyswietlJesliCzlowiek("Narysuj swoje statki.\nDo dyspozycji masz:\n- 1 czteromasztowiec\n- 2 trzymasztowce " +
                    "\n- 3 dwumasztowce\n- 4 jednomasztowce\nStatki możesz dowolnie ustawić, obrócić i wygiąć z zachowaniem zasady,\n" +
                    "że każdy maszt jednego statku musi stykać się z jego kolejnym masztem ścianką boczną \n(nie może łączyć się na ukos)" +
                    " oraz dwa statki nie mogę “dotykać” się żadnym bokiem masztu.\n" +
                    "Zaczynamy grę!");
            /*if (Gracze.czyTuraKomputera()) {
                uzupelnijPlanszeStatkami();
            } else {
                Gracze.uzupelnijPlanszeCzlowiekaDoTestow();
            }*/
            uzupelnijPlanszeStatkami();
            if (Gracze.czyTuraCzlowieka()) {
                Scanner sc = new Scanner(System.in);
                System.out.println("Twoja plansza jest gotowa. Czas na drugiego gracza.");
                System.out.println("Wciśnij \"Enter\", żeby kontynuować grę.");
                sc.nextLine();
            }
            Gracze.zmienGracza();
        }
    }

    static void wydrukujPlansze() {
        System.out.print("\t");
        for (int i = 0; i < zbiorAlfabetyczny.length; i++) {
            System.out.print(zbiorAlfabetyczny[i] + " | ");
        }
        System.out.println();
        for (int wiersz = 1; wiersz < 11; wiersz++) {
            if (wiersz <= 9) {
                System.out.print("0" + wiersz + "| ");
            } else {
                System.out.print(wiersz + "| ");
            }
            for (int kolumna = 1; kolumna < 11; kolumna++) {
                int pole = Gracze.dajWartoscZpolaAktualnegoGracza(wiersz - 1, kolumna - 1);
                char poleSymbol = switch (pole) {
                    case PUSTE -> PUSTE_SYMBOL;
                    case STATEK -> STATEK_SYMBOL;
                    case TRAFIONY -> TRAFIONY_SYMBOL;
                    case PUDLO -> PUDLO_SYMBOL;
                    default -> 0;
                };
                System.out.print(poleSymbol + " | ");
            }
            System.out.println();
        }
    }

    static void wydrukujPlansze(int[][] roboczyStatek) {
        System.out.print("\t");
        for (int i = 0; i < zbiorAlfabetyczny.length; i++) {
            System.out.print(zbiorAlfabetyczny[i] + " | ");
        }
        System.out.println();
        for (int wiersz = 1; wiersz < 11; wiersz++) {
            if (wiersz <= 9) {
                System.out.print("0" + wiersz + "| ");
            } else {
                System.out.print(wiersz + "| ");
            }
            for (int kolumna = 1; kolumna < 11; kolumna++) {
                int pole = -2;
                for (int maszt = 0; maszt < roboczyStatek.length; maszt++) {
                    if (roboczyStatek[maszt][0] == wiersz - 1 && roboczyStatek[maszt][1] == kolumna - 1) {
                        pole = ROBOCZY_STATEK;
                    }
                }
                if (pole != ROBOCZY_STATEK) {
                    pole = Gracze.dajWartoscZpolaAktualnegoGracza(wiersz - 1, kolumna - 1);
                }
                char poleSymbol = switch (pole) {
                    case PUSTE -> PUSTE_SYMBOL;
                    case STATEK -> STATEK_SYMBOL;
                    case TRAFIONY -> TRAFIONY_SYMBOL;
                    case PUDLO -> PUDLO_SYMBOL;
                    case ROBOCZY_STATEK -> ROBOCZY_STATEK_SYMBOL;
                    default -> 0;
                };
                System.out.print(poleSymbol + " | ");
            }
            System.out.println();
        }
    }

    static void uzupelnijPlanszeStatkami() {
        wielkoscStatku = 4;
        int iloscTakichSamychStatkow;
        do {
            iloscTakichSamychStatkow = ustalIloscTakichSamychStatkow();
            for (int nrStatku = 0; nrStatku < iloscTakichSamychStatkow; nrStatku++) {
                Gracze.wyswietlJesliCzlowiek("Narysuj statek. Ilosc masztów: " + wielkoscStatku);
                int[][] wspolrzedneStatku = RysowanieStatku.narysujStatek();
                wpiszStatekDoPlanszy(wspolrzedneStatku);
                wydrukujPlansze();
            }
            wielkoscStatku--;
        } while (wielkoscStatku > 0);
    }

    static int ustalIloscTakichSamychStatkow() {
        return switch (wielkoscStatku) {
            case 4 -> 1;
            case 3 -> 2;
            case 2 -> 3;
            case 1 -> 4;
            default -> 0;
        };
    }

    public static void wpiszStatekDoPlanszy(int[][] wspolrzedneStatku) {
        for (int maszt = 0; maszt < wspolrzedneStatku.length; maszt++) {
            int wierszMasztu = wspolrzedneStatku[maszt][0];
            int kolumnaMasztu = wspolrzedneStatku[maszt][1];
            Gracze.wpiszStatekWpole(wierszMasztu, kolumnaMasztu);
        }
    }

    static int[] pobierzRuchGracza() {
        int[] maszt = RysowanieStatku.dajWierszIkolumneMasztu();
        int wierszStatku = maszt[0];
        int kolumnaStatku = maszt[1];
        if (Gracze.czyTuraCzlowieka()) {
            if (!RysowanieStatku.czyWplanszy(wierszStatku, kolumnaStatku)) {
                System.out.println("Podane pole jest poza planszą. Spróbuj jeszcze raz.");
                return pobierzRuchGracza();
            }
        }
        if (sprawdzCzyPowtorkaRuchu(maszt)) {
            Gracze.wyswietlJesliCzlowiek("Podane pole już było strzelane. Spróbuj jeszcze raz.");
            return pobierzRuchGracza();
        }
        return maszt;
    }

    static boolean sprawdzCzyPowtorkaRuchu(int[] pobranyRuch) {
        int wierszStatku = pobranyRuch[0];
        int kolumnaStatku = pobranyRuch[1];
        return (Gracze.dajWartoscZpolaPrzeciwnika(wierszStatku, kolumnaStatku) == TRAFIONY) || (Gracze.dajWartoscZpolaPrzeciwnika(wierszStatku, kolumnaStatku) == PUDLO);
    }

    static boolean sprawdzCzyTrafionyMaszt(int[] pobranyRuch) {
        int wierszStatku = pobranyRuch[0];
        int kolumnaStatku = pobranyRuch[1];
        return Gracze.dajWartoscZpolaPrzeciwnika(wierszStatku, kolumnaStatku) == STATEK;
    }

    static void wydrukujPlanszePrzeciwnika() {
        System.out.print("\t");
        for (int i = 0; i < zbiorAlfabetyczny.length; i++) {
            System.out.print(zbiorAlfabetyczny[i] + " | ");
        }
        System.out.println();
        for (int wiersz = 1; wiersz < 11; wiersz++) {
            if (wiersz <= 9) {
                System.out.print("0" + wiersz + "| ");
            } else {
                System.out.print(wiersz + "| ");
            }
            for (int kolumna = 1; kolumna < 11; kolumna++) {
                int pole = Gracze.dajWartoscZpolaPrzeciwnika(wiersz - 1, kolumna - 1);
                char poleSymbol = switch (pole) {
                    case PUSTE, STATEK -> PUSTE_SYMBOL;
                    case TRAFIONY -> TRAFIONY_SYMBOL;
                    case PUDLO -> PUDLO_SYMBOL;
                    default -> 0;
                };
                System.out.print(poleSymbol + " | ");
            }
            System.out.println();
        }
    }

    static boolean sprawdzCzyCalyStatekTrafiony(int wierszMasztu, int kolumnaMasztu) {
        for (int wiersz = wierszMasztu - 1; wiersz <= wierszMasztu + 1; wiersz++) {
            for (int kolumna = kolumnaMasztu - 1; kolumna <= kolumnaMasztu + 1; kolumna++) {
                if (((wiersz == wierszMasztu - 1) || (wiersz == wierszMasztu + 1)) && ((kolumna == kolumnaMasztu - 1) || (kolumna == kolumnaMasztu + 1))) {
                    continue;
                }
                if (wiersz < 0 || wiersz >= Gracze.dajWielkoscAktualnejPlanszy() || kolumna < 0 || kolumna >= Gracze.dajWielkoscAktualnejPlanszy()) {
                    continue;
                }
                if (Gracze.dajWartoscZpolaPrzeciwnika(wiersz, kolumna) == STATEK) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean czyCalyStatekTrafiony(int wierszMasztu, int kolumnaMasztu, char strona) {
        if (Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu, kolumnaMasztu) == STATEK) {
            return false;
        }
        boolean result = true;

        if ((wierszMasztu - 1 >= 0) && (wierszMasztu - 1 < Gracze.dajWielkoscAktualnejPlanszy()) && (kolumnaMasztu >= 0) && (kolumnaMasztu < Gracze.dajWielkoscAktualnejPlanszy())) {
            int sasiad1 = Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu - 1, kolumnaMasztu);
            if (strona == 'N') {
                sasiad1 = PUDLO;
            }
            if ((sasiad1 != PUDLO) && (sasiad1 != PUSTE)) { //&& nie wychodze poza mape
                result = result && czyCalyStatekTrafiony(wierszMasztu - 1, kolumnaMasztu, 'S');
            }
        }

        if ((wierszMasztu >= 0) && (wierszMasztu < Gracze.dajWielkoscAktualnejPlanszy()) && (kolumnaMasztu + 1 >= 0) && (kolumnaMasztu + 1 < Gracze.dajWielkoscAktualnejPlanszy())) {
            int sasiad2 = Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu, kolumnaMasztu + 1);
            if (strona == 'E') {
                sasiad2 = PUDLO;
            }
            if ((sasiad2 != PUDLO) && (sasiad2 != PUSTE)) { //&& nie wychodze poza mape
                result = result && czyCalyStatekTrafiony(wierszMasztu, kolumnaMasztu + 1, 'W');
            }
        }

        if ((wierszMasztu + 1 >= 0) && (wierszMasztu + 1 < Gracze.dajWielkoscAktualnejPlanszy()) && (kolumnaMasztu >= 0) && (kolumnaMasztu < Gracze.dajWielkoscAktualnejPlanszy())) {
            int sasiad3 = Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu + 1, kolumnaMasztu);
            if (strona == 'S') {
                sasiad3 = PUDLO;
            }
            if ((sasiad3 != PUDLO) && (sasiad3 != PUSTE)) { //&& nie wychodze poza mape
                result = result && czyCalyStatekTrafiony(wierszMasztu + 1, kolumnaMasztu, 'N');
            }
        }
        if ((wierszMasztu >= 0) && (wierszMasztu < Gracze.dajWielkoscAktualnejPlanszy()) && (kolumnaMasztu - 1 >= 0) && (kolumnaMasztu - 1 < Gracze.dajWielkoscAktualnejPlanszy())){
            int sasiad4 = Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu, kolumnaMasztu - 1);
            if (strona == 'W') {
                sasiad4 = PUDLO;
            }
            if ((sasiad4 != PUDLO) && (sasiad4 != PUSTE)) { //&& nie wychodze poza mape
                result = result && czyCalyStatekTrafiony(wierszMasztu, kolumnaMasztu - 1, 'E');
            }
        }
        return result;
    }

    private int[] dajSasiadow(int wierszMasztu, int kolumnaMasztu) {
        int[] sasiedzi = new int[4];
        sasiedzi[0] = Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu - 1, kolumnaMasztu);
        sasiedzi[1] = Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu, kolumnaMasztu + 1);
        sasiedzi[2] = Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu + 1, kolumnaMasztu);
        sasiedzi[3] = Gracze.dajWartoscZpolaPrzeciwnika(wierszMasztu, kolumnaMasztu - 1);
        return sasiedzi;
    }
}
