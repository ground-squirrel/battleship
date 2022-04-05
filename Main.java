package battleship;


import java.util.*;


class Battlefield {
    /**
     * The field with ships
     */
    private final boolean[][] field = new boolean[10][10];

    /**
     * Hits and misses
     */
    private final boolean[][] shots = new boolean[10][10];

    private final char[][] fullField = new char[10][10];

    private int shipsSunkNumber = 0;

    private final String owner;

    /**
     * Get owner name
     * @return - owner name
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Constructor
     * @param owner - owner name
     */
    public Battlefield(String owner) {
        this.owner = owner;
    }

    /**
     * Different ship types and their sizes
     */
    enum ShipType {
        AIRCRAFT_CARRIER(5),
        BATTLESHIP(4),
        SUBMARINE(3),
        CRUISER(3),
        DESTROYER(2);

        final int size;

        ShipType(int size) {
            this.size = size;
        }

        int getSize() {
            return this.size;
        }

        String getName() {
            String str = this.name().replaceAll("_", " ");
            return str.charAt(0) + str.substring(1).toLowerCase();
        }

        String getFullName() {
            return String.format("%s (%d cells)", this.getName(), this.getSize());
        }

    }


    /**
     * Increase count of ships sunk by one
     */
    public void increaseShipSunkNumber() {
        shipsSunkNumber++;
    }

    public int getShipsSunkNumber() {
        return shipsSunkNumber;
    }

    /**
     * Check whether coordinates are valid
     * @param noseCoord - nose coordinates
     * @param tailCoord - tail coordinates
     * @param st - ship type
     * @return - flag
     */
    public boolean areCoordinatesValid(String[] noseCoord, String[] tailCoord, ShipType st) {
        try {

            if (noseCoord.length < 2 || tailCoord.length < 2) {
                throw new Exception("Error!");
            }

            int noseCoordLetterValue = noseCoord[0].charAt(0);
            int tailCoordLetterValue = tailCoord[0].charAt(0);

            int noseCoordNumberValue = Integer.parseInt(noseCoord[1]);
            int tailCoordNumberValue = Integer.parseInt(tailCoord[1]);

            //check that the coordinates are within the field range
            if (noseCoordLetterValue < (int) 'A' || noseCoordLetterValue > (int) 'J'
                    || tailCoordLetterValue < (int) 'A' || tailCoordLetterValue > (int) 'J') {
                throw new Exception("Wrong ship location!");
            }

            if (noseCoordNumberValue < 1 || noseCoordNumberValue > field.length
                    || tailCoordNumberValue < 1 || tailCoordNumberValue > field.length) {
                throw new Exception("Wrong ship location!");
            }

            //check that ship is not diagonal and of the right size
            if (tailCoordLetterValue - noseCoordLetterValue + 1 != st.getSize()
                    && tailCoordNumberValue - noseCoordNumberValue + 1 != st.getSize()) {
                throw new Exception("Wrong length of the " + st.getName() + "!");
            }

            if (noseCoordLetterValue != tailCoordLetterValue
                    && tailCoordLetterValue - noseCoordLetterValue + 1 != st.getSize()) {
                throw new Exception("Wrong ship location!");
            }
            if (noseCoordNumberValue != tailCoordNumberValue
                    && tailCoordNumberValue - noseCoordNumberValue + 1 != st.getSize()) {
                throw new Exception("Wrong ship location!");
            }

        } catch (Exception ex) {
            System.out.print("Error! " + ex.getMessage() + " Try again: ");
            return false;
        }

        return true;
    }

