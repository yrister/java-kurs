package com.madnes;

import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GameRuntime {
    static private final GameRuntime HOLDER_INSTANCE = new GameRuntime();
    public static GameRuntime getInstance() {
        return HOLDER_INSTANCE;
    }
    private GameRuntime() {} // приватный конструктор, чтоб неповадно было плодить синглтон

    private GameController controller = new GameController(); // контроллер карты (отвечает за взаимодействия обьектов и проверку условия прекращения игры)
    private Field field = new Field(); // поле с обьектами
    private boolean _isPlaying = false; // активна ли игра

    public boolean isPlaying() {
        return _isPlaying;
    }

    public Field getField() {
        return field;
    }
    public int gameID = DatabaseManager.getMaxGameID();;

    public String sourcesPath = "/Users/Roman/Downloads/madness people/out/artifacts/madness_people_war_exploded/";
    public Logger logger = Logger.getLogger("MyLog");

    /*
    * создание обьектов игры и ее запуск
    */
    public void start() {
        gameID++;

        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter
            String logPath = sourcesPath + "game_log_" + gameID + ".txt";
            fh = new FileHandler(logPath);
            System.out.println("See logs at: " + logPath);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("\nStart  game #" + gameID + "\n");

        _isPlaying = true;

        ConfigManager.setGameParamFromConfig();

        field = new Field();

        ArrayList<Agent> agents = ConfigManager.getAgents();
        field.setAgents(agents);
        field.setArtifacts(new ArrayList<Artifact>());

        for (Agent agent: agents) {
            double halfSize = StaticResource.agentSize / 2;
            double x = random(halfSize, StaticResource.fieldWidth - halfSize);
            double y = random(halfSize, StaticResource.fieldHeight - halfSize);
            agent.position.setLocation(x, y);
            agent.size = StaticResource.agentSize;
            Thread thread = new Thread(agent);
            thread.start();
            logger.info("\nCreate agent" +
                    "\ngroup: " + agent.groupID +
                    "\nid: " + agent.agentID +
                    "\nposition(x: " + x + ", y: " + y + ")" +
                    "\nenergy: " + agent.getEnergy() + "\n");
        }
        Thread thread = new Thread(controller);
        thread.start();
    }

    /*
    * остановка игры
    */
    public void stop() {

        if (_isPlaying == false) {
            field = new Field();
            return;
        }

        _isPlaying = false;

        logger.info("\nStop  game #" + gameID + "\n");


        for (Handler handler: logger.getHandlers()) {
            logger.removeHandler(handler);
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
