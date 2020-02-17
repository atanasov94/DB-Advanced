import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;

public class P4_AddMinion {
    private static Connection connection;
    private static String query;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;
    public static void main(String[] args) throws SQLException, IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "1234");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", properties);

        String minionInput = reader.readLine();
        String villainInput = reader.readLine();
        String town = minionInput.split("\\s+")[3];

        if (!checkIfTownExistsInDatabase(town)) {
            addTownToDatabase(town);
        }

        if (!checkIfVillainExistsInDatabase(villainInput)) {
            addVillainToDatabase(villainInput);
        }

        addMinionToDatabase(minionInput);
        addMinionVillainRelation(minionInput, villainInput);

    }

    private static void addMinionVillainRelation(String minionInput, String villainInput) throws SQLException {
        String villainName = villainInput.split("\\s+")[1];
        String minionName = minionInput.split("\\s+")[1];
        int minionId = getMinionId(minionName);
        int villainId = getVillainId(villainName);
        query = "INSERT INTO minions_villains (minion_id, villain_id)\n" +
                "VALUES (?, ?)";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, minionId);
        preparedStatement.setInt(2, villainId);
        preparedStatement.execute();
        System.out.println(String.format("Successfully added %s to be minion of %s.", minionName, villainName));
    }

    private static int getVillainId(String villainName) throws SQLException {
        query = "SELECT id FROM villains WHERE name = ?;";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, villainName);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next() ? resultSet.getInt(1) : null;
    }

    private static int getMinionId(String minionName) throws SQLException {
        query = "SELECT id FROM minions WHERE name = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, minionName);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next() ? resultSet.getInt(1) : null;
    }

    private static void addMinionToDatabase(String minionInput) throws SQLException {
        String[] tokens = minionInput.split("\\s+");
        String name = tokens[1];
        int age = Integer.parseInt(tokens[2]);
        String townName = tokens[3];
        int townId = getTownId(townName);
        query = "INSERT INTO minions (name, age, town_id)\n" +
                "VALUES (?, ?, ?)";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setInt(2, age);
        preparedStatement.setInt(3, townId);
        preparedStatement.execute();
    }

    private static int getTownId(String townName) throws SQLException {
        query = "SELECT id FROM towns WHERE name = ?;";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, townName);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next() ? resultSet.getInt(1) : null;

    }

    private static void addVillainToDatabase(String villainInput) throws SQLException {
        String villainName = villainInput.split("\\s+")[1];
        query = "INSERT INTO villains (name, evilness_factor) \n" +
                "VALUES (?, 'evil')";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, villainName);
        preparedStatement.execute();
        System.out.println(String.format("Villain %s was added to the database.", villainName));
    }

    private static boolean checkIfVillainExistsInDatabase(String villainInput) throws SQLException {
        String villainName = villainInput.split("\\s+")[1];
        query = "SELECT * FROM villains WHERE name = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, villainName);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    private static void addTownToDatabase(String town) throws SQLException {
        query = "INSERT INTO towns (name)\n" +
                "VALUES (?);";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, town);
        preparedStatement.execute();
        System.out.println(String.format("Town %s was added to the database.", town));
    }

    private static boolean checkIfTownExistsInDatabase(String town) throws SQLException {
        query = "SELECT * FROM towns WHERE name = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, town);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }
}
