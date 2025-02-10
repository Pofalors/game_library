import java.sql.*;
import java.util.Scanner;

public class GameDatabase {

    // Στατικές μεταβλητές για τα στοιχεία σύνδεσης με τη βάση δεδομένων
    private static final String URL = "jdbc:mysql://localhost:3306/myDB";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Μέθοδος σύνδεσης με τη βάση δεδομένων
    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection to MySQL has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Μέθοδος για τη δημιουργία νέων πινάκων
    private static void createNewTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (\n"
                                 + "    id INTEGER PRIMARY KEY,\n"
                                 + "    name TEXT NOT NULL,\n"
                                 + "    age INTEGER\n"
                                 + ");";
        
        String createGamesTable = "CREATE TABLE IF NOT EXISTS games (\n"
                                 + "    user_id INTEGER,\n"
                                 + "    name_of_game TEXT NOT NULL,\n"
                                 + "    release_date DATE NOT NULL,\n"
                                 + "    hours_played INTEGER,\n"
                                 + "    PRIMARY KEY (user_id, name_of_game),\n"
                                 + "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE\n"
                                 + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createGamesTable);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Μέθοδος για την εισαγωγή δεδομένων
    private static void insert(int id, String name, int age) {
        String sql = "INSERT INTO users(id, name, age) VALUES(?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, age);
            pstmt.executeUpdate();
            System.out.println("Users inserted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Μέθοδος για την εισαγωγή παιχνιδιών
    private static void insertGame(int userId, String nameOfGame, String releaseDate, int hoursPlayed) {
        // Έλεγχος αν υπάρχει ήδη εγγραφή με το ίδιο user_id και name_of_game
        String checkSql = "SELECT COUNT(*) FROM games WHERE user_id = ? AND name_of_game = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, nameOfGame);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("The game already exists for this user. No duplicate entry allowed.");
                return;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Εισαγωγή νέας εγγραφής αν δεν υπάρχει διπλότυπο
        String insertSql = "INSERT INTO games(user_id, name_of_game, release_date, hours_played) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, nameOfGame);
            pstmt.setDate(3, Date.valueOf(releaseDate));
            pstmt.setInt(4, hoursPlayed);
            pstmt.executeUpdate();
            System.out.println("Game inserted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Μέθοδος για την ενημέρωση δεδομένων
    private static void update(int id, String name, int age) {
        String sql = "UPDATE users SET name = ?, age = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Data updated successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Μέθοδος για την ενημέρωση παιχνιδιών
    private static void updateGame(int userId, String nameOfGame, String releaseDate, int hoursPlayed) {
        String sql = "UPDATE games SET hours_played = ? WHERE user_id = ? AND name_of_game = ? AND release_date = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hoursPlayed);
            pstmt.setInt(2, userId);
            pstmt.setString(3, nameOfGame);
            pstmt.setDate(4, Date.valueOf(releaseDate));
            pstmt.executeUpdate();
            System.out.println("Game updated successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Μέθοδος για τη διαγραφή χρηστών
    private static void deleteUser(int id) {
        String sqlgame = "DELETE FROM games WHERE user_id = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sqlgame)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            String sql = "DELETE FROM users WHERE id = ?";

            try (Connection conn1 = connect();
                PreparedStatement pstmt1 = conn.prepareStatement(sql)) {
                pstmt1.setInt(1, id);
                pstmt1.executeUpdate();
                System.out.println("User and related games deleted successfully.");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Μέθοδος για τη διαγραφή συγκεκριμένων παιχνιδιών
    private static void deleteGame(int userId, String nameOfGame) {
        String sql = "DELETE FROM games WHERE user_id = ? AND name_of_game = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, nameOfGame);
            pstmt.executeUpdate();
            System.out.println("Game deleted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Μέθοδος για την εκτέλεση δυναμικών SQL ερωτημάτων από τον χρήστη
    public static void executeUserQuery() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your SQL query:");
        String userQuery = scanner.nextLine();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(userQuery)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Εκτύπωση των κεφαλίδων των στηλών
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(String.format("%-20s", rsmd.getColumnName(i)));
            }
            System.out.println();
            System.out.println("-----------------------------------------------------------------");

            // Εκτύπωση των δεδομένων
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(String.format("%-20s", rs.getString(i)));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
    }

    // Μέθοδος για την εμφάνιση όλων των δεδομένων
    private static void selectAll() {
        String sql = "SELECT id, name, age FROM users";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            System.out.println(String.format("%-5s %-20s %-5s", "ID", "Name", "Age"));
            System.out.println("-------------------------------");
            while (rs.next()) {
                System.out.println(String.format("%-5d %-20s %-5d", rs.getInt("id"), rs.getString("name"), rs.getInt("age")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Μέθοδος για την εμφάνιση όλων των παιχνιδιών
    private static void selectAllGames() {
        String sql = "SELECT user_id, name_of_game, release_date, hours_played FROM games";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            System.out.println(String.format("%-10s %-30s %-15s %-12s", "User ID", "Game Name", "Release Date", "Hours Played"));
            System.out.println("----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.println(String.format("%-10d %-30s %-15s %-12d", rs.getInt("user_id"), rs.getString("name_of_game"), rs.getDate("release_date"), rs.getInt("hours_played")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Δημιουργία πινάκων
        createNewTables();

        boolean menuSQL = true;
        while (menuSQL) {
            boolean menuTables = true;
            System.out.println("~~ SQL MENU ~~");
            System.out.println("PRESS :");
            System.out.println("0 -> EDIT USER TABLE");
            System.out.println("1 -> EDIT GAMES TABLE");
            System.out.println("2 -> ENTER SQL QUERY");
            System.out.println("3 -> EXIT");
            System.out.print("GIVE CHOICE : ");
            int choice = sc.nextInt();
            while (choice != 0 && choice != 1 && choice != 2 && choice != 3) {
                System.out.println();
                System.out.println("Give proper value for menu choice");
                System.out.println("~~ SQL MENU ~~");
                System.out.println("PRESS :");
                System.out.println("0 -> EDIT USER TABLE");
                System.out.println("1 -> EDIT GAMES TABLE");
                System.out.println("2 -> ENTER SQL QUERY");
                System.out.println("3 -> EXIT");
                System.out.print("GIVE CHOICE : ");
                choice = sc.nextInt();
            }
            if (choice == 0) {
                System.out.println("~~ User Table MENU ~~");
                System.out.println("PRESS :");
                System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                System.out.print("GIVE CHOICE : ");
                while (menuTables) {
                    choice = sc.nextInt();
                    while (choice != 0 && choice != 1 && choice != 2 && choice != 3 && choice != 4) {
                        System.out.println();
                        System.out.println("Give proper value for choice");
                        System.out.println("~~ User Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                        choice = sc.nextInt();
                    }
                    if (choice == 0) {
                        // Εισαγωγή δεδομένων
                        insert(1, "John Doe", 25);
                        insert(2, "Jane Smith", 30);

                        System.out.println("~~ User Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                    } else if (choice == 1) {
                        // Διαγραφή δεδομένων
                        deleteUser(2);
                        deleteUser(1);

                        System.out.println("~~ User Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                    } else if (choice == 2) {
                        // Ενημέρωση δεδομένων
                        update(1, "John Doe", 26);

                        System.out.println("~~ User Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                    } else if (choice == 3){
                        // Εμφάνιση όλων των δεδομένων
                        selectAll();
                        System.out.println();

                        System.out.println("~~ User Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                    } else if (choice == 4) {
                        menuTables = false;
                    }
                }
            } else if (choice == 1) {
                System.out.println("~~ Games Table MENU ~~");
                System.out.println("PRESS :");
                System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                System.out.print("GIVE CHOICE : ");
                while (menuTables) {
                    choice = sc.nextInt();
                    while (choice != 0 && choice != 1 && choice != 2 && choice != 3 && choice != 4) {
                        System.out.println();
                        System.out.println("Give proper value for choice");
                        System.out.println("~~ Games Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                        choice = sc.nextInt();
                    }
                    if (choice == 0) {
                        // Εισαγωγή δεδομένων στον πίνακα games
                        insertGame(1, "The Legend of Zelda", "1986-02-21", 100);
                        insertGame(1, "Super Mario Bros.", "1985-09-13", 150);
                        insertGame(2, "Minecraft", "2011-11-18", 200);

                        System.out.println("~~ Games Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                    } else if (choice == 1) {
                        // Διαγραφή δεδομένων
                        deleteGame(1, "Super Mario Bros.");

                        System.out.println("~~ Games Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                    } else if (choice == 2) {
                        // Ενημέρωση δεδομένων
                        updateGame(1, "The Legend of Zelda", "1986-02-21", 120);

                        System.out.println("~~ Games Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                    } else if (choice == 3){
                        // Εμφάνιση όλων των δεδομένων
                        selectAllGames();
                        System.out.println();

                        System.out.println("~~ Games Table MENU ~~");
                        System.out.println("PRESS :");
                        System.out.println("0 -> ΕΙΣΑΓΩΓΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("1 -> ΔΙΑΓΡΑΦΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("2 -> ΕΝΗΜΕΡΩΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("3 -> ΕΜΦΑΝΙΣΗ ΔΕΔΟΜΕΝΩΝ");
                        System.out.println("4 -> ΤΕΡΜΑΤΙΣΜΟΣ");
                        System.out.print("GIVE CHOICE : ");
                    } else if (choice == 4) {
                        menuTables = false;
                    }
                }
            } else if (choice == 2) {
                // Δημιουργία δυναμικού ερωτήματος SQL
                executeUserQuery();
                
            } else if (choice == 3) {
                menuSQL = false;
            }
        }
        sc.close();
    }   
}