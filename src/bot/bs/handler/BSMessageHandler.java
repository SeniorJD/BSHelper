package bot.bs.handler;

import bot.bs.BSMediator;
import bot.bs.Helper;
import bot.bs.Settings;
import bot.bs.player.Battles;
import bot.bs.scenarios.*;
import bot.plugins.handlers.MessageHandler;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import static bot.bs.Helper.COMMAND_RECOVER;
import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class BSMessageHandler extends MessageHandler {

    boolean ignoreHelperMessage = false;
    private boolean started = false;
    private BSMediator mediator = BSMediator.getInstance();
    private BSSender sender;

    private RunningScenario runningScenario;

    public BSMessageHandler() { }

    public void setSender(BSSender sender) {
        this.sender = sender;
    }

    public void setIgnoreHelperMessage(boolean ignoreHelperMessage) {
        this.ignoreHelperMessage = ignoreHelperMessage;
    }

    private void sendHelperMessage(String message) {
        ignoreHelperMessage = true;
        sender.sendHelperMessage(message);
    }

    public void handleMessage(int chatId, @NotNull TLMessage tlMessage) {
        Chat helperChat = getSender().getHelperChat();

        if (helperChat == null) {
            System.out.println("helper chat is null");
            return;
        }

        if (chatId != helperChat.getId()) {
            return;
        }

        if (tlMessage.getMessage().startsWith("exit")) {
            System.exit(0);
            return;
        }

        if (ignoreHelperMessage) {
            ignoreHelperMessage = false;
            return;
        }

        String message = tlMessage.getMessage().toLowerCase();

        if (message.startsWith(Helper.COMMAND_STOP)) {
            if (runningScenario != null) {
                try {
                    runningScenario.stop();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                runningScenario = null;
            }
            started = false;
            return;
        }

        if (message.startsWith(Helper.COMMAND_SETGOLD)) {
            String[] parsed = message.split("\\D");

            int value = 100000;
            for (String s : parsed) {
                if (s.isEmpty()) {
                    continue;
                }

                try {
                    value = Integer.parseInt(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                if (value <= 0) {
                    return;
                }

                Settings.setGoldToChange(value);
                break;
            }

            getSender().sendHelperMessage("new gold value: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_AUTOATTACK)) {
            String[] parsed = message.split("\\s");

            boolean value = true;
            for (String s : parsed) {
                if (s.isEmpty() || s.equals(Helper.COMMAND_AUTOATTACK)) {
                    continue;
                }

                try {
                    value = Boolean.parseBoolean(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                Settings.setAutoAttack(value);
                break;
            }

            getSender().sendHelperMessage("auto attack: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_AUTOSEARCH)) {
            String[] parsed = message.split("\\s");

            boolean value = true;
            for (String s : parsed) {
                if (s.isEmpty() || s.equals(Helper.COMMAND_AUTOSEARCH)) {
                    continue;
                }

                try {
                    value = Boolean.parseBoolean(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                Settings.setAutoSearch(value);
                break;
            }

            getSender().sendHelperMessage("auto search: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_AUTOBUILD)) {
            String[] parsed = message.split("\\s");

            boolean value = true;
            for (String s : parsed) {
                if (s.isEmpty() || s.equals(Helper.COMMAND_AUTOBUILD)) {
                    continue;
                }

                try {
                    value = Boolean.parseBoolean(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                Settings.setAutoBuild(value);
                break;
            }

            getSender().sendHelperMessage("auto build: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_ADD_ALLY_ALLIANCE)) {
            if (message.equals(Helper.COMMAND_ADD_ALLY_ALLIANCE)) {
                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_ADD_ALLY_ALLIANCE.length() + 1);
                Settings.addAllyAlliance(alliance);

                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_REMOVE_ALLY_ALLIANCE)) {
            if (message.equals(Helper.COMMAND_REMOVE_ALLY_ALLIANCE)) {
                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_REMOVE_ALLY_ALLIANCE.length() + 1);
                Settings.removeAllyAlliance(alliance);

                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_ADD_ALLY_PLAYER)) {
            if (message.equals(Helper.COMMAND_ADD_ALLY_PLAYER)) {
                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_ADD_ALLY_PLAYER.length() + 1);
                Settings.addAllyPlayer(alliance);

                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_REMOVE_ALLY_PLAYER)) {
            if (message.equals(Helper.COMMAND_REMOVE_ALLY_PLAYER)) {
                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_REMOVE_ALLY_PLAYER.length() + 1);
                Settings.removeAllyPlayer(alliance);

                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_BUILDING_SCENARIO)) {
            String scenario = message.substring(Helper.COMMAND_BUILDING_SCENARIO.length() + 1);
            String[] parsed = scenario.split("\\D+");

            int value = -1;
            for (String s : parsed) {
                if (s.isEmpty()) {
                    continue;
                }

                try {
                    value = Integer.parseInt(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                    value = -1;
                }

                if (value < 0 || value > 7) {
                    getSender().sendHelperMessage("only values 0-7 are OK");
                    return;
                }
            }

            Settings.setBuildingScenario(scenario);

            getSender().sendHelperMessage("building scenario: " + scenario);
            return;
        } else if (message.startsWith(Helper.COMMAND_RISKY_ATTACK)) {
            String valueS = message.substring(Helper.COMMAND_RISKY_ATTACK.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            Settings.setRiskyAttack(value);

            getSender().sendHelperMessage("risky attack " + value);

            return;
        } else if (message.startsWith(Helper.COMMAND_MAX_SEARCH)) {
            String valueS = message.substring(Helper.COMMAND_MAX_SEARCH.length() + 1);
            int value = 100;
            try {
                value = Integer.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            Settings.setMaxSearch(value);

            getSender().sendHelperMessage("max search " + value);

            return;
        } else if (message.startsWith(Helper.COMMAND_OPPONENT)) {
            String task = tlMessage.getMessage().substring(Helper.COMMAND_OPPONENT.length());
            if (!task.isEmpty()) {
                if (task.startsWith(" ")) {
                    task = task.substring(1);
                }
            }

            Settings.setOpponent(task);
            if (task.isEmpty()) {
                getSender().sendHelperMessage("next opponent could be any weak player");
            } else {
                getSender().sendHelperMessage("next opponent: " + task);
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_SEARCH_APPROPRIATE)) {
            String valueS = message.substring(Helper.COMMAND_SEARCH_APPROPRIATE.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            Settings.setSearchAppropriate(value);

            getSender().sendHelperMessage("search appropriate " + value);

            return;
        }

        if (runningScenario != null) {
            try {
                runningScenario.stop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            runningScenario = null;
//            return;
        }

        if (message.startsWith(Helper.COMMAND_START)) {
            started = true;

            Settings.printSettings(sender);
            return;
        }

        if (!started) {
            sendHelperMessage("use start command in order to start the bot");
            return;
        }

        if (message.startsWith(Helper.COMMAND_FIND)) {
            runningScenario = new FindingScenario(this);
            runningScenario.start();
            return;
        } else if (message.startsWith(Helper.COMMAND_BUILD)) {
            if (mediator.inBattle) {
                sendHelperMessage("Cannot build while the battle");
                return;
            }

            runningScenario = new BuildingScenario(this);
            runningScenario.start();
            return;
        } else if (message.startsWith(COMMAND_RECOVER)) {
            runningScenario = new RecoverScenario(this);
            runningScenario.start();
            return;
        } else if (message.startsWith(Helper.COMMAND_HELP)) {
            sendHelperMessage(Helper.RESPONSE_HELP);
        }
    }

    @Override
    public void handleMessage(@NotNull TLUpdateShortMessage message) {
        if (!started) {
            return;
        }
        IUser bsBot = sender.getUser();

        if (bsBot == null) {
            return;
        }

        if (message.getUserId() != bsBot.getUserId()) {
            return;
        }

        if (shouldIgnore(message.getMessage())) {
            if (shouldRecover(message.getMessage())) {
                Battles.getInstance().addBattle(message.getMessage());
                runningScenario = new RecoverScenario(this);
                runningScenario.start();
            }
            return;
        }

        if (runningScenario != null) {
            runningScenario.handleMessage(message);
            return;
        }

        handleMessage(message.getMessage());
    }

    private boolean shouldRecover(String message) {
        return message.contains(BATTLE_FINISHED);
    }

    @Override
    public void handleMessage(@NotNull TLMessage message) {
        if (!started) {
            return;
        }
        IUser bsBot = sender.getUser();

        if (bsBot == null) {
            return;
        }

        if (message.getFromId() != bsBot.getUserId()) {
            return;
        }

        if (shouldIgnore(message.getMessage())) {
            if (shouldRecover(message.getMessage())) {
                Battles.getInstance().addBattle(message.getMessage());
                if (runningScenario != null) {
                    runningScenario.stop();
                }

                runningScenario = new RecoverScenario(this);
                runningScenario.start();
            }
            return;
        }

        if (runningScenario != null) {
            try {
                runningScenario.handleMessage(message);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        }

        handleMessage(message.getMessage());
    }

    protected boolean shouldIgnore(String message) {
        if (message.contains(ALLY_ATTACKS)) {
            return true;
        } else if (message.contains(ALLY_ATTACKS2)) {
            return true;
        } else if (message.contains(ALLY_ATTACKED)) {
            return true;
        } else if (message.contains(DEFENCE_STARTED)) {
            mediator.inBattle = true;
            return true;
        } else if (message.contains(ATTACK_STARTED)) {
            mediator.inBattle = true;
            return true;
        } else if (message.contains(YOU_WIN)) {
            mediator.inBattle = false;
            return true;
        } else if (message.contains(YOU_LOST)) {
            mediator.inBattle = false;
            return true;
        } else if (message.contains(BATTLE_FINISHED)) {
            mediator.inBattle = false;
            return true;
        } else if (message.contains(BATTLE_UNFINISHED)) {
            mediator.inBattle = true;
            return true;
        } else if (message.contains(HINT)) {
            return true;
        }

        return false;
    }

    protected void handleMessage(@NotNull String message) {
    }

    public void setRunningScenario(RunningScenario runningScenario) {
        this.runningScenario = runningScenario;
    }

    public RunningScenario getRunningScenario() {
        return runningScenario;
    }

    public BSSender getSender() {
        return sender;
    }
}
