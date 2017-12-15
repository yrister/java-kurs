package com.madnes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(urlPatterns = "/game")
public class GameServlet extends HttpServlet {
    /*
    * api для получения обьектов на карте
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String json = "{\"agents\": [";

        GameRuntime.getInstance().sourcesPath = getServletContext().getRealPath("/WEB-INF/");

        ArrayList<String> agentsStr = new ArrayList();
        for (Agent agent: GameRuntime.getInstance().getField().getAgents()) {
//            if (agent.isVisable) {
                agentsStr.add("{\"x\": " + agent.position.x
                        + ", \"y\": " + agent.position.y
                        + ", \"groupID\": " + agent.groupID
                        + ", \"energy\": " + agent.getEnergy()
                        + ", \"damage\": " + agent.damage
                        + ", \"isVisable\": " + agent.isVisable
                        + ", \"direction\": " + (agent.getDirection() + Math.PI / 2) + "}");
//            }
        }
        json += String.join(",", agentsStr);
        json += "],\"artifacts\": [";

        ArrayList<String> artifactsStr = new ArrayList();
        for (Artifact artifact: GameRuntime.getInstance().getField().getArtifacts()) {
            artifactsStr.add("{\"x\": " + artifact.position.x +
                    ", \"y\": " + artifact.position.y +
                    ", \"size\": " + artifact.getEnergy() * StaticResource.sizePerEnergy
                    + ", \"energy\": " + artifact.getEnergy() + "" +
                    ", \"type\": " + artifact.type + "}");
        }
        json += String.join(",", artifactsStr);
        json += "], \"isPlaying\": " + GameRuntime.getInstance().isPlaying() +"}";

        out.print(json);
        out.close();
    }
}
