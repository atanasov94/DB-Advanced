import java.sql.*;
import java.util.Properties;

public class P2_GetVillainsNames {
    public static void main(String[] args) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "1234");

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", props);

        String query = "SELECT v.name, COUNT(mv.minion_id) as number_of_minions FROM villains AS v\n" +
                "JOIN minions_villains mv ON v.id = mv.villain_id\n" +
                "GROUP BY v.name\n" +
                "HAVING number_of_minions > 15\n" +
                "ORDER BY number_of_minions DESC";

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            System.out.println(String.format("%s %d", resultSet.getString(1), resultSet.getInt(2)));
        }
    }
}
