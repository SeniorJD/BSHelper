package bot.bs;

/**
 * @author SeniorJD
 */
public class Util {

    public static final String BS_BOT_NAME = "BastionSiegeBot";
    public static final String BS_HELPER_NAME = "BastionSiegeHelper";

    public static final String SEASON = "Сезон";
    public static final String BUILDINGS_MENU = "Постройки";
    public static final String CANNOT_BUILD = "К сожалению, у тебя не хватает";
    public static final String DEFENCE_STARTED = "Твои владения атакованы!";
    public static final String ALLY_ATTACKED = "! Ты можешь отправить свою армию помогать защищаться.";
    public static final String ALLY_ATTACKS = "Ты можешь выступить на подмогу";
    public static final String ALLY_ATTACKS2 = "Ты можешь вступить в бой";
    public static final String YOU_WIN = "Твоя награда составила";
    public static final String YOU_LOST = "Твоя казна опустела на";
    public static final String HINT = ":speech_balloon:";
    public static final String NO_MONEY_NO_HONEY = "Сначала подкопи деньжат.";
    public static final String NO_STOCK_PLACE = "Купить можем, но складывать некуда.";
    public static final String ATTACK_STARTED = "⚔Осада началась!";
    public static final String BATTLE_FINISHED = "Битва с";
    public static final String BATTLE_FINISHED_ALLIANCE = "Битва с альянсом";
    public static final String BATTLE_UNFINISHED = "Текущая война сказывается на каждом";
    public static final String NO_MONEY = "Чтобы организовать хорошую разведку, нужно";
    public static final String NO_FOOD = "Отправить солдат в дозор без";
    public static final String RECRUITING_ARMY_DONE_WELL = "⚔Армии";
    public static final String RECRUITING_NO_PEOPLE = "У тебя не хватает свободных людей. Улучшай Дома, чтобы иметь больше людей.";
    public static final String REPORT_SENT = "Почтовый голубь отправлен Создателю.";
    public static final String CAMPAIGN_FINISHED = "⚔ ";
    public static final String CAMPAIGN_SUCCESSFUL = "казна пополнилась на";
    public static final String REPAIR_WALL = "Чинить";
    public static final String ALREADY_IN_BATTLE = "Воевать на два фронта слишком накладно.";
    public static final String ALLIANCE_DEFENCE_JOINED = "\uD83D\uDD4A\uD83D\uDEE1Твоя армия присоединилась к атаке.";
    public static final String ALLIANCE_ATTACK_JOINED = "\uD83D\uDD4A⚔Твоя армия присоединилась к атаке.";
    public static final String ARMY_JOINED_DEFENCE = "присоединилась к защите!";
    public static final String ARMY_JOINED_ATTACK = "присоединилась к атаке!";
    public static final String FINISHED = "окончена. ";
    public static final String WON = "Поздравляю";
    public static final String AWARD = " Твоя награда составила ";
    public static final String NEXT_ATTACK = "Следующая атака";
    public static final String NEXT_ATTACK_MINS = "мин.";
    public static final String NEXT_ATTACK_SECS = "сек.";
    public static final String ENEMY_UNDER_IMMUN = "еще не оправился от предыдущей битвы.";
    public static final String WATCHERS_GONE = "⚔ Дозорные отправились в путь.";
    public static final String ARMY_WORD = "Армия";

