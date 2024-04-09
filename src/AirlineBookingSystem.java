import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AirlineBookingSystem {
    private static final String URL = "csci-cs418-18.dhcp.bsu.edu";
    private static final String USER = "Studentdba";
    private static final String PASSWORD = "u71BH*}U";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            createTable(connection);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Book");
                System.out.println("2. View Reservation");
                System.out.println("3. Cancel Reservation");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        bookFlight(connection);
                        break;
                    case "2":
                        viewReservation(connection);
                        break;
                    case "3":
                        cancelReservation(connection);
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS booking (id INT AUTO_INCREMENT PRIMARY KEY, "
                + "passenger_name VARCHAR(255), flight_number VARCHAR(10), departure_date DATE, seat_number INT)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    private static void bookFlight(Connection connection) throws SQLException {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter passenger name: ");
            String passengerName = scanner.nextLine();
            System.out.print("Enter flight number: ");
            String flightNumber = scanner.nextLine();
            System.out.print("Enter departure date (YYYY-MM-DD): ");
            String departureDate = scanner.nextLine();
            System.out.print("Enter seat number: ");
            int seatNumber = scanner.nextInt();

            String sql = "INSERT INTO booking (passenger_name, flight_number, departure_date, seat_number) "
                    + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, passengerName);
                statement.setString(2, flightNumber);
                statement.setString(3, departureDate);
                statement.setInt(4, seatNumber);
                statement.executeUpdate();
                System.out.println("Booking successful.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewReservation(Connection connection) throws SQLException {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter passenger name: ");
            String passengerName = scanner.nextLine();

            String sql = "SELECT * FROM booking WHERE passenger_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, passengerName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Your bookings:");
                        do {
                            System.out.println("ID: " + resultSet.getInt("id")
                                    + ", Flight Number: " + resultSet.getString("flight_number")
                                    + ", Departure Date: " + resultSet.getString("departure_date")
                                    + ", Seat Number: " + resultSet.getInt("seat_number"));
                        } while (resultSet.next());
                    } else {
                        System.out.println("No bookings found for " + passengerName);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cancelReservation(Connection connection) throws SQLException {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter passenger name: ");
            String passengerName = scanner.nextLine();
            System.out.print("Enter booking ID to cancel: ");
            int bookingId = scanner.nextInt();

            String sql = "DELETE FROM booking WHERE id = ? AND passenger_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bookingId);
                statement.setString(2, passengerName);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Booking canceled successfully.");
                } else {
                    System.out.println("No booking found with ID " + bookingId + " for " + passengerName);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
