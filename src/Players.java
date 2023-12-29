public class Players {
    private static int[][] planszaGracza1 = new int[10][10];
    private static int[][] planszaGracza2 = new int[10][10];
    private static final int GRACZ1 = 0;
    private static final int GRACZ2 = 1;
    private static int aktualnyGracz = GRACZ1;
    private static String graczPierwszy = "Agnieszka";
    private static String graczDrugi = "Gracz Drugi";

    private static int[][] dajPlanszeAktualnegoGracza() {
        return aktualnyGracz == GRACZ1 ? planszaGracza1 : planszaGracza2;
    }

    private static int[][] dajPlanszePrzeciwnika() {
        return aktualnyGracz == GRACZ1 ? planszaGracza2 : planszaGracza1;
    }

    static int dajWielkoscAktualnejPlanszy() {
        int[][] aktualnaPlansza = dajPlanszeAktualnegoGracza();
        return aktualnaPlansza.length;
    }

    static String dajNazweAktualnegoGracza() {
        return aktualnyGracz == GRACZ1 ? graczPierwszy : graczDrugi;
    }

    static int dajAktualnegoGracza() {
        return aktualnyGracz == GRACZ1 ? GRACZ1 : GRACZ2;
    }

    static void wyswietlJesliCzlowiek(String doWyswietlenia) {
        if (aktualnyGracz == GRACZ1) {
            System.out.println(doWyswietlenia);
        }
    }

    static void wyswietlJesliKomputer(String doWyswietlenia) {
        if (aktualnyGracz == GRACZ2) {
            System.out.println(doWyswietlenia);
        }
    }

    static int ustalKolumneJesliCzlowiek() {
        return AlgorytmCzlowieka.ustalKolumne();
    }

    static int ustalWierszJesliCzlowiek() {
        return AlgorytmCzlowieka.ustalWiersz();
    }

    static int ustalKolumneJesliKomputer() {
        return AlgorytmKomputera.wylosujKolumne();
    }

    static int ustalWierszJesliKomputer() {
        return AlgorytmKomputera.wylosujWiersz();
    }

    static void zmienGracza() {
        aktualnyGracz = aktualnyGracz == GRACZ1 ? GRACZ2 : GRACZ1;
    }

    static int dajWartoscZpolaAktualnegoGracza(int wiersz, int kolumna) {
        int[][] aktualnaPlansza = dajPlanszeAktualnegoGracza();
        return aktualnaPlansza[wiersz][kolumna];
    }

    static int dajWartoscZpolaPrzeciwnika(int wiersz, int kolumna) {
        int[][] planszaPrzeciwnika = dajPlanszePrzeciwnika();
        return planszaPrzeciwnika[wiersz][kolumna];
    }

    static void wpiszStatekWpole(int wiersz, int kolumna) {
        int[][] aktualnaPlansza = dajPlanszeAktualnegoGracza();
        aktualnaPlansza[wiersz][kolumna] = Statki.STATEK;
    }

    static void wpiszSymbolWpolePrzeciwnika(int wiersz, int kolumna, int symbol) {
        int[][] planszaPrzeciwnika = dajPlanszePrzeciwnika();
        planszaPrzeciwnika[wiersz][kolumna] = symbol;
    }

    static boolean sprawdzCzyZostalyStatkiDoTrafienia() {
        boolean nietrafionyStatek = false;
        int[][] planszaPrzeciwnika = Gracze.dajPlanszePrzeciwnika();
        for (int wiersz = 0; wiersz < planszaPrzeciwnika.length; wiersz++) {
            for (int kolumna = 0; kolumna < planszaPrzeciwnika[wiersz].length; kolumna++) {
                if (planszaPrzeciwnika[wiersz][kolumna] == Statki.STATEK){
                    nietrafionyStatek = true;
                    return nietrafionyStatek;
                }
            }
        }
        return nietrafionyStatek;
    }

    public static boolean czyTuraCzlowieka() {
        return aktualnyGracz == GRACZ1;
    }
    public static boolean czyTuraKomputera() {
        return aktualnyGracz == GRACZ2;
    }

    public static void uzupelnijPlanszeCzlowiekaDoTestow() {
        planszaGracza1 = new int[][]{
                {Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.PUSTE,Statki.STATEK,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.STATEK,Statki.STATEK,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.PUSTE,Statki.STATEK,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.STATEK,Statki.STATEK,Statki.STATEK,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE},
                {Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE,Statki.PUSTE}
        };
    }
}
