package com.madnes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class ConfigManager {

    /*
    * выставляет статические параметры игры из файла конфигурации
    */
    public static void setGameParamFromConfig() {

        try {
            final File file = new File(GameRuntime.getInstance().sourcesPath + "config.xml");
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(file);

            final NodeList list = document.getElementsByTagName("game");
            if (list.getLength() == 0 || list.item(0).getNodeType() != Node.ELEMENT_NODE) {
                System.out.println("empty config.xml");
                return;
            }
            final Element element = (Element) list.item(0);

            StaticResource.agentSize = Double.parseDouble(element.getAttribute("agentSize"));
            StaticResource.minAgentEnergy = Double.parseDouble(element.getAttribute("minAgentEnergy"));
            StaticResource.energyPerMove = Double.parseDouble(element.getAttribute("energyPerMove"));
            StaticResource.timeForMove = Integer.parseInt(element.getAttribute("timeForMove"));
            StaticResource.minArtifactEnergy = Double.parseDouble(element.getAttribute("minArtifactEnergy"));
            StaticResource.maxArtifactEnergy = Double.parseDouble(element.getAttribute("maxArtifactEnergy"));
            StaticResource.maxArtifactNumber = Integer.parseInt(element.getAttribute("maxArtifactNumber"));
            StaticResource.minArtifactAppearanceTime = Integer.parseInt(element.getAttribute("minArtifactAppearanceTime"));
            StaticResource.artifactAppearanceFrequency = Double.parseDouble(element.getAttribute("artifactAppearanceFrequency"));
            StaticResource.sizePerEnergy = Double.parseDouble(element.getAttribute("sizePerEnergy"));
            StaticResource.fieldHeight = Double.parseDouble(element.getAttribute("fieldHeight"));
            StaticResource.fieldWidth = Double.parseDouble(element.getAttribute("fieldWidth"));

            StaticResource.databaseUrl = element.getAttribute("databaseUrl");
            StaticResource.databaseUser = element.getAttribute("databaseUser");
            StaticResource.databasePassword = element.getAttribute("databasePassword");


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /*
    * возвращает агентов из файла конфигурации
    */
    public static ArrayList<Agent> getAgents() {
        ArrayList<Agent> agents = new ArrayList<Agent>();
        try {
            final File file = new File(GameRuntime.getInstance().sourcesPath + "config.xml");
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(file);

            int currentAgentID = 1;

            final NodeList list = document.getElementsByTagName("group");
            for (int i = 0; i < list.getLength(); i++) {
                final Node node = list.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;

                final Element element = (Element) node;

                final double speed = Double.parseDouble(element.getAttribute("speed"));
                final double energy = Double.parseDouble(element.getAttribute("energy"));
                final double power = Double.parseDouble(element.getAttribute("power"));
                final double armor = Double.parseDouble(element.getAttribute("armor"));
                final double number = Integer.parseInt(element.getAttribute("number"));

                for (int j = 0; j < number; j++) {
                    Agent agent = new Agent(speed, energy, power, armor);
                    agent.agentID = currentAgentID++;
                    agent.groupID = i + 1;
                    agents.add(agent);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return agents;
    }

}