    public static final String CONTROL_UP = ":arrow_up: Наверх";
    public static final String CONTROL_BACK = ":arrow_left: Назад";
    public static final String CONTROL_BUILDINGS = "\uD83C\uDFD8 Постройки";
    public static final String CONTROL_WORKSHOP = "⚒ Мастерская";
    public static final String CONTROL_TREBUCHET = "⚔Требушет";
    public static final String CONTROL_TREBUCHET2 = "⚔ Требушет";
    public static final String CONTROL_TOWN = ":european_post_office: Ратуша";
    public static final String CONTROL_TRADE = ":circus_tent: Торговля";
    public static final String CONTROL_BUY = ":moneybag: Купить";
    public static final String CONTROL_BUY_WOOD = ":evergreen_tree: Дерево";
    public static final String CONTROL_BUY_STONE = "⛏ Камень";
    public static final String CONTROL_BUY_FOOD = ":meat_on_bone: Еда";
    public static final String CONTROL_STOCK = "\uD83C\uDFDA Склад";
    public static final String CONTROL_HOUSE = "\uD83C\uDFD8 Дома";
    public static final String CONTROL_FARM = ":sunflower: Ферма";
    public static final String CONTROL_SAWMILL = ":evergreen_tree: Лесопилка";
    public static final String CONTROL_MINES = "⛏ Шахта";
    public static final String CONTROL_BARRACKS = "\uD83D\uDEE1 Казармы";
    public static final String CONTROL_WALL = ":european_castle: Стена";
    public static final String CONTROL_UPGRADE = "⚒ Улучшить";
    public static final String CONTROL_WAR = "⚔ Война";
    public static final String CONTROL_FIND_ALL = ":mag_right: Всех";
    public static final String CONTROL_FIND_APPROPRIATE = ":mag_right: Подходящих";
    public static final String CONTROL_RECRUIT = ":heavy_plus_sign: Обучить";
    public static final String CONTROL_REPAIR = "⚒ Чинить";
    public static final String CONTROL_INFO = ":scroll: Инфо";

    public static final String DIGIT_REGEX = "\\D";

    public static final String TERRITORY_SIGN = "\uD83D\uDDFA";
    public static final String TERRITORY_REGEX = "([\\d]+)("+ TERRITORY_SIGN +"?)";

    public static final String POPULATION_SIGN = "\uD83D\uDC65";
    public static final String POPULATION_REGEX = "([\\d]+)("+ POPULATION_SIGN +"?)";

    public static final String ARMY_SIGN = "⚔";
    public static final String ARMY_REGEX = "([\\d]+)("+ ARMY_SIGN +"?)";

    public static final String ARCHERS_KEYWORD = "Лучники";
    public static final String WALL_RUINED_KEYWORD = "Чинить";
    public static final String POPULATION_KEYWORD = "Жители";
    public static final String GOLD_KEYWORD = "Золото";

    public static final String GOLD_SIGN = "\uD83D\uDCB0";
    public static final String GOLD_REGEX = "([\\d]+)("+ GOLD_SIGN +"?)";

    public static final String WOOD_SIGN = "\uD83C\uDF32";
    public static final String WOOD_SIGN2 = ":evergreen_tree:";
    public static final String WOOD_REGEX = "([\\d]+)("+ WOOD_SIGN +"?)";

    public static final String STONE_SIGN = "⛏";
    public static final String STONE_SIGN2 = "⛏";
    public static final String STONE_REGEX = "([\\d]+)("+ STONE_SIGN +"?)";

    public static final String FOOD_SIGN = "\uD83C\uDF56";
    public static final String FOOD_SIGN2 = ":meat_on_bone:";
    public static final String FOOD_REGEX = "([\\d]+)("+ FOOD_SIGN +"?)";

    public static final String TOWN_SIGN = "\uD83C\uDFE4";
    public static final String STOCK_SIGN = "\uD83C\uDFDA";
    public static final String HOUSE_SIGN = "\uD83C\uDFD8";
    public static final String FARM_SIGN = "\uD83C\uDF3B";
    public static final String SAWMILL_SIGN = "\uD83C\uDF32";
    public static final String STONE_QUARRY_SIGN = "⛏";
    public static final String BARRACKS_SIGN = "\uD83D\uDEE1";
    public static final String WALL_SIGN = "\uD83C\uDFF0";
    public static final String TREBUCHET_SIGN = "⚔Требушет";

    public static final String KARMA_SIGN = "☯";

    public static int getRequiredAmount(int currentLvl, int koef) {
        int nextLvl = currentLvl + 1;
        int part1 = (int) (nextLvl*(nextLvl-1)*((2.*nextLvl+8)/6+2./nextLvl));
        int part2 = (int) (currentLvl*(currentLvl-1)*((2.*currentLvl+8)/6+2./currentLvl));
        return koef * (part1 - part2) / 2;
    }

