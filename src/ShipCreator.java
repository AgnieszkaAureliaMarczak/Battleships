import java.util.Arrays;

public class ShipCreator {
    static int numberOfCorrectMasts = 0;

    static int[][] drawShip() {
        int[][] maszty = new int[wielkoscStatku][2];
        Narzedzia.uzupelnijTabliceDwuwymiarowaLiczba(maszty, 100);
        //jak te maszty są ok to wpisujemy w plansze uzytkownika
        narysujMaszty(maszty);

        if (!sprawdzCzyMasztyPrzylegajaDoSiebie(maszty)) {
            Gracze.wyswietlJesliCzlowiek("Ups. Narysowany statek jest niepoprawny. Każdy maszt statku musi " +
                    "stykać się z jego kolejnym masztem ścianką boczną. Spróbuj jeszcze raz.");
            if (Gracze.czyTuraCzlowieka()) {
                Statki.wydrukujPlansze();
            }
            return drawShip();
        }
        return maszty;
    }

    public static void narysujMaszty(int[][] maszty) {
        for (int i = 0; i < wielkoscStatku; i++) {
            int[] maszt = getMastCoordinates();
            int wierszStatku = maszt[0];
            int kolumnaStatku = maszt[1];
            if (Gracze.czyTuraCzlowieka()) {
                if (!checkIfWithinBoard(wierszStatku, kolumnaStatku)) {
                    System.out.println("Podane pole jest poza planszą. Spróbuj jeszcze raz.");
                    narysujMaszty(maszty);
                    return;
                }
            }
            if (!czyPustePole(maszty, wierszStatku, kolumnaStatku)) {
                Gracze.wyswietlJesliCzlowiek("Podane pole jest już zajęte. Spróbuj jeszcze raz.");
                narysujMaszty(maszty);
                return;
            }
            if (!sprawdzCzyMasztNiePrzylegaDoInnegoStatku2(wierszStatku, kolumnaStatku)) {
                Gracze.wyswietlJesliCzlowiek("Wygląda na to, że chcesz narysować maszt przylegający do innego statku. \n" +
                        "Pamętaj, że statki nie mogę “dotykać” się żadnym bokiem masztu. \n" +
                        "Spróbuj jeszcze raz.");
                narysujMaszty(maszty);
                return;
            }
            maszty[numberOfCorrectMasts] = new int[]{wierszStatku, kolumnaStatku};
            if (Gracze.czyTuraCzlowieka()) {
                Statki.wydrukujPlansze(maszty);
            }
            numberOfCorrectMasts++;
            if (numberOfCorrectMasts == wielkoscStatku) {
                numberOfCorrectMasts = 0;
                break;
            }
        }
    }

    static int[] getMastCoordinates() {
        int[] maszt = new int[2];
        if (Gracze.czyTuraCzlowieka()) {
            maszt[1] = Gracze.ustalKolumneJesliCzlowiek();
            maszt[0] = Gracze.ustalWierszJesliCzlowiek();
        } else {
            maszt[1] = Gracze.ustalKolumneJesliKomputer();
            maszt[0] = Gracze.ustalWierszJesliKomputer();
        }
        return maszt;
    }

    static boolean checkIfWithinBoard(int wierszIndex, int kolumnaIndex) {
        return wierszIndex >= 0 && wierszIndex <= 9 && kolumnaIndex >= 0 && kolumnaIndex <= 9;
    }

    static boolean czyPustePole(int[][] maszty, int wierszIndex, int kolumnaIndex) {
        for (int wiersz = 0; wiersz < maszty.length; wiersz++) {
            for (int kolumna = 0; kolumna < maszty[wiersz].length; kolumna++) {
                if ((maszty[wiersz][0] == wierszIndex) && (maszty[wiersz][1] == kolumnaIndex)) {
                    return false;
                }
            }
        }
        return Gracze.dajWartoscZpolaAktualnegoGracza(wierszIndex, kolumnaIndex) != STATEK;
    }

