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

    private static boolean checkIfCorrectMasts(int[][] shipCoordinates) {
        boolean correctMasts = false;
        int[][] shipCoordinatesSortedByVertical = sortMastsByVerticalCoordinate(shipCoordinates, 1);
        int[][] shipCoordinatesSortedByHorizontal = sortMastsByHorizontalCoordinate
                (shipCoordinates, 0);
        boolean mastsAdjacentInRow = checkIfMastsAdjacentInRow(sortMastsByVerticalCoordinate
                (shipCoordinates, 1));
        if (mastsAdjacentInRow) {
            if (countMastsAdjacentInRow(shipCoordinatesSortedByVertical) == shipSize) {
                correctMasts = true;
                return correctMasts;
            }
        }
        boolean mastsAdjacentInColumn = checkIfMastsAdjacentInColumn(sortMastsByHorizontalCoordinate
                (shipCoordinates, 0));
        if (mastsAdjacentInColumn) {
            if (countMastsAdjacentInColumn(shipCoordinatesSortedByVertical) == shipSize) {
                correctMasts = true;
                return correctMasts;
            }
        }
        if (mastsAdjacentInRow && mastsAdjacentInColumn) {
            int adjacentRow = findAdjacentRow(shipCoordinatesSortedByVertical);
            if (checkIfRowAndColumnAdjacentToEachOther(shipCoordinatesSortedByHorizontal, adjacentRow)) {
                if ((shipSize == 4) && (countMastsAdjacentInRow(shipCoordinatesSortedByVertical) == 2) &&
                        (countMastsAdjacentInColumn(shipCoordinatesSortedByVertical) == 2)) {
                    return correctMasts;
                }
                correctMasts = true;
                return correctMasts;
            }
        }
        return correctMasts;
    }

    private static int[][] sortMastsByVerticalCoordinate(int[][] masts, int columnOfVerticalCoordinate) {
        int[] tempTable = new int[2];
        for (int row = 0; row < masts.length; row++) {
            for (int nextRow = 1; nextRow < masts.length - row; nextRow++) {
                if (masts[nextRow][columnOfVerticalCoordinate] < masts[nextRow - 1][columnOfVerticalCoordinate]) {
                    tempTable[0] = masts[nextRow - 1][columnOfVerticalCoordinate - 1];
                    tempTable[1] = masts[nextRow - 1][columnOfVerticalCoordinate];
                    masts[nextRow - 1][columnOfVerticalCoordinate] = masts[nextRow][columnOfVerticalCoordinate];
                    masts[nextRow - 1][columnOfVerticalCoordinate - 1] = masts[nextRow][columnOfVerticalCoordinate - 1];
                    masts[nextRow][columnOfVerticalCoordinate - 1] = tempTable[0];
                    masts[nextRow][columnOfVerticalCoordinate] = tempTable[1];
                }
            }
        }
        return masts;
    }

    private static int[][] sortMastsByHorizontalCoordinate(int[][] masts, int columnOfHorizontalCoordinate) {
        int[] tempTable = new int[2];
        for (int row = 0; row < masts.length; row++) {
            for (int nextRow = 1; nextRow < masts.length - row; nextRow++) {
                if (masts[nextRow][columnOfHorizontalCoordinate] < masts[nextRow - 1][columnOfHorizontalCoordinate]) {
                    tempTable[0] = masts[nextRow - 1][columnOfHorizontalCoordinate];
                    tempTable[1] = masts[nextRow - 1][columnOfHorizontalCoordinate + 1];
                    masts[nextRow - 1][columnOfHorizontalCoordinate] = masts[nextRow][columnOfHorizontalCoordinate];
                    masts[nextRow - 1][columnOfHorizontalCoordinate + 1] = masts[nextRow][columnOfHorizontalCoordinate + 1];
                    masts[nextRow][columnOfHorizontalCoordinate] = tempTable[0];
                    masts[nextRow][columnOfHorizontalCoordinate + 1] = tempTable[1];
                }
            }
        }
// can be rewritten using the copyOfMasts
        int[][] copyOfMasts = new int[masts.length][masts[0].length];
        for (int row = 0; row < masts.length; row++) {
            copyOfMasts[row] = Arrays.copyOf(masts[row], masts[row].length);
        }
        return masts;
    }


    private static boolean checkIfMastsAdjacentInRow(int[][] posortowanaTablicaWgKolumnyMasztu) {
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

    static int countMastsAdjacentInRow(int[][] posortowanaTablicaWgKolumnyMasztu) {
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

    static int findAdjacentRow(int[][] posortowanaTablicaWgKolumnyMasztu) {
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

    static boolean checkIfMastsAdjacentInColumn(int[][] posortowanaTablicaWgWierszuMasztu) {
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

    static int countMastsAdjacentInColumn(int[][] posortowanaTablicaWgWierszuMasztu) {
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

    static boolean checkIfRowAndColumnAdjacentToEachOther(int[][] posortowanaTablicaWgWierszuMasztu, int przylegajacyWiersz) {
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
