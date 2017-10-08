package bot.bs;

import bot.bs.scenarios.BSSender;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SeniorJD
 */
public class Settings {
    private static final String SPLITTER = "=";
    public static final String GOLD_TO_CHANGE_KEY = "goldToChange";
    public static final String AUTO_ATTACK_KEY = "autoAttack";
    public static final String AUTO_SEARCH_KEY = "autoSearch";
    public static final String AUTO_BUILD_KEY = "autoBuild";
    public static final String FIND_KEY = "find";
    public static final String ALLY_ALLIANCES = "allyAlliances";
    public static final String ALLY_PLAYERS = "allyPlayers";
    public static final String BUILDING_SCENARIO = "buildingScenario";

    private static int goldToChange = 100000;
    private static boolean autoAttack = true;
    private static boolean autoSearch = true;
    private static boolean autoBuild = true;
    private static int buildingScenario = 0;
    private static String findOpponent = "";
    private static List<String> allyAlliances = new ArrayList<>();
    private static List<String> allyPlayers = new ArrayList<>();

    public static void readSettings() {
        File file = new File("settings.bs");

        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(SPLITTER);

                switch (arr[0]) {
                    case GOLD_TO_CHANGE_KEY:
                        if (arr.length == 1) {
                            break;
                        }
                        goldToChange = Integer.valueOf(arr[1]);
                        break;
                    case AUTO_ATTACK_KEY:
                        if (arr.length == 1) {
                            break;
                        }
                        autoAttack = Boolean.valueOf(arr[1]);
                        break;
                    case AUTO_SEARCH_KEY:
                        if (arr.length == 1) {
                            break;
                        }
                        autoSearch = Boolean.valueOf(arr[1]);
                        break;
                    case AUTO_BUILD_KEY:
                        if (arr.length == 1) {
                            break;
                        }
                        autoBuild = Boolean.valueOf(arr[1]);
                        break;
                    case FIND_KEY: {
                        if (arr.length > 1) {
                            findOpponent = (arr[1]);
                        } else {
                            findOpponent = "";
                        }
                        break;
                    }
                    case ALLY_ALLIANCES: {
                        if (arr.length == 1) {
                            break;
                        }
                        String[] allies = arr[1].split(";");
                        for (String ally : allies) {
                            if (ally.isEmpty()) {
                                continue;
                            }

                            allyAlliances.add(ally);
                        }
                        break;
                    }
                    case ALLY_PLAYERS: {
                        if (arr.length == 1) {
                            break;
                        }
                        String[] allies = arr[1].split(";");
                        for (String ally : allies) {
                            if (ally.isEmpty()) {
                                continue;
                            }

                            allyPlayers.add(ally);
                        }
                        break;
                    }
                    case BUILDING_SCENARIO: {
                        if (arr.length == 1) {
                            break;
                        }
                        buildingScenario = Integer.valueOf(arr[1]);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings() {
        File file = new File("settings.bs");

        try {
            file.createNewFile();

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(generateSettings());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateSettings() {
        return GOLD_TO_CHANGE_KEY + SPLITTER + goldToChange + "\n" +
                AUTO_ATTACK_KEY + SPLITTER + autoAttack + "\n" +
                AUTO_SEARCH_KEY + SPLITTER + autoSearch + "\n" +
                AUTO_BUILD_KEY + SPLITTER + autoBuild + "\n" +
                FIND_KEY + SPLITTER + findOpponent + "\n" +
                BUILDING_SCENARIO + SPLITTER + buildingScenario + "\n" +
                generateAllyAlliancesValues() +
                generateAllyPlayersValues();
    }

    public static String generateAllyAlliancesValues() {
        StringBuilder sb = new StringBuilder();

        sb.append(ALLY_ALLIANCES).append(SPLITTER);

        for (int i = 0; i < allyAlliances.size(); i++) {
            sb.append(allyAlliances.get(i));

            if (i < allyAlliances.size() - 1) {
                sb.append(";");
            }
        }

        sb.append("\n");

        return sb.toString();
    }

    public static String generateAllyPlayersValues() {
        StringBuilder sb = new StringBuilder();

        sb.append(ALLY_PLAYERS).append(SPLITTER);

        for (int i = 0; i < allyPlayers.size(); i++) {
            sb.append(allyPlayers.get(i));

            if (i < allyPlayers.size() - 1) {
                sb.append(";");
            }
        }

        sb.append("\n");

        return sb.toString();
    }

    public static void printSettings(BSSender sender) {
        String s = generateSettings();
        sender.sendHelperMessage(s);
    }

    public static int getGoldToChange() {
        return goldToChange;
    }

    public static void setGoldToChange(int goldToChange) {
        Settings.goldToChange = goldToChange;
        saveSettings();
    }

    public static boolean isAutoAttack() {
        return autoAttack;
    }

    public static void setAutoAttack(boolean autoAttack) {
        Settings.autoAttack = autoAttack;
        saveSettings();
    }

    public static boolean isAutoSearch() {
        return autoSearch;
    }

    public static void setAutoSearch(boolean autoSearch) {
        Settings.autoSearch = autoSearch;
        saveSettings();
    }

    public static boolean isAutoBuild() {
        return autoBuild;
    }

    public static void setAutoBuild(boolean autoBuild) {
        Settings.autoBuild = autoBuild;
        saveSettings();
    }

    public static String getFindOpponent() {
        return findOpponent;
    }

    public static void setFindOpponent(String findOpponent) {
        Settings.findOpponent = findOpponent;
        saveSettings();
    }

    public static List<String> getAllyAlliances() {
        return allyAlliances;
    }

    public static void addAllyAlliance(String alliance) {
        allyAlliances.remove(alliance);
        allyAlliances.add(alliance);
        saveSettings();
    }

    public static void removeAllyAlliance(String alliance) {
        allyAlliances.remove(alliance);
        saveSettings();
    }

    public static List<String> getAllyPlayers() {
        return allyPlayers;
    }

    public static void addAllyPlayer(String player) {
        allyPlayers.remove(player);
        allyPlayers.add(player);
        saveSettings();
    }

    public static void removeAllyPlayer(String player) {
        allyPlayers.remove(player);
        saveSettings();
    }

    public static void setBuildingScenario(int buildingScenario) {
        Settings.buildingScenario = buildingScenario;
    }

    public static int getBuildingScenario() {
        return buildingScenario;
    }
}
