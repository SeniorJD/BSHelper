package bot;

import bot.bs.BSMediator;
import bot.bs.Util;
import bot.bs.player.Battles;

import java.util.Timer;
import java.util.TimerTask;

import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class Test {
    static String s = "[\uD83D\uDE0E]JD One \n" +
            "JD Land   \n" +
            "\n" +
            "Статус           Город\n" +
            "Территория     42301\uD83D\uDDFA\n" +
            "\n" +
            "Сезон           Лето\uD83C\uDF43\n" +
            "Погода       Облачно☁️\n" +
            "Время       20:16:00\uD83D\uDD53\n" +
            "\n" +
            "Жители          2680\uD83D\uDC65\n" +
            "Армия              0⚔\n" +
            "Золото         55252\uD83D\uDCB0\n" +
            "Дерево        893750\uD83C\uDF32\n" +
            "Камень        893750⛏\n" +
            "Еда           550861\uD83C\uDF56";

    static String s2 = "Постройки\n" +
            "\n" +
            "\uD83C\uDFE4    94⛔️\n" +
            "\uD83C\uDFDA   125⛔️ 1250/1250\uD83D\uDC65\n" +
            "\uD83C\uDFD8   134⛔️ 2395/2680\uD83D\uDC65\n" +
            "\uD83C\uDF3B    70\u200B✅   700/700\uD83D\uDC65\n" +
            "\uD83C\uDF32    50\u200B✅   500/500\uD83D\uDC65\n" +
            "⛏    50\u200B✅   500/500\uD83D\uDC65\n" +
            "\uD83D\uDEE1   120\u200B✅ 4800/4800⚔\n" +
            "\uD83C\uDFF0    30⛔️   300/300\uD83C\uDFF9\n" +
            "\n" +
            "Что будем строить?";

    static String s6 = ":bangbang:Битва с AdeOne окончена. Поздравляю, JD One! Твоя армия одержала победу. Победители 4800⚔ без единой потери гордо возвращаются домой. Твоя награда составила 200081:moneybag:, a 98\uD83D\uDDFA отошли к твоим владениям.";

    public static void main(String[] args) {
        Battles battles = Battles.getInstance();

        battles.addBattle(s6);

        System.out.println(battles.getGoldPerUnit("VINQ"));;
    }

    private static void test3() {
        BSMediator mediator = BSMediator.getInstance();
        mediator.parseMainState(s);

        System.out.println(mediator);
    }

    static void test1() {
        String[] splitted = s2.split("\n");

        for (int i = 0; i < splitted.length; i++) {
            String split = splitted[i];
            System.out.println(i + " " + split);

            String[] split1 = split.split("\\D");
//            String[] splitted2 = s2.split("\\s");
            for (int j = 0; j < split1.length; j++) {
                System.out.println( j + " " + split1[j]);
            }
        }
    }

    static void test2() {
        String s2 = "42301\uD83D\uDDFA";
        System.out.println(s2.matches("([\\d]+)("+ Util.TERRITORY_SIGN+"?)"));

        if (s2.contains(TERRITORY_SIGN)) {
            s2 = s2.substring(0, s2.indexOf(TERRITORY_SIGN));
        }

        int territory = Integer.valueOf(s2);
        System.out.println(territory);
    }

    static void test4() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("finished");
            }
        }, 1000 * 3);
    }

    static void test5() {
        int nextBuildingToUpgrade = -1;

        int stockCapacity = getStockCapacity(126);

        int goldKoef = getGoldKoef(Util.HOUSE);
        int woodKoef = getWoodKoef(Util.HOUSE);
        int stoneKoef = getStoneKoef(Util.HOUSE);

        int goldRequiredForHouse = getRequiredAmount(135, goldKoef);
        int woodRequiredForHouse = getRequiredAmount(135, woodKoef);
        int stoneRequiredForHouse = getRequiredAmount(135, stoneKoef);

        goldKoef = getGoldKoef(Util.TOWN);
        woodKoef = getWoodKoef(Util.TOWN);
        stoneKoef = getStoneKoef(Util.TOWN);
        int goldRequiredForTown = getRequiredAmount(95, goldKoef);
        int woodRequiredForTown = getRequiredAmount(95, woodKoef);
        int stoneRequiredForTown = getRequiredAmount(95, stoneKoef);

        if ((woodRequiredForHouse > stockCapacity || stoneRequiredForHouse > stockCapacity) &&
                (woodRequiredForTown > stockCapacity || stoneRequiredForTown > stockCapacity)) {
            nextBuildingToUpgrade = Util.STOCK;
        } else {
            if (woodRequiredForTown < stockCapacity && stoneRequiredForTown < stockCapacity) {
                nextBuildingToUpgrade = Util.HOUSE;
            } else {
                nextBuildingToUpgrade = Util.TOWN;
            }
        }

        int goldRequired;
        int woodRequired;
        int stoneRequired;

        if (nextBuildingToUpgrade == Util.STOCK) {
            goldKoef = getGoldKoef(Util.STOCK);
            woodKoef = getWoodKoef(Util.STOCK);
            stoneKoef = getStoneKoef(Util.STOCK);

            goldRequired = getRequiredAmount(126, goldKoef);
            woodRequired = getRequiredAmount(126, woodKoef);
            stoneRequired = getRequiredAmount(126, stoneKoef);
        } else if (nextBuildingToUpgrade == Util.TOWN) {
            goldRequired = goldRequiredForTown;
            woodRequired = woodRequiredForTown;
            stoneRequired = stoneRequiredForTown;
        } else {
            goldRequired = goldRequiredForHouse;
            woodRequired = woodRequiredForHouse;
            stoneRequired = stoneRequiredForHouse;
        }

        System.out.println(goldRequired + "; " + woodRequired + "; " + stoneRequired);
    }

    static void test6() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("first");
            }
        }, 3000);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("second");
            }
        }, 3000);
    }
}
