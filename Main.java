package battleship;


import java.util.*;

public class Main {

    /**
     * The field with ships
     */
    private static final boolean[][] field = new boolean[10][10];

    /**
     * Hits and misses
     */
    private static final boolean[][] shots = new boolean[10][10];

    private static char[][] fullField = new char[10][10];

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
     * Check whether coordinates are valid
     * @param noseCoord - nose coordinates
     * @param tailCoord - tail coordinates
     * @param st - ship type
     * @return - flag
     */
    public static boolean areCoordinatesValid(String[] noseCoord, String[] tailCoord, ShipType st) {
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

    public static boolean isPlacementValid(String[] nose, String[] tail, ShipType st) {

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
    public static void printField(boolean fogOfWar) {
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


    public static boolean checkShip(int row, int column) {


        return false;
    }

    /**
     * Place a ship
     * @param coordinates - nose and tail coordinates
     * @param st - ship type
     */
    public static boolean placeShip(String coordinates, ShipType st) {

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
    public static boolean isGreater(String[] first, String[] second) {
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
    public static boolean takeAShot(String shot) throws Exception {

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
     * @return
     */
    private static boolean isShipSunk(String shot) {

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
     * @return
     */
    private static boolean wasShotAlready(String shot) {
        String[] coord = new String[]{shot.substring(0, 1), shot.substring(1)};
        int row = coord[0].charAt(0) - (int) 'A';
        int column = Integer.parseInt(coord[1]) - 1;

        return shots[row][column];
    }

    /**
     * The game
     * @param args - arguments
     */
    public static void main(String[] args) {
        // Write your code here
        printField(false);

        Scanner sc = new Scanner(System.in);

        List<ShipType> shipList = new ArrayList<>(Arrays.asList(ShipType.AIRCRAFT_CARRIER, ShipType.BATTLESHIP
                                                                , ShipType.SUBMARINE, ShipType.CRUISER
                                                                , ShipType.DESTROYER));

        //place the ships
        for (ShipType ship: shipList) {
            System.out.printf("Enter the coordinates of the %s: ", ship.getFullName());

            boolean isSet = false;
            while (!isSet) {
                String coord = sc.nextLine();
                isSet = placeShip(coord, ship);
            }
            printField(false);
        }

        //war!
        System.out.println("The game starts!");
        printField(true);
        System.out.println("Take a shot!");

        int shipSunkNumber = 0;

        while (shipSunkNumber < 5) {
            try {
                String shot = sc.nextLine();

                boolean wasShot = wasShotAlready(shot);
                boolean isHit = takeAShot(shot);

                printField(true);

                if (isHit) {
                    if (isShipSunk(shot) && !wasShot) {
                        shipSunkNumber++;
                        System.out.println(shipSunkNumber != 5 ? "You sank a ship! Specify a new target:" : "");
                    } else {
                        System.out.println("You hit a ship! Try again:");
                    }

                } else {
                    System.out.println("You missed! Try again:");
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage() + " Try again: ");
            }
        }

        if (shipSunkNumber == 5) {
            System.out.println("You sank the last ship. You won. Congratulations!");
        }


    }
}
