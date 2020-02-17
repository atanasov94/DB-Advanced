import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;

public class P8_IncreaseMinionsAge {
    private static String query;
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "1234");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", properties);

        int[] array = Arrays.stream(reader.readLine().split("\\s+")).mapToInt(Integer::parseInt).toArray();
        query = "UPDATE minions\n" +
                "SET age = age + 1, name = LOWER(name)\n" +
                "WHERE id = ?;";
        preparedStatement = connection.prepareStatement(query);

        for (int i = 0; i < array.length; i++) {
            preparedStatement.setInt(1, array[i]);
            preparedStatement.execute();
        }

        query = "SELECT name, age FROM minions;";
        preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString("name") + " " + resultSet.getInt("age"));
        }
    }
}
