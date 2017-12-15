package com.madnes;

import java.util.ArrayList;

public class Field {

    private ArrayList<Agent> agents = new ArrayList<Agent>();
    private ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

    /*
    * удаляет артифакт с карты
    */
    public void removeArtifact(Artifact artifact) {
        artifacts.remove(artifact);
    }

    /*
    * удаляет агентов с закончившейся энергией
    */
    public void removeWeekAgents() {
        int aliveCount = 0;
        Agent lastAliveAgent = null;
        for (Agent agent: agents) {
            if (agent.isAlive && agent.getEnergy() < StaticResource.minAgentEnergy) {
                agent.isAlive = false;
                agent.isVisable = false;
                GameRuntime.getInstance().logger.info("\nAgent dead" +
                        "\nagent id: " + agent.agentID + "\n");
            } else if (agent.isAlive) {
                aliveCount++;
                lastAliveAgent = agent;
            }
        }
        if (aliveCount <= 1) {
            lastAliveAgent.isAlive = false;
            if (DatabaseManager.getMaxGameID() != GameRuntime.getInstance().gameID) {
                DatabaseManager.addAgent(lastAliveAgent);
            }
            GameRuntime.getInstance().stop();

            GameRuntime.getInstance().logger.info("\nGame ended" +
                    "\nlast agent id: " + lastAliveAgent.agentID + "\n");
        }
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public ArrayList<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;
    }

    public void setArtifacts(ArrayList<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

}
