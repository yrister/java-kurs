package com.madnes;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GameController implements Runnable {


    /*
    * класс пары агентов, которые учавствовали во взаимодействии
    * @param agent агент
    * @param object обьект на карте
    */
    private class AgentPair {
        public int first = 0;
        public int second = 0;

        public AgentPair(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    private long lastAppearanceArtifactTime = 0; // последнее появление артифакта на карте
    private ArrayList<AgentPair> interactionObjects = new ArrayList<>(); // уже взаимодействовавшие обьекты (удаляются при прикращениии взаимодействия)
    private ArrayList<AgentPair> oldInteractionObjects = new ArrayList<>();

    /*
    * мониторинг обьектов на карте, их взаимодействий и проверка условий остановки игры во втором потоке
    */
    @Override
    public void run() {
        while (GameRuntime.getInstance().isPlaying()) {
            ArrayList<GameObject> objects = new ArrayList<>();

            objects.addAll(GameRuntime.getInstance().getField().getAgents());
            objects.addAll(GameRuntime.getInstance().getField().getArtifacts());

            oldInteractionObjects = interactionObjects;
            interactionObjects = new ArrayList<>();

            for (int i = 0; i < objects.size() - 1; i++) {
                if (!(objects.get(i) instanceof Agent)) {
                    continue;
                }
                for (int j = i + 1; j < objects.size(); j++) {
                    interact((Agent) objects.get(i), objects.get(j));
                }
            }

            GameRuntime.getInstance().getField().removeWeekAgents();

            long currTime = System.currentTimeMillis();
            if (GameRuntime.getInstance().getField().getArtifacts().size() < StaticResource.maxArtifactNumber &&
                    currTime - lastAppearanceArtifactTime > StaticResource.minArtifactAppearanceTime) {
                lastAppearanceArtifactTime = System.currentTimeMillis();
                if (Math.random() < StaticResource.artifactAppearanceFrequency) {
                    double energy = random(StaticResource.minArtifactEnergy, StaticResource.maxArtifactEnergy);
                    double size = StaticResource.sizePerEnergy * energy;
                    Artifact artifact = new Artifact(energy);
                    double x = random(0, StaticResource.fieldWidth - size);
                    double y = random(0, StaticResource.fieldHeight - size);
                    artifact.position.setLocation(x, y);
                    artifact.size = size;
                    GameRuntime.getInstance().getField().getArtifacts().add(artifact);

                    GameRuntime.getInstance().logger.info("\nCreate artifact" +
                            "\nposition(x: " + x + ", y: " + y + ")" +
                            "\nenergy: " + energy + "\n");
                }
            }

            try { Thread.sleep(StaticResource.timeForMove / 2); }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    /*
    * взаимодеуствие агента с обьектом на карте
    * @param agent агент
    * @param object обьект на карте
    */
    private void interact(Agent agent, GameObject object) {
        if (!agent.isAlive) {
            return;
        }
        if (agent.position.distance(object.position) > (agent.size + object.size) / 2) {
            return;
        }

        if(object instanceof Artifact) {
            Artifact artifact = (Artifact) object;
            interactWithArtifact(agent, artifact);
        } else if(object instanceof Agent) {
            interactWithAgent(agent, (Agent) object);
        } else {
            System.out.println("unknown class (func interact, class GameController)");
        }
    }

    /*
    * взаимодеуствие агента с артифактом
    * @param agent агент
    * @param artifact артифакт
    */
    private void interactWithArtifact(Agent agent, Artifact artifact) {
        GameRuntime.getInstance().logger.info("\nAgent find artifact" +
                "\nagent id: " + agent.agentID +
                "\nartifact energy: " + artifact.getEnergy() + "\n");
        agent.addEnergy(artifact.getEnergy());
        GameRuntime.getInstance().getField().removeArtifact(artifact);
    }

    /*
    * взаимодеуствие двух агентов
    * @param agent первый агент
    * @param object второй агент
    */
    private void interactWithAgent(Agent agent, Agent object) {
        if (!object.isAlive) {
            return;
        }

        interactionObjects.add(new AgentPair(agent.agentID, object.agentID));

        for (AgentPair pair: oldInteractionObjects) {
            if (pair.first == agent.agentID && pair.second == object.agentID) {
                return;
            }
        }
        double impulseX = agent.position.x - object.position.x;
        double impulseY = agent.position.y - object.position.y;

        agent.applyCollisionImpulse(new Point2D.Double(impulseX, impulseY));
        object.applyCollisionImpulse(new Point2D.Double(-impulseX, -impulseY));
        if (agent.groupID != object.groupID) {
            agent.makeHit(object.getPower());
            object.makeHit(agent.getPower());

            GameRuntime.getInstance().logger.info("\nAgent hit other agent" +
                    "\nagent id: " + agent.agentID +
                    "\nother agent id: " + object.agentID + "\n");
        }
    }

    /*
    * возвращает случайное значение
    * @param min минимальное значение
    * @param max максимальное значение
    */
    private double random(double min, double max) {
        return min + Math.random() * ((max - min) + 1);
    }
}
