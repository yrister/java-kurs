package com.madnes;

import java.sql.*;

public class DatabaseManager {
    private static String myDriver = "com.mysql.jdbc.Driver";

    private static Connection getConn() throws ClassNotFoundException, SQLException {
        Class.forName(myDriver);
        return DriverManager.getConnection(StaticResource.databaseUrl, StaticResource.databaseUser, StaticResource.databasePassword);
    }

    public static int getMaxGameID() {
        try
        {
            Connection conn = getConn();

            String query = "SELECT MAX(game_id) AS max_game_id FROM agents";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            int maxID = 0;
            while (rs.next())
            {
                if (rs.getString("max_game_id") == null) {
                    maxID = 0;
                } else {
                    maxID = rs.getInt("max_game_id");
                }
            }
            st.close();
            return maxID;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static void addAgent(Agent agent) {
        try
        {
            Connection conn = getConn();

            String query = "insert into agents (game_id, agent_game_id, group_id, x, y, energy)"
                    + " values (?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, GameRuntime.getInstance().gameID);
            preparedStmt.setInt(2, agent.agentID);
            preparedStmt.setInt(3, agent.groupID);
            preparedStmt.setDouble(4, agent.position.x);
            preparedStmt.setDouble(5, agent.position.y);
            preparedStmt.setDouble(6, agent.getEnergy());

            preparedStmt.execute();
            conn.close();

            GameRuntime.getInstance().logger.info("\nAgent added to database" +
                    "\nagent id: " + agent.agentID + "\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