    public static final int TOWN = 0;
    public static final int STOCK = 1;
    public static final int HOUSE = 2;
    public static final int FARM = 3;
    public static final int SAWMILL = 4;
    public static final int MINES = 5;
    public static final int BARRACKS = 6;
    public static final int WALL = 7;
    public static final int TREBUCHET = 8;

    public static int getGoldKoef(int buildingIndex) {
        switch (buildingIndex) {
            case TOWN:
                return 500;
            case STOCK:
                return 200;
            case HOUSE:
                return 200;
            case SAWMILL:
                return 100;
            case MINES:
                return 100;
            case FARM:
                return 100;
            case BARRACKS:
                return 200;
            case WALL:
                return 5000;
            case TREBUCHET:
                return 8000;
        }

        return -1; // throw exception?
    }

    public static int getWoodKoef(int buildingIndex) {
        switch (buildingIndex) {
            case TOWN:
                return 200;
            case STOCK:
                return 100;
            case HOUSE:
                return 100;
            case SAWMILL:
                return 50;
            case MINES:
                return 50;
            case FARM:
                return 50;
            case BARRACKS:
                return 100;
            case WALL:
                return 500;
            case TREBUCHET:
                return 1000;
        }

        return -1; // throw exception?
    }

    public static int getStoneKoef(int buildingIndex) {
        switch (buildingIndex) {
            case TOWN:
                return 200;
            case STOCK:
                return 100;
            case HOUSE:
                return 100;
            case SAWMILL:
                return 50;
            case MINES:
                return 50;
            case FARM:
                return 50;
            case BARRACKS:
                return 100;
            case WALL:
                return 1500;
            case TREBUCHET:
                return 300;
        }

        return -1; // throw exception?
    }

    public static int getStockCapacity(int level) {
        return (level * 50 + 1000) * level;
    }

    public static final String[] ALLIANCE_TRIADA = new String[] {
            "\uD83D\uDDE1\u200B",
            "\uD83D\uDDE1",
            ":dagger:"
    };

    public static final String[] ALLIANCE_RAIDERS = new String[] {
            "\uD83D\uDEE1\u200B",
            "\uD83D\uDEE1",
            ":shield:"
    };

    public static final String[] ALLIANCE_FREEDOM = new String[] {
            "⭐️",
            "⭐",
            ":star:"
    };

    public static final String[] ALLIANCE_FTARS = new String[] {
            "\uD83C\uDF1A\u200B",
            "\uD83C\uDF1A",
            ":new_moon_with_face:"
    };

    public static final String[] ALLIANCE_SHMALLIANCE = new String[] {
            "\uD83D\uDD31\u200B",
            "\uD83D\uDD31",
            ":trident:"
    };

    public static final String[] ALLIANCE_COWS = new String[] {
            "\uD83D\uDC04\u200B",
            "\uD83D\uDC04",
            ":cow2:"
    };

    public static final String[] ALLIANCE_CONFEDERATION = new String[] {
            "☣\u200B",
            "☣",
            ":biohazard:"
    };

    public static final String[] ALLIANCE_FOX = new String[] {
            "\uD83E\uDD8A\u200B",
            "\uD83E\uDD8A",
            ":fox:"
    };

    public static final String[] ALLIANCE_SMILEY = new String[] {
            "\uD83D\uDE08\u200B",
            "\uD83D\uDE08",
            ":smiling_imp:"
    };

    public static final String[] ALLIANCE_RABBIT = new String[] {
            "\uD83D\uDC30\u200B",
            "\uD83D\uDC30",
            ":rabbit:"
    };

    public static final String[] ALLIANCE_FOREST = new String[] {
            "\uD83C\uDF32\u200B",
            "\uD83C\uDF32",
            ":evergreen_tree:"
    };

    public static final String[] ALLIANCE_SNAKES = new String[] {
            "\uD83D\uDC09\u200B",
            "\uD83D\uDC09",
            ":dragon:"
    };

