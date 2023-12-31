package battleships;

public class Players {
    private static int[][] playersOneBoard = new int[10][10];
    private static int[][] playersTwoBoard = new int[10][10];
    private static final int PLAYER1 = 0;
    private static final int PLAYER2 = 1;
    private static int currentPlayer = PLAYER1;
    private static String playerOne = "Agnieszka";
    private static String playerTwo = "Gracz Drugi";

    private static int[][] getCurrentPlayersBoard() {
        return currentPlayer == PLAYER1 ? playersOneBoard : playersTwoBoard;
    }

    private static int[][] getOpponentsBoard() {
        return currentPlayer == PLAYER1 ? playersTwoBoard : playersOneBoard;
    }

    static int getCurrentBoardSize() {
        int[][] currentBoard = getCurrentPlayersBoard();
        return currentBoard.length;
    }

    static String getCurrentPlayersName() {
        return currentPlayer == PLAYER1 ? playerOne : playerTwo;
    }

    static int getCurrentPlayer() {
        return currentPlayer == PLAYER1 ? PLAYER1 : PLAYER2;
    }

    static void printIfCurrentPlayerIsHuman(String toPrint) {
        if (currentPlayer == PLAYER1) {
            System.out.println(toPrint);
        }
    }

    static void printIfCurrentPlayerIsComputer(String toPrint) {
        if (currentPlayer == PLAYER2) {
            System.out.println(toPrint);
        }
    }

    static int establishVerticalCoordinateIfHuman() {
        return HumanAlgorithm.establishVerticalCoordinate();
    }

    static int establishHorizontalCoordinateIfHuman() {
        return HumanAlgorithm.establishHorizontalCoordinate();
    }

    static int establishVerticalCoordinateIfComputer() {
        return ComputerAlgorithm.drawVerticalCoordinate();
    }

    static int establishHorizontalCoordinateIfComputer() {
        return ComputerAlgorithm.drawHorizontalCoordinate();
    }

    static void changePlayer() {
        currentPlayer = currentPlayer == PLAYER1 ? PLAYER2 : PLAYER1;
    }

    static int getValueFromCurrentPlayersSquare(int horizontalCoordinate, int verticalCoordinate) {
        int[][] currentBoard = getCurrentPlayersBoard();
        return currentBoard[horizontalCoordinate][verticalCoordinate];
    }

    static int getValueFromOpponentsSquare(int horizontalCoordinate, int verticalCoordinate) {
        int[][] opponentsBoard = getOpponentsBoard();
        return opponentsBoard[horizontalCoordinate][verticalCoordinate];
    }

    static void addMastToCurrentSquare(int horizontalCoordinate, int verticalCoordinate) {
        int[][] currentBoard = getCurrentPlayersBoard();
        currentBoard[horizontalCoordinate][verticalCoordinate] = Game.SHIP;
    }

    static void addSymbolToOpponentsBoard(int horizontalCoordinate, int verticalCoordinate, int symbol) {
        int[][] opponentsBoard = getOpponentsBoard();
        opponentsBoard[horizontalCoordinate][verticalCoordinate] = symbol;
    }

    static boolean checkIfShipsLeftToShoot() {
        boolean unshotShip = false;
        int[][] opponentsBoard = getOpponentsBoard();
        for (int row = 0; row < opponentsBoard.length; row++) {
            for (int column = 0; column < opponentsBoard[row].length; column++) {
                if (opponentsBoard[row][column] == Game.SHIP){
                    unshotShip = true;
                    return unshotShip;
                }
            }
        }
        return unshotShip;
    }

    public static boolean isHumansMove() {
        return currentPlayer == PLAYER1;
    }
    public static boolean isComputersMove() {
        return currentPlayer == PLAYER2;
    }

    public static void fillInHumansBoardForTesting() {
        playersOneBoard = new int[][]{
                {Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.EMPTY,Game.SHIP,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.SHIP,Game.SHIP,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.EMPTY,Game.SHIP,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.SHIP,Game.SHIP,Game.SHIP,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY},
                {Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY,Game.EMPTY}
        };
    }
}
