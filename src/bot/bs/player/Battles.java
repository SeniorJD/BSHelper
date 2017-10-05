package bot.bs.player;

import bot.bs.BSMediator;

import java.io.*;
import java.util.*;

import static bot.bs.Util.BATTLE_FINISHED;
import static bot.bs.Util.BATTLE_FINISHED_ALLIANCE;

/**
 * @author SeniorJD
 */
public class Battles {
    Map<String, List<Battle>> battles = new HashMap<>();

    public static Battles getInstance() {
        return INSTANCE;
    }

    private static final Battles INSTANCE = new Battles();

    private Battles() {
        init();
    }

    private void init() {
        File file = new File("battles.bs");

        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                int delimiterIndex = line.indexOf(":");
                String name = line.substring(0, delimiterIndex);
                String battlesSource = line.substring(delimiterIndex + 1);

                String[] battles = battlesSource.split(";");
                if (battles.length > 5) {
                    battles = Arrays.copyOf(battles, 5);
                }

                List<Battle> battlesList = new ArrayList<>();

                for (String battleSource : battles) {
                    String[] battle = battleSource.split(":");
                    boolean won = Boolean.valueOf(battle[0]);
                    int gold = Integer.valueOf(battle[1]);

                    Battle b = new Battle(name, won, gold);

                    boolean contains = false;
                    for (Battle b2 : battlesList) {
                        if (b2.equals(b)) {
                            contains = true;
                            break;
                        }
                    }

                    if (!contains) {
                        battlesList.add(b);
                    }
                }

                this.battles.put(name, battlesList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (battles.size() == 0) {
            return;
        }

        File file = new File("battles.bs");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Map.Entry<String, List<Battle>> entry : battles.entrySet()) {
                writer.write(entry.getKey());
                writer.write(":");
                for (Battle battle : entry.getValue()) {
                    writer.write(String.valueOf(battle.won));
                    writer.write(":");
                    writer.write(String.valueOf(battle.goldPerUnit));
                    writer.write(";");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final String FINISHED = "окончена. ";
    static final String WON = "Поздравляю";
    static final String AWARD = " Твоя награда составила ";
    public void addBattle(String message) {
        if (message.contains(BATTLE_FINISHED_ALLIANCE)) {
            return;
        }

        String originalMessage = message;

        message = message.substring(message.indexOf(BATTLE_FINISHED) + BATTLE_FINISHED.length() + 1);
        if (message.startsWith("[")) {
            message = message.substring(message.indexOf("]") + 1);
        }

        String name = message.substring(0, message.indexOf(FINISHED) - 1);

        message = message.substring(message.indexOf(FINISHED) + FINISHED.length());

        boolean won = message.startsWith(WON);
        int gold = 0;

        if (won) {
            int goldIndex = message.indexOf(AWARD);

            if (goldIndex > -1) {
                message = message.substring(message.indexOf(AWARD) + AWARD.length());

                int moneyBagIndex = message.indexOf(":moneybag:");
                if (moneyBagIndex == -1 ) {
                    moneyBagIndex = message.indexOf("\uD83D\uDCB0");
                }

                String goldS = message.substring(0, moneyBagIndex);

                gold = Integer.parseInt(goldS);

                if (BSMediator.getInstance().barracksLevel != 0) {
                    gold /= BSMediator.getInstance().barracksLevel * 40;
                } else {
                    gold = 100;
                }
            }
        }

        addBattle(name, new Battle(name, won, gold));
    }

    private void addBattle(String name, Battle battle) {
        if (!battles.containsKey(name)) {
            battles.put(name, toList(battle));
        } else {
            List<Battle> battles = this.battles.get(name);

            for (Battle b : battles) {
                if (b.equals(battle)) {
                    return;
                }
            }

            if (battles.size() == 5) {
                battles.remove(0);
            }

            battles.add(battle);
        }

        save();
    }

    private List<Battle> toList(Battle battle) {
        List<Battle> list = new ArrayList<>();

        list.add(battle);

        return list;
    }

    public int getGoldPerUnit(String name) {
        if (!battles.containsKey(name)) {
            return 200;
        }

        List<Battle> battleList = battles.get(name);

        int sum = 0;
        int battlesWon = 0;
        for (Battle battle : battleList) {
            if (!battle.won) {
                continue;
            }

            battlesWon++;
            sum += battle.goldPerUnit;
        }

        if (battlesWon == 0) {
            return 0;
        }

        return sum / battlesWon;
    }
}
