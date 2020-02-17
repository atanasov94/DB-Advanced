import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class P7_PrintAllMinionNames {
    private static String query;
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws SQLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "1234");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", properties);

        query = "SELECT name FROM minions;";
        preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        List<String> list = new ArrayList<>();

        while (resultSet.next()) {
            list.add(resultSet.getString(1));
        }

        for (int i = 0; i < list.size() / 2; i++) {
            System.out.println(list.get(i));
            System.out.println(list.get(list.size() - (i + 1)));
        }

    }
}