    public static final String[] ALLIANCE_ANCHOR = new String[] {
            "⚓\u200B",
            "⚓",
            ":anchor:"
    };

    public static final String[] ALLIANCE_WOLVES = new String[] {
            "\uD83D\uDC3A\u200B",
            "\uD83D\uDC3A",
            ":wolf:"
    };

    public static final String[] ALLIANCE_SWORDS = new String[] {
            "⚔\u200B",
            "⚔",
            ":crossed_swords:"
    };

    public static final String[] ALLIANCE_MEMES = new String[] {
            "☠️",
            "☠",
            ":skull_and_crossbones:"
    };

    public static final String[] ALLIANCE_LEGION = new String[] {
            "\uD83E\uDD85\u200B",
            "\uD83E\uDD85",
            ":eagle:"
    };

    public static final String[] ALLIANCE_POO = new String[] {
            "\uD83D\uDCA9\u200B",
            "\uD83D\uDCA9",
            ":poo:"
    };

    public static final String[] ALLIANCE_BUTTERFLY = new String[] {
            "\uD83E\uDD8B\u200B",
            "\uD83E\uDD8B",
            ":butterfly:"
    };

    public static final String[] ALLIANCE_ALIENS = new String[] {
            "\uD83D\uDC7D\u200B",
            "\uD83D\uDC7D",
            ":alien:"
    };

    public static final String[] ALLIANCE_CAT = new String[] {
            "\uD83D\uDC31\u200B",
            "\uD83D\uDC31",
            ":cat:"
    };

    public static final String[] ALLIANCE_EYE = new String[] {
            "\uD83D\uDC41\u200B",
            "\uD83D\uDC41",
            ":eye:"
    };

    public static final String[] ALLIANCE_GUITAR = new String[] {
            "\uD83C\uDFB8\u200B",
            "\uD83C\uDFB8",
            ":guitar:"
    };

    public static final String[][] ALLIANCES = new String[23][3];
    static {
        int i = 0;
        ALLIANCES[i++] = ALLIANCE_TRIADA;
        ALLIANCES[i++] = ALLIANCE_RAIDERS;
        ALLIANCES[i++] = ALLIANCE_FREEDOM;
        ALLIANCES[i++] = ALLIANCE_FTARS;
        ALLIANCES[i++] = ALLIANCE_SHMALLIANCE;
        ALLIANCES[i++] = ALLIANCE_COWS;
        ALLIANCES[i++] = ALLIANCE_CONFEDERATION;
        ALLIANCES[i++] = ALLIANCE_FOX;
        ALLIANCES[i++] = ALLIANCE_SMILEY;
        ALLIANCES[i++] = ALLIANCE_RABBIT;
        ALLIANCES[i++] = ALLIANCE_FOREST;
        ALLIANCES[i++] = ALLIANCE_SNAKES;
        ALLIANCES[i++] = ALLIANCE_ANCHOR;
        ALLIANCES[i++] = ALLIANCE_WOLVES;
        ALLIANCES[i++] = ALLIANCE_SWORDS;
        ALLIANCES[i++] = ALLIANCE_MEMES;
        ALLIANCES[i++] = ALLIANCE_LEGION;
        ALLIANCES[i++] = ALLIANCE_POO;
        ALLIANCES[i++] = ALLIANCE_BUTTERFLY;
        ALLIANCES[i++] = ALLIANCE_ALIENS;
        ALLIANCES[i++] = ALLIANCE_CAT;
        ALLIANCES[i++] = ALLIANCE_EYE;
        ALLIANCES[i] = ALLIANCE_GUITAR;
    }

    public static String translateAllianceIfNeeded(String alliance) {
        if (alliance == null || alliance.isEmpty()) {
            return alliance;
        }

        for (int i = 0; i < ALLIANCES.length; i++) {
            String[] allInfo = ALLIANCES[i];
            for (int j = 0; j < allInfo.length; j++) {
                if (alliance.equals(allInfo[j])) {
                    return allInfo[0];
                }
            }
        }

        return alliance;
    }
}
