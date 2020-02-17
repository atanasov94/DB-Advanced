import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class P5_ChangeTownNamesCasing {
    private static String query;
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;
    private static final String driver =  "jdbc:mysql://localhost:3306/";
    private static final String databaseName = "minions_db";
    public static void main(String[] args) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "1234");
        connection = DriverManager.getConnection(driver + databaseName, properties);

        String townNameInput = reader.readLine();
        changeTownNames(townNameInput);
        printChangedTowns(townNameInput);

    }

    private static void printChangedTowns(String townNameInput) throws SQLException {
        query = "SELECT name FROM towns WHERE country = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, townNameInput);
        resultSet = preparedStatement.executeQuery();
        List<String> list = new ArrayList<>();
        int counter = 0;
        while (resultSet.next()) {
            list.add(resultSet.getString(1));
        }
        System.out.println(!list.isEmpty() ? list : "No town names were affected.");
    }

    private static void changeTownNames(String townNameInput) throws SQLException {
        query = "UPDATE towns\n" +
                "SET name = UPPER(name)\n" +
                "WHERE country = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, townNameInput);
        int numberUpdated = preparedStatement.executeUpdate();
        if (numberUpdated > 0) {
            System.out.println(String.format("%d town names were affected.", numberUpdated));
        }
    }
}
