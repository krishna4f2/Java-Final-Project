import java.util.Scanner;

public class MovieTicketBookingsystem {

    // ---------- Theatre setup ----------
    static final int ROWS = 5;          // rows A to E
    static final int COLS = 6;          // seats 1 to 6 in each row

    // 2D array for seats: false = available, true = booked
    static boolean[][] seats = new boolean[ROWS][COLS];

    // 2D array to remember how much was paid for each seat (used for refund)
    static double[][] paid = new double[ROWS][COLS];

    // ---------- Pricing ----------
    static final double WEEKDAY_PRICE = 150;
    static final double WEEKEND_PRICE = 200;
    static int day;                     // 1 = Monday ... 7 = Sunday

    // ---------- Movie details and rating ----------
    static String movieName = "Jailer 2";
    static int ratingTotal = 45;        // sum of all ratings given so far
    static int ratingCount = 10;        // how many people rated

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        // A few seats are already booked, so the seat chart looks realistic
        seats[0][1] = true;  paid[0][1] = WEEKDAY_PRICE;   // A2
        seats[2][3] = true;  paid[2][3] = WEEKDAY_PRICE;   // C4

        System.out.println("===== WELCOME TO CINEMA BOOKING =====");

        // Ask the show day until a valid number (1 to 7) is entered
        do {
            System.out.print("Enter show day (1=Mon, 2=Tue, ... 6=Sat, 7=Sun): ");
            day = readInt();
            if (day < 1 || day > 7) {
                System.out.println("Invalid day. Please enter 1 to 7.");
            }
        } while (day < 1 || day > 7);

        int choice;
        do {
            System.out.println("\n===== " + movieName + " | Ticket price: Rs." + getPrice() + " =====");
            System.out.println("1. Show Available Seats");
            System.out.println("2. Book Ticket");
            System.out.println("3. Cancel Ticket");
            System.out.println("4. Rate Movie");
            System.out.println("5. View Movie Rating");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1: showSeats(); break;
                case 2: bookTicket(); break;
                case 3: cancelTicket(); break;
                case 4: rateMovie(); break;
                case 5: viewRating(); break;
                case 6: System.out.println("Thank you! Enjoy the movie."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);
    }

    // ---------- Module: Weekend Pricing ----------
    static double getPrice() {
        if (day == 6 || day == 7) {      // Saturday or Sunday
            return WEEKEND_PRICE;
        } else {
            return WEEKDAY_PRICE;
        }
    }

    // ---------- Module: Show Available Seats ----------
    static void showSeats() {
        System.out.println("\n          SCREEN THIS WAY");
        System.out.println();
        System.out.println("   -------- Jailer 2 ---------");
        System.out.println();

        // Column numbers
        System.out.print("   ");
        for (int c = 0; c < COLS; c++) {
            System.out.print(" " + (c + 1) + "  ");
        }
        System.out.println();

        // Each row: [ ] = available, [X] = booked
        int available = 0;
        for (int r = 0; r < ROWS; r++) {
            System.out.print((char) ('A' + r) + "  ");
            for (int c = 0; c < COLS; c++) {
                if (seats[r][c]) {
                    System.out.print("[X] ");
                } else {
                    System.out.print("[ ] ");
                    available++;
                }
            }
            System.out.println();
        }
        System.out.println("\n[ ] Available   [X] Booked");
        System.out.println("Seats available: " + available + " / " + (ROWS * COLS));
    }

    // ---------- Module: Book Ticket (with Discount Coupon) ----------
    static void bookTicket() {
        showSeats();

        System.out.print("\nEnter row (A-E): ");
        String rowInput = sc.nextLine().trim().toUpperCase();
        if (rowInput.length() != 1) {
            System.out.println("Invalid row.");
            return;
        }
        int r = rowInput.charAt(0) - 'A';      // 'A' -> 0, 'B' -> 1 ...
        if (r < 0 || r >= ROWS) {
            System.out.println("Invalid row. Choose A to E.");
            return;
        }

        System.out.print("Enter seat number (1-" + COLS + "): ");
        int c = readInt() - 1;                 // seat 1 -> index 0
        if (c < 0 || c >= COLS) {
            System.out.println("Invalid seat number.");
            return;
        }

        if (seats[r][c]) {
            System.out.println("Sorry, seat " + rowInput + (c + 1) + " is already booked.");
            return;
        }

        double price = getPrice();
        System.out.print("Enter coupon code (or press Enter to skip): ");
        String code = sc.nextLine().trim().toUpperCase();
        double finalPrice = applyCoupon(price, code);

        seats[r][c] = true;
        paid[r][c] = finalPrice;

        System.out.println("\n----- TICKET BOOKED -----");
        System.out.println("Movie  : " + movieName);
        System.out.println("Seat   : " + rowInput + (c + 1));
        System.out.println("Amount : Rs." + finalPrice);
        System.out.println("-------------------------");
    }

    // ---------- Module: Discount Coupon ----------
    static double applyCoupon(double price, String code) {
        if (code.isEmpty()) {
            return price;                                  // no coupon entered
        } else if (code.equals("SAVE10")) {
            System.out.println("Coupon applied: 10% discount!");
            return price * 0.90;
        } else if (code.equals("STUDENT20")) {
            System.out.println("Coupon applied: 20% student discount!");
            return price * 0.80;
        } else {
            System.out.println("Invalid coupon. Full price will be charged.");
            return price;
        }
    }

    // ---------- Module: Cancel Ticket ----------
    static void cancelTicket() {
        System.out.print("Enter row of the booked seat (A-E): ");
        String rowInput = sc.nextLine().trim().toUpperCase();
        if (rowInput.length() != 1) {
            System.out.println("Invalid row.");
            return;
        }
        int r = rowInput.charAt(0) - 'A';
        if (r < 0 || r >= ROWS) {
            System.out.println("Invalid row. Choose A to E.");
            return;
        }

        System.out.print("Enter seat number (1-" + COLS + "): ");
        int c = readInt() - 1;
        if (c < 0 || c >= COLS) {
            System.out.println("Invalid seat number.");
            return;
        }

        if (!seats[r][c]) {
            System.out.println("Seat " + rowInput + (c + 1) + " is not booked, nothing to cancel.");
        } else {
            System.out.println("Ticket cancelled for seat " + rowInput + (c + 1));
            System.out.println("Refund amount: Rs." + paid[r][c]);
            seats[r][c] = false;
            paid[r][c] = 0;
        }
    }

    // ---------- Module: Movie Rating ----------
    static void rateMovie() {
        System.out.print("Rate " + movieName + " (1 to 5): ");
        int rating = readInt();
        if (rating >= 1 && rating <= 5) {
            ratingTotal += rating;
            ratingCount++;
            System.out.println("Thanks for rating!");
        } else {
            System.out.println("Invalid rating. Enter a number from 1 to 5.");
        }
    }

    static void viewRating() {
        double average = (double) ratingTotal / ratingCount;
        System.out.printf("%s : %.1f / 5 (%d ratings)%n", movieName, average, ratingCount);
    }

    // ---------- Helper: safe number input ----------
    static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;      // treated as invalid input
        }
    }
}