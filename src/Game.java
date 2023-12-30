import java.util.Scanner;

public class Game {
    static char[] gridLetters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
    static int shipSize;
    public static final int EMPTY = 0;
    public static final int SHIP = 1;
    public static final int HIT = 2;
    public static final int MISS = 3;
    public static final int TRIAL_SHIP = 4;
    public static final char EMPTY_SYMBOL = ' ';
    public static final char SHIP_SYMBOL = '\u25A1';
    public static final char HIT_SYMBOL = 'X';
    public static final char MISS_SYMBOL = '*';
    public static final char TRIAL_SHIP_SYMBOL = '\u2713';

    public static void main(String[] args) {
        prepareBothBoardsForGame();
        System.out.println("Zaczynamy gre!");
        do {
            System.out.println();
            System.out.println(Players.getCurrentPlayersName() + " twój ruch.");
            printOpponentsBoard();
            int[] playersMove = getPlayersMove();
            int horizontalCoordinate = playersMove[0];
            int verticaLCoordinate = playersMove[1];
            if (checkIfMastHit(playersMove)) {
                Players.addSymbolToOpponentsBoard(horizontalCoordinate, verticaLCoordinate, HIT);
                if (isEntireShipHit(horizontalCoordinate, verticaLCoordinate, 'X')) {
                    System.out.println("Trafiony - zatopiony!");
                } else {
                    System.out.println("Trafiony!");
                }
                if (!Players.checkIfShipsLeftToShoot()) {
                    Players.printIfCurrentPlayerIsHuman("Koniec gry! Wygrałeś. Gratulacje!");
                    Players.printIfCurrentPlayerIsComputer("Koniec gry! Wygrał " + Players.getCurrentPlayersName());
                    return;
                }
                printOpponentsBoard();
                if (Players.isHumansMove()) {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Wciśnij \"Enter\", żeby kontynuować grę.");
                    sc.nextLine();
                }
            } else {
                Players.addSymbolToOpponentsBoard(horizontalCoordinate, verticaLCoordinate, MISS);
                System.out.println("Pudło!");
                printOpponentsBoard();
                if (Players.isHumansMove()) {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Wciśnij \"Enter\", żeby kontynuować grę.");
                    sc.nextLine();
                }
            }
            Players.changePlayer();
        } while (Players.checkIfShipsLeftToShoot());
    }

    static void prepareBothBoardsForGame() {
        for (int i = 0; i < 2; i++) {
            Players.printIfCurrentPlayerIsHuman(Players.getCurrentPlayersName() + ", witaj w grze w statki!\nOto " +
                    "twoja plansza:");
            if (Players.isHumansMove()) {
                printBoard();
            }
            Players.printIfCurrentPlayerIsHuman("Narysuj swoje statki.\nDo dyspozycji masz:\n- 1 czteromasztowiec\n- " +
                    "2 trzymasztowce " +
                    "\n- 3 dwumasztowce\n- 4 jednomasztowce\nStatki możesz dowolnie ustawić, obrócić i wygiąć z " +
                    "zachowaniem zasady,\n" +
                    "że każdy maszt jednego statku musi stykać się z jego kolejnym masztem ścianką boczną \n" +
                    "(nie może łączyć się na ukos)" +
                    " oraz dwa statki nie mogę “dotykać” się żadnym bokiem masztu.\n" +
                    "Zaczynamy grę!");
            /*if (Gracze.czyTuraKomputera()) { // fragment tylko do testow!
                uzupelnijPlanszeStatkami();
            } else {
                Gracze.uzupelnijPlanszeCzlowiekaDoTestow();
            }*/
            fillInBoardWithShips();
            if (Players.isHumansMove()) {
                Scanner sc = new Scanner(System.in);
                System.out.println("Twoja plansza jest gotowa. Czas na drugiego gracza.");
                System.out.println("Wciśnij \"Enter\", żeby kontynuować grę.");
                sc.nextLine();
            }
            Players.changePlayer();
        }
    }

    static void printBoard() {
        System.out.print("\t");
        for (char gridLetter : gridLetters) {
            System.out.print(gridLetter + " | ");
        }
        System.out.println();
        for (int row = 1; row < 11; row++) {
            if (row <= 9) {
                System.out.print("0" + row + "| ");
            } else {
                System.out.print(row + "| ");
            }
            for (int column = 1; column < 11; column++) {
                int square = Players.getValueFromCurrentPlayersSquare(row - 1, column - 1);
                char squareSymbol = switch (square) {
                    case EMPTY -> EMPTY_SYMBOL;
                    case SHIP -> SHIP_SYMBOL;
                    case HIT -> HIT_SYMBOL;
                    case MISS -> MISS_SYMBOL;
                    default -> 0;
                };
                System.out.print(squareSymbol + " | ");
            }
            System.out.println();
        }
    }

    static void printBoard(int[][] trialShip) {
        System.out.print("\t");
        for (char gridLetter : gridLetters) {
            System.out.print(gridLetter + " | ");
        }
        System.out.println();
        for (int row = 1; row < 11; row++) {
            if (row <= 9) {
                System.out.print("0" + row + "| ");
            } else {
                System.out.print(row + "| ");
            }
            for (int column = 1; column < 11; column++) {
                char squareSymbol = getSquareSymbol(trialShip, row, column);
                System.out.print(squareSymbol + " | ");
            }
            System.out.println();
        }
    }

    private static char getSquareSymbol(int[][] trialShip, int row, int column) {
        int square = -2;
        for (int mast = 0; mast < trialShip.length; mast++) {
            if (trialShip[mast][0] == row - 1 && trialShip[mast][1] == column - 1) {
                square = TRIAL_SHIP;
            }
        }
        if (square != TRIAL_SHIP) {
            square = Players.getValueFromCurrentPlayersSquare(row - 1, column - 1);
        }
        return switch (square) {
            case EMPTY -> EMPTY_SYMBOL;
            case SHIP -> SHIP_SYMBOL;
            case HIT -> HIT_SYMBOL;
            case MISS -> MISS_SYMBOL;
            case TRIAL_SHIP -> TRIAL_SHIP_SYMBOL;
            default -> 0;
        };
    }

    static void fillInBoardWithShips() {
        shipSize = 4;
        int numberOfSameSizeShips;
        do {
            numberOfSameSizeShips = establishNumberOfSameSizeShips();
            for (int shipNumber = 0; shipNumber < numberOfSameSizeShips; shipNumber++) {
                Players.printIfCurrentPlayerIsHuman("Narysuj statek. Ilosc masztów: " + shipSize);
                int[][] shipCoordinates = ShipCreator.drawShip();
                addShipToBoard(shipCoordinates);
                printBoard();
            }
            shipSize--;
        } while (shipSize > 0);
    }

    static int establishNumberOfSameSizeShips() {
        return switch (shipSize) {
            case 4 -> 1;
            case 3 -> 2;
            case 2 -> 3;
            case 1 -> 4;
            default -> 0;
        };
    }

    static void addShipToBoard(int[][] shipCoordinates) {
        for (int mast = 0; mast < shipCoordinates.length; mast++) {
            int mastHorizontalCoordinate = shipCoordinates[mast][0];
            int mastVerticalCoordinate = shipCoordinates[mast][1];
            Players.addMastToCurrentSquare(mastHorizontalCoordinate, mastVerticalCoordinate);
        }
    }

    static int[] getPlayersMove() {
        int[] mast = ShipCreator.getMastCoordinates();
        int horizontalCoordinate = mast[0];
        int verticalCoordinate = mast[1];
        if (Players.isHumansMove()) {
            if (!ShipCreator.checkIfWithinBoard(horizontalCoordinate, verticalCoordinate)) {
                System.out.println("Podane pole jest poza planszą. Spróbuj jeszcze raz.");
                return getPlayersMove();
            }
        }
        if (checkIfRepeatedMove(mast)) {
            Players.printIfCurrentPlayerIsHuman("Podane pole już było strzelane. Spróbuj jeszcze raz.");
            return getPlayersMove();
        }
        return mast;
    }

    static boolean checkIfRepeatedMove(int[] playersMove) {
        int horizontalCoordinate = playersMove[0];
        int verticalCoordinate = playersMove[1];
        return (Players.getValueFromOpponentsSquare(horizontalCoordinate, verticalCoordinate) == HIT) ||
                (Players.getValueFromOpponentsSquare(horizontalCoordinate, verticalCoordinate) == MISS);
    }

    static boolean checkIfMastHit(int[] playersMove) {
        int horizontalCoordinate = playersMove[0];
        int verticalCoordinate = playersMove[1];
        return Players.getValueFromOpponentsSquare(horizontalCoordinate, verticalCoordinate) == SHIP;
    }

    static void printOpponentsBoard() {
        System.out.print("\t");
        for (int i = 0; i < gridLetters.length; i++) {
            System.out.print(gridLetters[i] + " | ");
        }
        System.out.println();
        for (int row = 1; row < 11; row++) {
            if (row <= 9) {
                System.out.print("0" + row + "| ");
            } else {
                System.out.print(row + "| ");
            }
            for (int column = 1; column < 11; column++) {
                int square = Players.getValueFromOpponentsSquare(row - 1, column - 1);
                char squareSymbol = switch (square) {
                    case EMPTY, SHIP -> EMPTY_SYMBOL;
                    case HIT -> HIT_SYMBOL;
                    case MISS -> MISS_SYMBOL;
                    default -> 0;
                };
                System.out.print(squareSymbol + " | ");
            }
            System.out.println();
        }
    }

    public static boolean isEntireShipHit(int mastHorizontalCoordinate, int mastVerticalCoordinate, char side) {
        if (Players.getValueFromOpponentsSquare(mastHorizontalCoordinate, mastVerticalCoordinate) == SHIP) {
            return false;
        }
        boolean result = true;

        if ((mastHorizontalCoordinate - 1 >= 0) && (mastHorizontalCoordinate - 1 < Players.getCurrentBoardSize()) &&
                (mastVerticalCoordinate >= 0) && (mastVerticalCoordinate < Players.getCurrentBoardSize())) {
            int neighbour1 = Players.getValueFromOpponentsSquare(mastHorizontalCoordinate - 1,
                    mastVerticalCoordinate);
            if (side == 'N') {
                neighbour1 = MISS;
            }
            if ((neighbour1 != MISS) && (neighbour1 != EMPTY)) {
                result = result && isEntireShipHit(mastHorizontalCoordinate - 1,
                        mastVerticalCoordinate, 'S');
            }
        }

        if ((mastHorizontalCoordinate >= 0) && (mastHorizontalCoordinate < Players.getCurrentBoardSize())
                && (mastVerticalCoordinate + 1 >= 0) && (mastVerticalCoordinate + 1 < Players.getCurrentBoardSize())) {
            int neighbour2 = Players.getValueFromOpponentsSquare(mastHorizontalCoordinate,
                    mastVerticalCoordinate + 1);
            if (side == 'E') {
                neighbour2 = MISS;
            }
            if ((neighbour2 != MISS) && (neighbour2 != EMPTY)) {
                result = result && isEntireShipHit(mastHorizontalCoordinate,
                        mastVerticalCoordinate + 1, 'W');
            }
        }

        if ((mastHorizontalCoordinate + 1 >= 0) && (mastHorizontalCoordinate + 1 < Players.getCurrentBoardSize()) &&
                (mastVerticalCoordinate >= 0) && (mastVerticalCoordinate < Players.getCurrentBoardSize())) {
            int neighbour3 = Players.getValueFromOpponentsSquare(mastHorizontalCoordinate + 1,
                    mastVerticalCoordinate);
            if (side == 'S') {
                neighbour3 = MISS;
            }
            if ((neighbour3 != MISS) && (neighbour3 != EMPTY)) {
                result = result && isEntireShipHit(mastHorizontalCoordinate + 1,
                        mastVerticalCoordinate, 'N');
            }
        }
        if ((mastHorizontalCoordinate >= 0) && (mastHorizontalCoordinate < Players.getCurrentBoardSize()) &&
                (mastVerticalCoordinate - 1 >= 0) && (mastVerticalCoordinate - 1 < Players.getCurrentBoardSize())) {
            int neighbour4 = Players.getValueFromOpponentsSquare(mastHorizontalCoordinate,
                    mastVerticalCoordinate - 1);
            if (side == 'W') {
                neighbour4 = MISS;
            }
            if ((neighbour4 != MISS) && (neighbour4 != EMPTY)) {
                result = result && isEntireShipHit(mastHorizontalCoordinate,
                        mastVerticalCoordinate - 1, 'E');
            }
        }
        return result;
    }

    private int[] getNeighbours(int mastHorizontalCoordinate, int mastVerticalCoordinate) {
        int[] neighbours = new int[4];
        neighbours[0] = Players.getValueFromOpponentsSquare(mastHorizontalCoordinate - 1,
                mastVerticalCoordinate);
        neighbours[1] = Players.getValueFromOpponentsSquare(mastHorizontalCoordinate,
                mastVerticalCoordinate + 1);
        neighbours[2] = Players.getValueFromOpponentsSquare(mastHorizontalCoordinate + 1,
                mastVerticalCoordinate);
        neighbours[3] = Players.getValueFromOpponentsSquare(mastHorizontalCoordinate,
                mastVerticalCoordinate - 1);
        return neighbours;
    }
}
