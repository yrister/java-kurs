package com.madnes;

public class StaticResource {

    // параметры агента
    static public double minAgentEnergy = 5;
    static public double energyPerMove = 0.02; // зависимость потраченой енергии от смещения агента
    static public long timeForMove = 100; // msec

    // параметры артифакта
    static public double minArtifactEnergy = 20;
    static public double maxArtifactEnergy = 40;
    static public int maxArtifactNumber = 10;
    static public long minArtifactAppearanceTime = 900; // msec // время между появлениями артифакта
    static public double artifactAppearanceFrequency = 0.3; // 0...1

    // поле и обьекты
    static public double fieldHeight = 600;
    static public double fieldWidth = 900;
    static public double agentSize = 30;
    static public double sizePerEnergy = 1; // зависимость размера артифакта от енергии

    // база данных
    static public String databaseUrl;
    static public String databaseUser;
    static public String databasePassword;
}
