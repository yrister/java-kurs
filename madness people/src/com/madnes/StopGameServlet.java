package com.madnes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet(urlPatterns = "/stop")
public class StopGameServlet extends HttpServlet {

    /*
    * останавливает игру
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GameRuntime.getInstance().stop();
        if (DatabaseManager.getMaxGameID() != GameRuntime.getInstance().gameID) {
            for (Agent agent: GameRuntime.getInstance().getField().getAgents()) {
                if (agent.isVisable) {
                    DatabaseManager.addAgent(agent);
                }
            }
        }

        GameRuntime.getInstance().logger.info("\nUser stops the game\n");
    }
}

