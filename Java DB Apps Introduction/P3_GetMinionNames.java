import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;

public class P3_GetMinionNames {
    private static String query;
    private static PreparedStatement statement;
    private static ResultSet resultSet;
    private static Connection connection;
    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );
        int idInput = Integer.parseInt(reader.readLine());
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "1234");

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", props);

        if (checkIfIdIsValid(idInput)) {
            System.out.println(getVillainName(idInput));
            getMinionsAndAge(idInput);
        } else {
            System.out.println(String.format("No villain with ID %d exists in the database.", idInput));
        }
    }

    private static void getMinionsAndAge(int idInput) throws SQLException {
        query = "SELECT m.name, m.age FROM minions AS m\n" +
                "JOIN minions_villains mv ON m.id = mv.minion_id\n" +
                "JOIN villains v ON mv.villain_id = v.id\n" +
                "WHERE v.id = ?;";
        statement = connection.prepareStatement(query);
        statement.setInt(1, idInput);
        resultSet = statement.executeQuery();
        int minionCounter = 0;
        while (resultSet.next()) {
            System.out.println(String.format("%d. %s %d", ++minionCounter, resultSet.getString("name"), resultSet.getInt("age")));
        }
    }

    private static String getVillainName(int idInput) throws SQLException {
        query = "SELECT v.name FROM villains AS v\n" +
                "where v.id = ?";
        statement = connection.prepareStatement(query);
        statement.setInt(1, idInput);
        resultSet = statement.executeQuery();
        return "Villain: " + (resultSet.next() ? resultSet.getString(1) : null);
    }

    private static boolean checkIfIdIsValid(int idInput) throws SQLException {
        query = "SELECT * FROM villains\n" +
                "WHERE id = ?";
        statement = connection.prepareStatement(query);
        statement.setInt(1, idInput);
        resultSet = statement.executeQuery();
        return resultSet.next();
    }
}
