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
    public static final String OPPONENT_KEY = "opponent";
    public static final String ATTACK_IF_MEET_KEY = "attackIfMeet";
    public static final String ALLY_ALLIANCES = "allyAlliances";
    public static final String ALLY_PLAYERS = "allyPlayers";
    public static final String BUILDING_SCENARIO = "building";
    public static final String RISKY_ATTACK = "riskyAttack";
    public static final String SEARCH_APPROPRIATE = "searchAppropriate";
    public static final String GIVE_IMMUN = "giveImmun";
    public static final String MAX_SEARCH = "maxSearch";
    public static final String ATTACK_CONQUEROR = "attackConqueror";

    private static int goldToChange = 100000;
    private static int maxSearch = 100;
    private static boolean autoAttack = true;
    private static boolean autoSearch = true;
    private static boolean autoBuild = true;
    private static boolean riskyAttack = false;
    private static boolean searchAppropriate = false;
    private static boolean giveImmun = false;
    private static boolean attackConqueror = false;
    private static String buildingScenario = "0 2 1";
    private static String opponent = "";
    private static String attackIfMeet = "";
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
                    case OPPONENT_KEY: {
                        if (arr.length > 1) {
                            opponent = (arr[1]);
                        } else {
                            opponent = "";
                        }
                        break;
                    }
                    case ATTACK_IF_MEET_KEY: {
                        if (arr.length > 1) {
                            attackIfMeet = (arr[1]);
                        } else {
                            attackIfMeet = "";
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
                        buildingScenario = arr[1];
                        break;
                    }
                    case RISKY_ATTACK: {
                        if (arr.length == 1) {
                            break;
                        }
                        riskyAttack = Boolean.valueOf(arr[1]);
                        break;
                    }
                    case MAX_SEARCH: {
                        if (arr.length == 1) {
                            break;
                        }
                        maxSearch = Integer.valueOf(arr[1]);
                        break;
                    }
                    case SEARCH_APPROPRIATE: {
                        if (arr.length == 1) {
                            break;
                        }
                        searchAppropriate = Boolean.valueOf(arr[1]);
                        break;
                    }
                    case GIVE_IMMUN: {
                        if (arr.length == 1) {
                            break;
                        }
                        giveImmun = Boolean.valueOf(arr[1]);
                        break;
                    }
                    case ATTACK_CONQUEROR:
                        if (arr.length == 1) {
                            break;
                        }
                        attackConqueror = Boolean.valueOf(arr[1]);
                        break;
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
                RISKY_ATTACK + SPLITTER + riskyAttack + "\n" +
                SEARCH_APPROPRIATE + SPLITTER + searchAppropriate + "\n" +
                GIVE_IMMUN + SPLITTER + giveImmun + "\n" +
                OPPONENT_KEY + SPLITTER + opponent + "\n" +
                ATTACK_IF_MEET_KEY + SPLITTER + attackIfMeet + "\n" +
                MAX_SEARCH + SPLITTER + maxSearch + "\n" +
                BUILDING_SCENARIO + SPLITTER + buildingScenario + "\n" +
                ATTACK_CONQUEROR + SPLITTER + attackConqueror + "\n" +
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

    public static String getAttackIfMeet() {
        return attackIfMeet;
    }

    public static void setAttackIfMeet(String attackIfMeet) {
        Settings.attackIfMeet = attackIfMeet;
    }

    public static String getFindOpponent() {
        return opponent;
    }

    public static void setOpponent(String findOpponent) {
        Settings.opponent = findOpponent;
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

    public static void setBuildingScenario(String buildingScenario) {
        Settings.buildingScenario = buildingScenario;
        saveSettings();
    }

    public static String getBuildingScenario() {
        return buildingScenario;
    }

    public static void setRiskyAttack(boolean riskyAttack) {
        Settings.riskyAttack = riskyAttack;
        saveSettings();
    }

    public static boolean isRiskyAttackEnabled() {
        return riskyAttack;
    }

    public static void setMaxSearch(int maxSearch) {
        Settings.maxSearch = maxSearch;
    }

    public static int getMaxSearch() {
        return maxSearch;
    }

    public static void setSearchAppropriate(boolean searchAppropriate) {
        Settings.searchAppropriate = searchAppropriate;
    }

    public static boolean isSearchAppropriate() {
        return searchAppropriate;
    }

    public static void setGiveImmun(boolean giveImmun) {
        Settings.giveImmun = giveImmun;
    }

    public static boolean isGiveImmun() {
        return giveImmun;
    }

    public static void setAttackConqueror(boolean attackConqueror) {
        Settings.attackConqueror = attackConqueror;
    }

    public static boolean isAttackConqueror() {
        return attackConqueror;
    }
}
