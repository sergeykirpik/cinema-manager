package cinema;

import java.util.Locale;
import java.util.Scanner;
import java.util.Arrays;

enum Action {
    SHOW_SEATS("1"),
    BUY_TICKET("2"),
    SHOW_STATISTICS("3"),
    EXIT("0");

    private final String input;

    Action(String input) {
        this.input = input;
    }

    static Action fromUserInput(String input) {
        Action action = null;
        for (Action a: Action.values()) {
            if (a.input.equals(input)) {
                action = a;
            }
        }
        return action;
    }
}

enum BuyTicketResult {
    RESULT_OK,
    ERR_OUT_OF_BOUNDS,
    ERR_SOLD_OUT
}

public class Cinema {
    static int SMALL_ROOM_TICKET_PRICE = 10;
    static int FRONT_TICKET_PRICE = 10;
    static int BACK_TICKET_PRICE = 8;

    static char FREE_SEAT = 'S';
    static char SOLD_OUT_SEAT = 'B';
    static Scanner scanner = new Scanner(System.in);
    int rowsInRoom;
    int seatsInRow;
    char[] seats;

    Cinema(int rowsInRoom, int seatsInRow) {
        this.rowsInRoom = rowsInRoom;
        this.seatsInRow = seatsInRow;
        seats = new char[rowsInRoom * seatsInRow];
        Arrays.fill(seats, FREE_SEAT);
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        System.out.println("Enter the number of rows:");
        int rowsInRoom = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the number of seats in each row:");
        int seatsInRow = Integer.parseInt(scanner.nextLine());

        Cinema cinema = new Cinema(rowsInRoom, seatsInRow);
        boolean finished = false;
        while (!finished) {
            switch (selectAction()) {
                case SHOW_SEATS:
                    cinema.showSeats();
                    break;
                case BUY_TICKET:
                    buyTicket(cinema);
                    break;
                case SHOW_STATISTICS:
                    showStatistics(cinema);
                    break;
                case EXIT:
                    finished = true;
                    break;
                default:
                    break;
            }
        }
    }

    static Action selectAction() {
        System.out.println();
        System.out.println("1. Show the seats");
        System.out.println("2. Buy a ticket");
        System.out.println("3. Statistics");
        System.out.println("0. Exit");

        return Action.fromUserInput(scanner.nextLine());
    }

    static void showStatistics(Cinema cinema) {
        int purchased = cinema.calculatePurchasedTickets();
        double percentage = (double) purchased / cinema.getTotalSeats() * 100;
        int currentIncome = cinema.calculateCurrentIncome();
        int totalIncome = cinema.calculateTotalIncome();

        System.out.println();
        System.out.printf("Number of purchased tickets: %d\n", purchased);
        System.out.printf("Percentage: %.2f%%\n", percentage);
        System.out.printf("Current income: $%d\n", currentIncome);
        System.out.printf("Total income: $%d\n", totalIncome);
    }

    static void buyTicket(Cinema cinema) {
        boolean success = false;
        int rowNumber = 0;
        int seatNumber;
        while (!success) {
            System.out.println("\nEnter a row number:");
            String[] rowNumberRawInput = scanner.nextLine().split("\\s+");

            System.out.println("Enter a seat number in that row:");
            String[] seatNumberRawInput = scanner.nextLine().split("\\s+");

            boolean valid =
                rowNumberRawInput.length == 1 &&
                rowNumberRawInput[0].matches("\\d+") &&
                seatNumberRawInput.length == 1 &&
                seatNumberRawInput[0].matches("\\d+");

            if (!valid) {
                System.out.println("\nWrong input!");
                continue;
            }
            rowNumber = Integer.parseInt(rowNumberRawInput[0]);
            seatNumber = Integer.parseInt(seatNumberRawInput[0]);

            switch (cinema.buyTicket(rowNumber, seatNumber)) {
                case RESULT_OK:
                    success = true;
                    break;
                case ERR_SOLD_OUT:
                    System.out.println("\nThat ticket has already been purchased!");
                    break;
                case ERR_OUT_OF_BOUNDS:
                    System.out.println("\nWrong input!");
                    break;
                default:
                    break;
            }
        }
        int ticketPrice = cinema.calculateTicketPrice(rowNumber);
        System.out.printf("\nTicket price: $%d%n", ticketPrice);
    }

    int getSeatIndex(int rowNumber, int seatNumber) {
        return (rowNumber - 1) * seatsInRow + seatNumber - 1;
    }

    char seatAt(int rowNumber, int seatNumber) {
        return seats[getSeatIndex(rowNumber, seatNumber)];
    }

    private void changeSeat(int rowNumber, int seatNumber, char mark) {
        seats[getSeatIndex(rowNumber, seatNumber)] = mark;
    }

    BuyTicketResult buyTicket(int rowNumber, int seatNumber) {
        boolean outOfBounds = rowNumber < 1 || rowNumber > rowsInRoom || seatNumber < 1 || seatNumber > seatsInRow;
        if (outOfBounds) {
            return BuyTicketResult.ERR_OUT_OF_BOUNDS;
        }

        boolean soldOut = seatAt(rowNumber, seatNumber) == SOLD_OUT_SEAT;
        if (soldOut) {
            return BuyTicketResult.ERR_SOLD_OUT;
        }

        changeSeat(rowNumber, seatNumber, SOLD_OUT_SEAT);
        return BuyTicketResult.RESULT_OK;
    }

    int getTotalSeats() {
        return rowsInRoom * seatsInRow;
    }

    int calculatePurchasedTickets() {
        int purchased = 0;
        for (char seat: seats) {
            if (seat == SOLD_OUT_SEAT) {
                purchased++;
            }
        }
        return purchased;
    }

    int calculateCurrentIncome() {
        int currentIncome = 0;
        for (int rowNumber = 1; rowNumber <= rowsInRoom; rowNumber++) {
            for (int seatNumber = 1; seatNumber <= seatsInRow; seatNumber++) {
                if (seatAt(rowNumber, seatNumber) == SOLD_OUT_SEAT) {
                    currentIncome += calculateTicketPrice(rowNumber);
                }
            }
        }
        return currentIncome;
    }

    void showSeats() {
        System.out.println();
        System.out.println("Cinema:");
        System.out.print(" ");
        for (int i = 1; i <= seatsInRow; i++) {
            System.out.printf(" %d", i);
        }
        System.out.println();
        for (int rowNumber = 1; rowNumber <= rowsInRoom; rowNumber++) {
            System.out.printf("%d", rowNumber);
            for (int seatNumber = 1; seatNumber <= seatsInRow; seatNumber++) {
                System.out.printf(" %c", seatAt(rowNumber, seatNumber));
            }
            System.out.println();
        }
    }

    int calculateTicketPrice(int rowNumber) {
        int totalSeats = rowsInRoom * seatsInRow;
        if (totalSeats <= 60) {
            return SMALL_ROOM_TICKET_PRICE;
        }
        if (rowNumber <= rowsInRoom / 2) {
            return FRONT_TICKET_PRICE;
        }
        return BACK_TICKET_PRICE;
    }

    int calculateTotalIncome() {
        int totalIncome = 0;
        for (int row = 1; row <= rowsInRoom; row++) {
            for (int seat = 1; seat <= seatsInRow; seat++) {
                totalIncome += calculateTicketPrice(row);
            }
        }
        return totalIncome;
    }

}
