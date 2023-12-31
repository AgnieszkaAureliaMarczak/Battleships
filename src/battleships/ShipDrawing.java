package battleships;

import java.util.Arrays;

import static battleships.Game.SHIP;
import static battleships.Game.shipSize;

public class ShipDrawing {
    static int numberOfCorrectMasts = 0;

    static int[][] drawShip() {
        int[][] masts = new int[shipSize][2];
        fillInArrayWithSameNumber(masts, 100);
        drawMasts(masts);
        if (!checkIfAllShipsMastsAdjacentToEachOther(masts)) {
            Players.printIfCurrentPlayerIsHuman("Ups. Narysowany statek jest niepoprawny. " +
                    "Każdy maszt statku musi stykać się z jego kolejnym masztem ścianką boczną. Spróbuj jeszcze raz.");
            if (Players.isHumansMove()) {
                Game.printBoard();
            }
            return drawShip();
        }
        return masts;
    }

    private static void fillInArrayWithSameNumber(int[][] arrayToFill, int fill) {
        for (int[] row : arrayToFill) {
            Arrays.fill(row, fill);
        }
    }

    private static void drawMasts(int[][] masts) {
        for (int i = 0; i < shipSize; i++) {
            int[] mast = getMastCoordinates();
            int horizontalCoordinate = mast[0];
            int verticalCoordinate = mast[1];
            if (Players.isHumansMove()) {
                if (!checkIfWithinBoard(horizontalCoordinate, verticalCoordinate)) {
                    System.out.println("Podane pole jest poza planszą. Spróbuj jeszcze raz.");
                    drawMasts(masts);
                    return;
                }
            }
            if (!checkIfEmptySquare(masts, horizontalCoordinate, verticalCoordinate)) {
                Players.printIfCurrentPlayerIsHuman("Podane pole jest już zajęte. Spróbuj jeszcze raz.");
                drawMasts(masts);
                return;
            }
            if (!checkIfMastNotAdjacentToAnotherShip(horizontalCoordinate, verticalCoordinate)) {
                Players.printIfCurrentPlayerIsHuman("Wygląda na to, że chcesz narysować maszt " +
                        "przylegający do innego statku. \n" +
                        "Pamiętaj, że statki nie mogę “dotykać” się żadnym bokiem masztu. \n" +
                        "Spróbuj jeszcze raz.");
                drawMasts(masts);
                return;
            }
            masts[numberOfCorrectMasts] = new int[]{horizontalCoordinate, verticalCoordinate};
            if (Players.isHumansMove()) {
                Game.printBoard(masts);
            }
            numberOfCorrectMasts++;
            if (numberOfCorrectMasts == shipSize) {
                numberOfCorrectMasts = 0;
                break;
            }
        }
    }

    static int[] getMastCoordinates() {
        int[] mast = new int[2];
        if (Players.isHumansMove()) {
            mast[0] = Players.establishHorizontalCoordinateIfHuman();
            mast[1] = Players.establishVerticalCoordinateIfHuman();
        } else {
            mast[0] = Players.establishHorizontalCoordinateIfComputer();
            mast[1] = Players.establishVerticalCoordinateIfComputer();
        }
        return mast;
    }

    static boolean checkIfWithinBoard(int horizontalCoordinate, int verticalCoordinate) {
        return horizontalCoordinate >= 0 && horizontalCoordinate <= Players.getBoardSize() - 1 &&
                verticalCoordinate >= 0 && verticalCoordinate <= Players.getBoardSize() - 1;
    }

    private static boolean checkIfEmptySquare(int[][] masts, int horizontalCordinate, int verticalCoordinate) {
        for (int row = 0; row < masts.length; row++) {
            for (int column = 0; column < masts[row].length; column++) {
                if ((masts[row][0] == horizontalCordinate) && (masts[row][1] == verticalCoordinate)) {
                    return false;
                }
            }
        }
        return Players.getValueFromCurrentPlayersSquare(horizontalCordinate, verticalCoordinate) != SHIP;
    }

    private static boolean checkIfMastNotAdjacentToAnotherShip(int horizontalCordinate, int verticalCoordinate) {
        for (int row = horizontalCordinate - 1; row <= horizontalCordinate + 1; row++) {
            for (int column = verticalCoordinate - 1; column <= verticalCoordinate + 1; column++) {
                if (((row == horizontalCordinate - 1) || (row == horizontalCordinate + 1)) &&
                        ((column == verticalCoordinate - 1) || (column == verticalCoordinate + 1))) {
                    continue;
                }
                if (row < 0 || row >= Players.getBoardSize() || column < 0 ||
                        column >= Players.getBoardSize()) {
                    continue;
                }
                if (Players.getValueFromCurrentPlayersSquare(row, column) == SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkIfAllShipsMastsAdjacentToEachOther(int[][] shipCoordinates) {
        boolean correctMasts;
        switch (shipSize) {
            case 4, 3 -> correctMasts = checkIfCorrectMasts(shipCoordinates);
            case 2 -> correctMasts = checkIfOnlyTwoMastsCorrect(shipCoordinates);
            default -> correctMasts = true;
        }
        return correctMasts;
    }

    private static boolean checkIfCorrectMasts(int[][] wspolrzedneStatku) { //refactor?
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
            kopia[wiersz] = Arrays.copyOf(tablicaDoSortowania[wiersz], tablicaDoSortowania[wiersz].length);
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

    static boolean checkIfOnlyTwoMastsCorrect(int[][] tablicaDwumasztowca) {
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