    public boolean isPlacementValid(String[] nose, String[] tail, ShipType st) {

        if(!areCoordinatesValid(nose, tail, st)) {
            return false;
        }

        int noseX = nose[0].charAt(0) - (int)'A';
        int noseY = Integer.parseInt(nose[1]) - 1 ;
        int tailX = tail[0].charAt(0) - (int)'A';
        int tailY = Integer.parseInt(tail[1]) - 1;

        //touching or crossing with other ships
        int zoneNoseX = (noseX == 0) ? 0 : (noseX - 1);
        int zoneNoseY = (noseY == 0) ? 0 : (noseY - 1);
        int zoneTailX = (tailX == field.length - 1) ? tailX : (tailX + 1);
        int zoneTailY = (tailY == field.length - 1) ? tailY : (tailY + 1);

        for (int i = zoneNoseX; i <= zoneTailX; i++) {
            for (int j = zoneNoseY; j < zoneTailY; j++) {
                if (field[i][j]) {
                    System.out.print("Error! You placed it too close to another one. Try again: ");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Print the field with ships placed
     * @param fogOfWar - fog of war flag
     */
    public void printField(boolean fogOfWar) {
        System.out.println();

        for (int i = - 1; i < field.length; i++) {
            for (int j = -1; j < field[0].length; j++) {
                if (i == -1 && j == -1) {
                    System.out.print("  ");
                } else if (i == -1) {
                    System.out.printf("%d ", j + 1);
                } else if (j == -1) {
                    System.out.printf("%s ", (char) (65 + i));
                } else {
                    if (shots[i][j]) {
                        System.out.print(field[i][j] ? "X" : "M");
                    } else {
                        System.out.print(fogOfWar ? "~" : (field[i][j] ? "O" : "~"));
                    }
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }


    /**
     * Place a ship
     * @param coordinates - nose and tail coordinates
     * @param st - ship type
     */
    public boolean placeShip(String coordinates, ShipType st) {

        String[] coordArray = coordinates.split("\\s+");
        if (coordArray.length != 2) {
            System.out.println("Error");
            return false;
        }

        String[] noseCoord = new String[] {coordArray[0].substring(0, 1), coordArray[0].substring(1)};
        String[] tailCoord = new String[] {coordArray[1].substring(0, 1), coordArray[1].substring(1)};

        if (isGreater(tailCoord, noseCoord)) {
            String[] tmp = tailCoord;
            tailCoord = noseCoord;
            noseCoord = tmp;
        }

        //check that coordinates are valid
        if (isPlacementValid(noseCoord, tailCoord, st)) {
            int noseX = noseCoord[0].charAt(0) - (int)'A';
            int noseY = Integer.parseInt(noseCoord[1]) - 1 ;
            int tailX = tailCoord[0].charAt(0) - (int)'A';
            int tailY = Integer.parseInt(tailCoord[1]) - 1;

            for (int i = noseX; i <= tailX; i++) {
                for (int j = noseY; j <= tailY; j++) {
                    field[i][j] = true;
                    fullField[i][j] = 'O';
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Comparison of alphanumerical coordinates
     * @param first - first coordinate
     * @param second - second coordinate
     * @return flag
     */
    public boolean isGreater(String[] first, String[] second) {
        if (first[0].charAt(0) < second[0].charAt(0)) {
            return true;
        } else return first[0].charAt(0) == second[0].charAt(0)
                && Integer.parseInt(first[1]) < Integer.parseInt(second[1]);
    }

    /**
     * Take a shot
     * @param shot - shot coordinates
     * @return - is the ship hit
     */
    public boolean takeAShot(String shot) throws Exception {

        try {
            String[] coord = new String[]{shot.substring(0, 1), shot.substring(1)};
            int row = coord[0].charAt(0) - (int) 'A';
            int column = Integer.parseInt(coord[1]) - 1;

            shots[row][column] = true;

            fullField[row][column] = field[row][column] ? 'X' : 'M';

            return field[row][column];
        } catch (Exception ex) {
            throw new Exception("Error! You entered the wrong coordinates!");
        }

    }


    /**
     * Check if the ship is sunk
     * @param shot - shot coordinates
     * @return - number of ships sunk
     */
    public boolean isShipSunk(String shot) {

        String[] coord = new String[]{shot.substring(0, 1), shot.substring(1)};
        int row = coord[0].charAt(0) - (int) 'A';
        int column = Integer.parseInt(coord[1]) - 1;

        List<List<Integer>> nearbyPoints = new ArrayList<>();

        if (row > 0 && row < fullField.length - 1) {
            nearbyPoints.add(new ArrayList<>(Arrays.asList(row - 1, column)));
            nearbyPoints.add(new ArrayList<>(Arrays.asList(row + 1, column)));
        } else if (row == 0) {
            nearbyPoints.add(new ArrayList<>(Arrays.asList(row + 1, column)));
        } else if (row == fullField.length - 1) {
            nearbyPoints.add(new ArrayList<>(Arrays.asList(row - 1, column)));
        }

        if (column > 0 && column < fullField[0].length - 1) {
            nearbyPoints.add(new ArrayList<>(Arrays.asList(row, column - 1)));
            nearbyPoints.add(new ArrayList<>(Arrays.asList(row, column + 1)));
        } else if (column == 0) {
            nearbyPoints.add(new ArrayList<>(Arrays.asList(row, column + 1)));
        } else if (column == fullField[0].length - 1) {
            nearbyPoints.add(new ArrayList<>(Arrays.asList(row, column - 1)));
        }

        for (List<Integer> point : nearbyPoints) {
            if (fullField[point.get(0)][point.get(1)] == 'O') {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the point was already shot at
     * @param shot - coordinates of a shot
     * @return - flag
     */
    public boolean wasShotAlready(String shot) {
        String[] coord = new String[]{shot.substring(0, 1), shot.substring(1)};
        int row = coord[0].charAt(0) - (int) 'A';
        int column = Integer.parseInt(coord[1]) - 1;

        return shots[row][column];
    }
}

class Game {
    List<Battlefield> battlefieldList;
    int currentPlayerNumber;

    int playerCount;

    public Game(Battlefield b1, Battlefield b2) {
        battlefieldList = new ArrayList<>();
        battlefieldList.add(b1);
        battlefieldList.add(b2);
        currentPlayerNumber = 0;
        playerCount = battlefieldList.size();
    }

    public void switchPlayer() {
        currentPlayerNumber = (currentPlayerNumber == 0 ? 1 : 0);
    }

    public Battlefield getCurrentBattlefield() {
        return battlefieldList.get(currentPlayerNumber);
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public Battlefield getEnemyBattlefield() {
        int enemyNum = (currentPlayerNumber == 0 ? 1 : 0);
        return battlefieldList.get(enemyNum);
    }

    public boolean isGameNotFinished() {
        return (battlefieldList.get(0).getShipsSunkNumber() < 5
                && battlefieldList.get(1).getShipsSunkNumber() < 5);
    }
}

public class Main {

    /**
     * The game
     * @param args - arguments
     */
    public static void main(String[] args) {
        // Write your code here

        Battlefield yours = new Battlefield("Player 1");
        Battlefield enemy = new Battlefield("Player 2");

        Game game = new Game(yours, enemy);

        List<Battlefield.ShipType> shipList
                = new ArrayList<>(Arrays.asList(Battlefield.ShipType.AIRCRAFT_CARRIER
                                                                            , Battlefield.ShipType.BATTLESHIP
                                                                            , Battlefield.ShipType.SUBMARINE
                                                                            , Battlefield.ShipType.CRUISER
                                                                            , Battlefield.ShipType.DESTROYER));

        Scanner sc = new Scanner(System.in);

        //place the ships for both players
        int i = 0;

        while (i < game.getPlayerCount()) {

            Battlefield current = game.getCurrentBattlefield();

            System.out.printf("%s, place your ships to the game field", current.getOwner());
            current.printField(false);

            //place the ships
            for (Battlefield.ShipType ship : shipList) {
                System.out.printf("Enter the coordinates of the %s: ", ship.getFullName());

                boolean isSet = false;
                while (!isSet) {
                    String coord = sc.nextLine();
                    isSet = current.placeShip(coord, ship);
                }
                current.printField(false);
            }

            //pass the move to the other player
            System.out.println("Press Enter and pass the move to another player\n...");
            sc.nextLine();
            i++;
            game.switchPlayer();
        }

        //war!
        while (game.isGameNotFinished()) {
            Battlefield current = game.getCurrentBattlefield();
            Battlefield foe = game.getEnemyBattlefield();
            foe.printField(true);
            System.out.println("---------------------");
            current.printField(false);
            System.out.printf("%s, it's your turn:\n", current.getOwner());

            try {
                String shot = sc.nextLine();

                boolean wasShot = foe.wasShotAlready(shot);
                boolean isHit = foe.takeAShot(shot);

                if (isHit) {
                    if (foe.isShipSunk(shot) && !wasShot) {
                        foe.increaseShipSunkNumber();
                        System.out.println(foe.getShipsSunkNumber() != 5
                                ? "You sank a ship!" + foe.getShipsSunkNumber()
                                : "You hit a ship!");
                    } else {
                        System.out.println("You hit a ship!");
                    }

                } else {
                    System.out.println("You missed!");
                }

                //pass the move to the other player
                if (game.isGameNotFinished()) {
                    System.out.println("Press Enter and pass the move to another player\n...");
                    sc.nextLine();

                    game.switchPlayer();
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage() + " Try again: ");
            }


        }

        //congratulations!
        System.out.println("You sank the last ship. You won. Congratulations!");

    }
}
