package bot.bs;

import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class BSMediator {

    private static BSMediator INSTANCE = new BSMediator();

    private BSMediator() {}

    public static BSMediator getInstance() {
        return INSTANCE;
    }

    public int territory = 0;
    public int population = 0;
    public int army = 0;
    public int gold = 0;
    public int wood = 0;
    public int stone = 0;
    int food = 0;

    public int townLevel = 0;
    public int stockLevel = 0;
    public int houseLevel = 0;
    int farmLevel = 0;
    int sawmillLevel = 0;
    int stoneQuarryLevel = 0;
    public int barracksLevel = 0;
    int wallLevel = 0;

    public int goldPerMinute = 0;
    int foodPerMinute = 0;

    public int nextBuildingToUpgrade = -1;
    public long timeToWait = 0;

    int goldRequired = 0;
    public int woodRequired = 0;
    public int stoneRequired = 0;
    public int foodRequired = 0;

    public boolean inBattle = false;

    @Override
    public String toString() {
        return "territory: " + territory + "\n" +
                "population: " + population + "\n" +
                "army: " + army + "\n" +
                "gold: " + gold + "\n" +
                "wood: " + wood + "\n" +
                "stone: " + stone + "\n" +
                "food: " + food;
    }

    public void parseMainState(String message) {
        String[] splitted = message.split("\n");

        for (String s : splitted) {
            if (s.contains(TERRITORY_SIGN)) {
                parseTerritory(s);
            } else if (s.contains(POPULATION_SIGN)) {
                parsePopulation(s);
            } else if (s.contains(ARMY_SIGN)) {
                parseArmy(s);
            } else if (s.contains(GOLD_SIGN)) {
                parseGold(s);
            } else if (s.contains(WOOD_SIGN)) {
                parseWood(s);
            } else if (s.contains(STONE_SIGN)) {
                parseStone(s);
            } else if (s.contains(FOOD_SIGN)) {
                parseFood(s);
            }
        }
    }

    public void parseBuildingsState(String message) {
        String[] splitted = message.split("\n");

        for (String s : splitted) {
            if (s.contains(TOWN_SIGN)) {
                parseTown(s);
            } else if (s.contains(STOCK_SIGN)) {
                parseStock(s);
            } else if (s.contains(HOUSE_SIGN)) {
                parseHouse(s);
            } else if (s.contains(FARM_SIGN)) {
                parseFarm(s);
            } else if (s.contains(SAWMILL_SIGN)) {
                parseSawmill(s);
            } else if (s.contains(STONE_QUARRY_SIGN)) {
                parseStoneQuarry(s);
            } else if (s.contains(BARRACKS_SIGN)) {
                parseBarracks(s);
            } else if (s.contains(WALL_SIGN)) {
                parseWall(s);
            }
        }

        foodPerMinute = farmLevel * 10 - houseLevel * 10;
        goldPerMinute = (int) (houseLevel * 10 + (townLevel * houseLevel * 2));

        refreshUpgradeList();
    }

    void parseTown(String string) {
        String[] digits = string.split(DIGIT_REGEX);

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            townLevel = Integer.valueOf(digit);
            break;
        }
    }

    void parseHouse(String string) {
        String[] digits = string.split(DIGIT_REGEX);

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            houseLevel = Integer.valueOf(digit);
            break;
        }
    }

    void parseStock(String string) {
        String[] digits = string.split(DIGIT_REGEX);

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            stockLevel = Integer.valueOf(digit);
            break;
        }
    }

    void parseFarm(String string) {
        String[] digits = string.split(DIGIT_REGEX);

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            farmLevel = Integer.valueOf(digit);
            break;
        }
    }

    void parseSawmill(String string) {
        String[] digits = string.split(DIGIT_REGEX);

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            sawmillLevel = Integer.valueOf(digit);
            break;
        }
    }

    void parseStoneQuarry(String string) {
        String[] digits = string.split(DIGIT_REGEX);

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            stoneQuarryLevel = Integer.valueOf(digit);
            break;
        }
    }

    void parseBarracks(String string) {
        String[] digits = string.split(DIGIT_REGEX);

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            barracksLevel = Integer.valueOf(digit);
            break;
        }
    }

    void parseWall(String string) {
        String[] digits = string.split(DIGIT_REGEX);

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            wallLevel = Integer.valueOf(digit);
            break;
        }
    }

    void parseTerritory(String string) {
        String[] splitted2 = string.split("\\s");
        for (String s : splitted2) {
            if (!s.isEmpty() && s.matches(TERRITORY_REGEX)) {
                if (s.contains(TERRITORY_SIGN)) {
                    s = s.substring(0, s.indexOf(TERRITORY_SIGN));
                }

                territory = Integer.valueOf(s);

                break;
            }
        }
    }

    void parsePopulation(String string) {
        String[] splitted2 = string.split("\\s");
        for (String s : splitted2) {
            if (!s.isEmpty() && s.matches(POPULATION_REGEX)) {
                if (s.contains(POPULATION_SIGN)) {
                    s = s.substring(0, s.indexOf(POPULATION_SIGN));
                }

                population = Integer.valueOf(s);

                break;
            }
        }
    }

    void parseArmy(String string) {
        String[] splitted2 = string.split("\\s");
        for (String s : splitted2) {
            if (!s.isEmpty() && s.matches(ARMY_REGEX)) {
                if (s.contains(ARMY_SIGN)) {
                    s = s.substring(0, s.indexOf(ARMY_SIGN));
                }

                army = Integer.valueOf(s);

                break;
            }
        }
    }

    void parseGold(String string) {
        String[] splitted2 = string.split("\\s");
        for (String s : splitted2) {
            if (!s.isEmpty() && s.matches(GOLD_REGEX)) {
                if (s.contains(GOLD_SIGN)) {
                    s = s.substring(0, s.indexOf(GOLD_SIGN));
                }

                gold = Integer.valueOf(s);

                break;
            }
        }
    }

    void parseWood(String string) {
        String[] splitted2 = string.split("\\s");
        for (String s : splitted2) {
            if (!s.isEmpty() && s.matches(WOOD_REGEX)) {
                if (s.contains(WOOD_SIGN)) {
                    s = s.substring(0, s.indexOf(WOOD_SIGN));
                }

                wood = Integer.valueOf(s);

                break;
            }
        }
    }

    void parseStone(String string) {
        String[] splitted2 = string.split("\\s");
        for (String s : splitted2) {
            if (!s.isEmpty() && s.matches(STONE_REGEX)) {
                if (s.contains(STONE_SIGN)) {
                    s = s.substring(0, s.indexOf(STONE_SIGN));
                }

                stone = Integer.valueOf(s);

                break;
            }
        }
    }

    void parseFood(String string) {
        String[] splitted2 = string.split("\\s");
        for (String s : splitted2) {
            if (!s.isEmpty() && s.matches(FOOD_REGEX)) {
                if (s.contains(FOOD_SIGN)) {
                    s = s.substring(0, s.indexOf(FOOD_SIGN));
                }

                food = Integer.valueOf(s);

                break;
            }
        }
    }

    public void refreshUpgradeList() {
        nextBuildingToUpgrade = -1;

        int stockCapacity = getStockCapacity(stockLevel);

        switch (Settings.getBuildingScenario()) {
            case 1:
                processUpgradeScenario1(stockCapacity);
                break;
            default:
                processUpgradeScenario0(stockCapacity);
        }
    }

    protected void processUpgradeScenario0(int stockCapacity) {
        int goldKoef = getGoldKoef(Util.HOUSE);
        int woodKoef = getWoodKoef(Util.HOUSE);
        int stoneKoef = getStoneKoef(Util.HOUSE);

        int goldRequiredForHouse = getRequiredAmount(houseLevel, goldKoef);
        int woodRequiredForHouse = getRequiredAmount(houseLevel, woodKoef);
        int stoneRequiredForHouse = getRequiredAmount(houseLevel, stoneKoef);

        goldKoef = getGoldKoef(Util.TOWN);
        woodKoef = getWoodKoef(Util.TOWN);
        stoneKoef = getStoneKoef(Util.TOWN);
        int goldRequiredForTown = getRequiredAmount(townLevel, goldKoef);
        int woodRequiredForTown = getRequiredAmount(townLevel, woodKoef);
        int stoneRequiredForTown = getRequiredAmount(townLevel, stoneKoef);

        if ((woodRequiredForHouse > stockCapacity || stoneRequiredForHouse > stockCapacity) &&
                (woodRequiredForTown > stockCapacity || stoneRequiredForTown > stockCapacity)) {
            nextBuildingToUpgrade = Util.STOCK;
        } else {
            if (woodRequiredForTown < stockCapacity && stoneRequiredForTown < stockCapacity) {
                nextBuildingToUpgrade = Util.TOWN;
            } else {
                nextBuildingToUpgrade = Util.HOUSE;
            }
        }

        int goldRequired;
        int woodRequired;
        int stoneRequired;

        if (nextBuildingToUpgrade == Util.STOCK) {
            goldKoef = getGoldKoef(Util.STOCK);
            woodKoef = getWoodKoef(Util.STOCK);
            stoneKoef = getStoneKoef(Util.STOCK);

            goldRequired = getRequiredAmount(stockLevel, goldKoef);
            woodRequired = getRequiredAmount(stockLevel, woodKoef);
            stoneRequired = getRequiredAmount(stockLevel, stoneKoef);
        } else if (nextBuildingToUpgrade == Util.TOWN) {
            goldRequired = goldRequiredForTown;
            woodRequired = woodRequiredForTown;
            stoneRequired = stoneRequiredForTown;
        } else {
            goldRequired = goldRequiredForHouse;
            woodRequired = woodRequiredForHouse;
            stoneRequired = stoneRequiredForHouse;
        }

        goldRequired -= gold;
        woodRequired -= wood;
        stoneRequired -= stone;

        if (woodRequired <= 0 && stoneRequired <= 0 && goldRequired <= 0) {
            timeToWait = 0;
        } else {
            if (woodRequired <= 0 && stoneRequired <= 0) {
                timeToWait = (goldRequired / goldPerMinute + 1) * 1000 * 60;
            } else {
                if (woodRequired > 0) {
                    goldRequired += woodRequired * 2;
                }

                if (stoneRequired > 0) {
                    goldRequired += stoneRequired * 2;
                }

                if (gold > Settings.getGoldToChange()) {
                    timeToWait = -1;
                } else {
                    timeToWait = (Math.min(goldRequired, Settings.getGoldToChange()) / goldPerMinute + 1) * 1000 * 60;
                }
            }
        }

        int foodLeftTime = foodPerMinute >= 0 ? Integer.MAX_VALUE : (food / -foodPerMinute) * 1000 * 60;

        if (timeToWait > foodLeftTime || foodLeftTime < 60 * 1000 * 60) {
            this.foodRequired = (int) (stockCapacity * 0.8 - food);
            if (foodRequired <= 0) {
                foodRequired = stockCapacity - food;
            }

            if (gold < Settings.getGoldToChange()) {
                timeToWait = ((Settings.getGoldToChange() - gold) / goldPerMinute + 1) * 1000 * 60;
            } else {
                timeToWait = -1;
            }
        } else {
            this.foodRequired = 0;
        }

        this.goldRequired = goldRequired;
        this.woodRequired = woodRequired;
        this.stoneRequired = stoneRequired;
    }

    protected void processUpgradeScenario1(int stockCapacity) {
        int goldKoef = getGoldKoef(Util.BARRACKS);
        int woodKoef = getWoodKoef(Util.BARRACKS);
        int stoneKoef = getStoneKoef(Util.BARRACKS);

        int goldRequiredForBarracks = getRequiredAmount(houseLevel, goldKoef);
        int woodRequiredForBarracks = getRequiredAmount(houseLevel, woodKoef);
        int stoneRequiredForBarracks = getRequiredAmount(houseLevel, stoneKoef);

        if (woodRequiredForBarracks > stockCapacity || stoneRequiredForBarracks > stockCapacity) {
            nextBuildingToUpgrade = Util.STOCK;
        } else {
            nextBuildingToUpgrade = Util.BARRACKS;
        }

        int goldRequired;
        int woodRequired;
        int stoneRequired;

        if (nextBuildingToUpgrade == Util.STOCK) {
            goldKoef = getGoldKoef(Util.STOCK);
            woodKoef = getWoodKoef(Util.STOCK);
            stoneKoef = getStoneKoef(Util.STOCK);

            goldRequired = getRequiredAmount(stockLevel, goldKoef);
            woodRequired = getRequiredAmount(stockLevel, woodKoef);
            stoneRequired = getRequiredAmount(stockLevel, stoneKoef);
        } else {
            goldRequired = goldRequiredForBarracks;
            woodRequired = woodRequiredForBarracks;
            stoneRequired = stoneRequiredForBarracks;
        }

        goldRequired -= gold;
        woodRequired -= wood;
        stoneRequired -= stone;

        if (woodRequired <= 0 && stoneRequired <= 0 && goldRequired <= 0) {
            timeToWait = 0;
        } else {
            if (woodRequired <= 0 && stoneRequired <= 0) {
                timeToWait = (goldRequired / goldPerMinute + 1) * 1000 * 60;
            } else {
                if (woodRequired > 0) {
                    goldRequired += woodRequired * 2;
                }

                if (stoneRequired > 0) {
                    goldRequired += stoneRequired * 2;
                }

                if (gold > Settings.getGoldToChange()) {
                    timeToWait = -1;
                } else {
                    timeToWait = (Math.min(goldRequired, Settings.getGoldToChange()) / goldPerMinute + 1) * 1000 * 60;
                }
            }
        }

        int foodLeftTime = foodPerMinute >= 0 ? Integer.MAX_VALUE : (food / -foodPerMinute) * 1000 * 60;

        if (timeToWait > foodLeftTime || foodLeftTime < 60 * 1000 * 60) {
            this.foodRequired = (int) (stockCapacity * 0.8 - food);
            if (foodRequired <= 0) {
                foodRequired = stockCapacity - food;
            }

            if (gold < Settings.getGoldToChange()) {
                timeToWait = ((Settings.getGoldToChange() - gold) / goldPerMinute + 1) * 1000 * 60;
            } else {
                timeToWait = -1;
            }
        } else {
            this.foodRequired = 0;
        }

        this.goldRequired = goldRequired;
        this.woodRequired = woodRequired;
        this.stoneRequired = stoneRequired;
    }
}
