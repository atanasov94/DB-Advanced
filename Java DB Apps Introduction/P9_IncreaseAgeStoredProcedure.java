import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class P9_IncreaseAgeStoredProcedure {
    public static void main(String[] args) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "1234");
        Connection connection =
                DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", properties);

//        The following query was executed:

//        CREATE PROCEDURE usp_get_older(minion_id int)
//        BEGIN
//        UPDATE minions
//        SET age = age + 1
//        WHERE id = minion_id;
//        END;

        int inputId = Integer.parseInt(reader.readLine());
        String query = "CALL usp_get_older(?)";
        CallableStatement callableStatement = connection.prepareCall(query);
        callableStatement.setInt(1, inputId);
        callableStatement.execute();

    }
}