    static boolean sprawdzCzyMasztNiePrzylegaDoInnegoStatku2(int wierszMasztu, int kolumnaMasztu) {
        for (int wiersz = wierszMasztu - 1; wiersz <= wierszMasztu + 1; wiersz++) {
            for (int kolumna = kolumnaMasztu - 1; kolumna <= kolumnaMasztu + 1; kolumna++) {
                if (((wiersz == wierszMasztu - 1) || (wiersz == wierszMasztu + 1)) && ((kolumna == kolumnaMasztu - 1) || (kolumna == kolumnaMasztu + 1))) {
                    continue;
                }
                if (wiersz < 0 || wiersz >= Gracze.dajWielkoscAktualnejPlanszy() || kolumna < 0 || kolumna >= Gracze.dajWielkoscAktualnejPlanszy()) {
                    continue;
                }
                if (Gracze.dajWartoscZpolaAktualnegoGracza(wiersz, kolumna) == STATEK) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean sprawdzCzyMasztyPrzylegajaDoSiebie(int[][] wspolrzedneStatku) {
        boolean poprawneMaszty;
        switch (wielkoscStatku) {
            case 4, 3 -> poprawneMaszty = czyPoprawneMaszty(wspolrzedneStatku);
            case 2 -> poprawneMaszty = czyPoprawneDwaMaszty(wspolrzedneStatku);
            default -> poprawneMaszty = true;
        }
        return poprawneMaszty;
    }

    static boolean czyPoprawneMaszty(int[][] wspolrzedneStatku) { //refactor?
        boolean poprawneMaszty = false;
        int[][] posortowaneWspolrzedneStatkuWgKolumny = sortujMasztyWgIchKolumny(wspolrzedneStatku, 1);
        int[][] posortowaneWspolrzedneStatkuWgWiersza = sortujMasztyWgIchWiersza(wspolrzedneStatku, 0);
        boolean czyMasztyPrzylegajaWwierszu = sprawdzCzyMasztyPrzylegajaWwierszu(sortujMasztyWgIchKolumny(wspolrzedneStatku, 1));
        if (czyMasztyPrzylegajaWwierszu) {
            if (policzMasztyPrzylegajaceWwierszu(posortowaneWspolrzedneStatkuWgKolumny) == wielkoscStatku) {
                poprawneMaszty = true;
                return poprawneMaszty;
            }
        }
        boolean czyMasztyPrzylegajaWkolumnie = sprawdzCzyMasztyPrzylegajaWkolumnie(sortujMasztyWgIchWiersza(wspolrzedneStatku, 0));
        if (czyMasztyPrzylegajaWkolumnie) {
            if (policzMasztyPrzylegajaceWkolumnie(posortowaneWspolrzedneStatkuWgKolumny) == wielkoscStatku) {
                poprawneMaszty = true;
                return poprawneMaszty;
            }
        }
        if (czyMasztyPrzylegajaWwierszu && czyMasztyPrzylegajaWkolumnie) {
            int przylegajacyWiersz = ustalPrzylegajacyWiersz(posortowaneWspolrzedneStatkuWgKolumny);
            if (sprawdzCzyWierszIkolumnaPrzylegajaDoSiebie(posortowaneWspolrzedneStatkuWgWiersza, przylegajacyWiersz)) {
                if ((wielkoscStatku == 4) && (policzMasztyPrzylegajaceWwierszu(posortowaneWspolrzedneStatkuWgKolumny) == 2) && (policzMasztyPrzylegajaceWkolumnie(posortowaneWspolrzedneStatkuWgKolumny) == 2)) {
                    return poprawneMaszty;
                }
                poprawneMaszty = true;
                return poprawneMaszty;
            }
        }
        return poprawneMaszty;
    }

    public static int[][] sortujMasztyWgIchKolumny(int[][] tablicaDoSortowania, int kolumna) {
        int[] tymczasowaTablica = new int[2];
        for (int wiersz = 0; wiersz < tablicaDoSortowania.length; wiersz++) {
            for (int kolejnyWiersz = 1; kolejnyWiersz < tablicaDoSortowania.length - wiersz; kolejnyWiersz++) {
                if (tablicaDoSortowania[kolejnyWiersz][kolumna] < tablicaDoSortowania[kolejnyWiersz - 1][kolumna]) {
                    tymczasowaTablica[0] = tablicaDoSortowania[kolejnyWiersz - 1][kolumna - 1];
                    tymczasowaTablica[1] = tablicaDoSortowania[kolejnyWiersz - 1][kolumna];
                    tablicaDoSortowania[kolejnyWiersz - 1][kolumna] = tablicaDoSortowania[kolejnyWiersz][kolumna];
                    tablicaDoSortowania[kolejnyWiersz - 1][kolumna - 1] = tablicaDoSortowania[kolejnyWiersz][kolumna - 1];
                    tablicaDoSortowania[kolejnyWiersz][kolumna - 1] = tymczasowaTablica[0];
                    tablicaDoSortowania[kolejnyWiersz][kolumna] = tymczasowaTablica[1];
                }
            }
        }
        /*System.out.println("Maszty posortowane wg kolumny");
        for (int[] rzad : tablicaDoSortowania) {
            for (int pozycja : rzad) {
                System.out.print(pozycja + " ");
            }
            System.out.println("");
        }*/
        return tablicaDoSortowania;
    }

    public static int[][] sortujMasztyWgIchWiersza(int[][] tablicaDoSortowania, int kolumna) {
        int[] tymczasowaTablica = new int[2];
        for (int wiersz = 0; wiersz < tablicaDoSortowania.length; wiersz++) {
            for (int kolejnyWiersz = 1; kolejnyWiersz < tablicaDoSortowania.length - wiersz; kolejnyWiersz++) {
                if (tablicaDoSortowania[kolejnyWiersz][kolumna] < tablicaDoSortowania[kolejnyWiersz - 1][kolumna]) {
                    tymczasowaTablica[0] = tablicaDoSortowania[kolejnyWiersz - 1][kolumna];
                    tymczasowaTablica[1] = tablicaDoSortowania[kolejnyWiersz - 1][kolumna + 1];
                    tablicaDoSortowania[kolejnyWiersz - 1][kolumna] = tablicaDoSortowania[kolejnyWiersz][kolumna];
                    tablicaDoSortowania[kolejnyWiersz - 1][kolumna + 1] = tablicaDoSortowania[kolejnyWiersz][kolumna + 1];
                    tablicaDoSortowania[kolejnyWiersz][kolumna] = tymczasowaTablica[0];
                    tablicaDoSortowania[kolejnyWiersz][kolumna + 1] = tymczasowaTablica[1];
                }
            }
        }

        int[][] kopia = new int[tablicaDoSortowania.length][tablicaDoSortowania[0].length];
        for (int wiersz = 0, tablicaDoSortowaniaLength = tablicaDoSortowania.length; wiersz < tablicaDoSortowaniaLength; wiersz++) {
            kopia[wiersz] =  Arrays.copyOf(tablicaDoSortowania[wiersz],tablicaDoSortowania[wiersz].length);
          /*  for (int kolumnaPrzegladana = 0; kolumnaPrzegladana < tablicaDoSortowania[wiersz].length; kolumnaPrzegladana++) {
                kopia[wiersz][kolumna] = tablicaDoSortowania[wiersz][kolumna];
            }*/
        }
        /*System.out.println("Maszty posortowane wg wiersza");
        for (int[] rzad : tablicaDoSortowania) {
            for (int pozycja : rzad) {
                System.out.print(pozycja + " ");
            }
            System.out.println("");
        }*/
        return tablicaDoSortowania;
    }


    static boolean sprawdzCzyMasztyPrzylegajaWwierszu(int[][] posortowanaTablicaWgKolumnyMasztu) {
        /*System.out.println("Posortowane maszty wg kolumny - podstawione do metody:");
        for (int[] rzad : posortowanaTablicaWgKolumnyMasztu) {
            for (int pozycja : rzad) {
                System.out.print(pozycja + " ");
            }
            System.out.println("");
        }*/
        boolean poprawnyWiersz = false;
        for (int wierszPosortowanejTablicy = 0; wierszPosortowanejTablicy < wielkoscStatku; wierszPosortowanejTablicy++) {
            int wierszMasztu = posortowanaTablicaWgKolumnyMasztu[wierszPosortowanejTablicy][0];
            for (int kolejnyWierszPosortowanejTablicy = wierszPosortowanejTablicy + 1; kolejnyWierszPosortowanejTablicy < wielkoscStatku; kolejnyWierszPosortowanejTablicy++) {
                if (posortowanaTablicaWgKolumnyMasztu[kolejnyWierszPosortowanejTablicy][0] == wierszMasztu) {
                    if (posortowanaTablicaWgKolumnyMasztu[kolejnyWierszPosortowanejTablicy][1] == posortowanaTablicaWgKolumnyMasztu[wierszPosortowanejTablicy][1] + 1 || posortowanaTablicaWgKolumnyMasztu[kolejnyWierszPosortowanejTablicy][1] == posortowanaTablicaWgKolumnyMasztu[wierszPosortowanejTablicy][1] - 1) {
                        poprawnyWiersz = true;
                        break;
                    } else {
                        poprawnyWiersz = false;
                        return poprawnyWiersz;
                    }
                }
            }
        }
        return poprawnyWiersz;
    }

    static int policzMasztyPrzylegajaceWwierszu(int[][] posortowanaTablicaWgKolumnyMasztu) {
        int przylegajaceMaszty = 1;
        for (int wierszPosortowanejTablicy = 0; wierszPosortowanejTablicy < wielkoscStatku; wierszPosortowanejTablicy++) {
            int wierszMasztu = posortowanaTablicaWgKolumnyMasztu[wierszPosortowanejTablicy][0];
            for (int kolejnyWierszPosortowanejTablicy = wierszPosortowanejTablicy + 1; kolejnyWierszPosortowanejTablicy < wielkoscStatku; kolejnyWierszPosortowanejTablicy++) {
                if (posortowanaTablicaWgKolumnyMasztu[kolejnyWierszPosortowanejTablicy][0] == wierszMasztu) {
                    if (posortowanaTablicaWgKolumnyMasztu[kolejnyWierszPosortowanejTablicy][1] == posortowanaTablicaWgKolumnyMasztu[wierszPosortowanejTablicy][1] + 1) {
                        przylegajaceMaszty++;
                    }
                }
            }
        }
        //    System.out.println("Przylegajace w wierszu: " + przylegajaceMaszty);
        return przylegajaceMaszty;
    }

    static int ustalPrzylegajacyWiersz(int[][] posortowanaTablicaWgKolumnyMasztu) {
        int przylegajacyWiersz = -1;
        for (int wierszPosortowanejTablicy = 0; wierszPosortowanejTablicy < wielkoscStatku; wierszPosortowanejTablicy++) {
            int wierszMasztu = posortowanaTablicaWgKolumnyMasztu[wierszPosortowanejTablicy][0];
            for (int kolejnyWierszPosortowanejTablicy = wierszPosortowanejTablicy + 1; kolejnyWierszPosortowanejTablicy < wielkoscStatku; kolejnyWierszPosortowanejTablicy++) {
                if (posortowanaTablicaWgKolumnyMasztu[kolejnyWierszPosortowanejTablicy][0] == wierszMasztu) {
                    if (posortowanaTablicaWgKolumnyMasztu[kolejnyWierszPosortowanejTablicy][1] == posortowanaTablicaWgKolumnyMasztu[wierszPosortowanejTablicy][1] + 1 || posortowanaTablicaWgKolumnyMasztu[kolejnyWierszPosortowanejTablicy][1] == posortowanaTablicaWgKolumnyMasztu[wierszPosortowanejTablicy][1] - 1) {
                        przylegajacyWiersz = wierszMasztu;
                    }
                }
            }
        }
        //    System.out.println("Przylegajacy wiersz: " + przylegajacyWiersz);
        return przylegajacyWiersz;
    }

    static boolean sprawdzCzyMasztyPrzylegajaWkolumnie(int[][] posortowanaTablicaWgWierszuMasztu) {
            /*System.out.println("Posortowane maszty wg wiersza - z metody:");
            for (int[] rzad : posortowanaTablicaWgWierszuMasztu) {
                for (int pozycja : rzad) {
                    System.out.print(pozycja + " ");
                }
                System.out.println("");
            }*/
        boolean poprawnaKolumna = false;
        for (int wierszPosortowanejTablicy = 0; wierszPosortowanejTablicy < wielkoscStatku; wierszPosortowanejTablicy++) {
            int kolumnaMasztu = posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][1];
            for (int kolejnyWierszPosortowanejTablicy = wierszPosortowanejTablicy + 1; kolejnyWierszPosortowanejTablicy < wielkoscStatku; kolejnyWierszPosortowanejTablicy++) {
                if (posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][1] == kolumnaMasztu) {
                    if (posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][0] == posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][0] + 1 || posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][0] == posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][0] - 1) {
                        poprawnaKolumna = true;
                        break;
                    } else {
                        poprawnaKolumna = false;
                        return poprawnaKolumna;
                    }
                }
            }
        }
        return poprawnaKolumna;
    }

    static int policzMasztyPrzylegajaceWkolumnie(int[][] posortowanaTablicaWgWierszuMasztu) {
        int przylegajaceMaszty = 1;
        for (int wierszPosortowanejTablicy = 0; wierszPosortowanejTablicy < wielkoscStatku; wierszPosortowanejTablicy++) {
            int kolumnaMasztu = posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][1];
            for (int kolejnyWierszPosortowanejTablicy = wierszPosortowanejTablicy + 1; kolejnyWierszPosortowanejTablicy < wielkoscStatku; kolejnyWierszPosortowanejTablicy++) {
                if (posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][1] == kolumnaMasztu) {
                    if (posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][0] == posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][0] + 1) {
                        przylegajaceMaszty++;
                    }
                }
            }
        }
        //  System.out.println("Przylegajace w kolumnie: " + przylegajaceMaszty);
        return przylegajaceMaszty;
    }

    static boolean sprawdzCzyWierszIkolumnaPrzylegajaDoSiebie(int[][] posortowanaTablicaWgWierszuMasztu, int przylegajacyWiersz) {
            /*System.out.println("Czy wiersz i kolumna przylegaja do siebie: posortowane maszty wg wiersza");
            for (int[] rzad : posortowanaTablicaWgWierszuMasztu) {
                for (int pozycja : rzad) {
                    System.out.print(pozycja + " ");
                }
                System.out.println("");
            }*/
        boolean czyPrzylegajaDoSiebie = false;
        for (int wierszPosortowanejTablicy = 0; wierszPosortowanejTablicy < wielkoscStatku; wierszPosortowanejTablicy++) {
            int kolumnaMasztu = posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][1];
            for (int kolejnyWierszPosortowanejTablicy = wierszPosortowanejTablicy + 1; kolejnyWierszPosortowanejTablicy < wielkoscStatku; kolejnyWierszPosortowanejTablicy++) {
                if (posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][1] == kolumnaMasztu) {
                    if (posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][0] == posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][0] + 1 || posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][0] == posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][0] - 1) {
                        if (posortowanaTablicaWgWierszuMasztu[kolejnyWierszPosortowanejTablicy][0] == przylegajacyWiersz || posortowanaTablicaWgWierszuMasztu[wierszPosortowanejTablicy][0] == przylegajacyWiersz) {
                            czyPrzylegajaDoSiebie = true;
                            return czyPrzylegajaDoSiebie;
                        }
                    }
                }
            }
        }
        return czyPrzylegajaDoSiebie;
    }

    static boolean czyPoprawneDwaMaszty(int[][] tablicaDwumasztowca) {
        boolean poprawneDwaMaszty = false;
        if (tablicaDwumasztowca[0][0] == tablicaDwumasztowca[1][0]) {
            if ((tablicaDwumasztowca[0][1] == tablicaDwumasztowca[1][1] + 1) || (tablicaDwumasztowca[0][1] == tablicaDwumasztowca[1][1] - 1)) {
                poprawneDwaMaszty = true;
            }
        } else if (tablicaDwumasztowca[0][1] == tablicaDwumasztowca[1][1]) {
            if ((tablicaDwumasztowca[0][0] == tablicaDwumasztowca[1][0] + 1) || (tablicaDwumasztowca[0][0] == tablicaDwumasztowca[1][0] - 1)) {
                poprawneDwaMaszty = true;
            }
        }
        return poprawneDwaMaszty;
    }
}
