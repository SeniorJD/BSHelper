package bot.bs.player;

import java.util.Objects;

/**
 * @author SeniorJD
 */
public class Battle {
    String name;
    boolean won;
    int goldPerUnit;

    public Battle(String name, boolean won, int goldPerUnit) {
        this.name = name;
        this.won = won;
        this.goldPerUnit = goldPerUnit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Battle)) {
            return false;
        }
        Battle b2 = (Battle) obj;

        if (!Objects.equals(name, b2.name)) {
            return false;
        }

        if (!Objects.equals(won, b2.won)) {
            return false;
        }

        if (!Objects.equals(goldPerUnit, b2.goldPerUnit)) {
            return false;
        }

        return true;
    }
}
