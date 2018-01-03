package bot.bs.scenarios;

import bot.bs.Settings;
import bot.bs.Util;
import bot.bs.handler.BSMessageHandler;
import org.telegram.api.keyboard.button.TLKeyboardButton;
import org.telegram.api.keyboard.replymarkup.TLReplayKeyboardMarkup;
import org.telegram.api.message.TLMessage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author SeniorJD
 */
public class SnowballThrower {
    private static final long MINUTE_IN_MILLIS = 1000 * 60;
    private static final long TEN_SEC_IN_MILLIS = 1000 * 10;
    private static final long FIVE_SEC_IN_MILLIS = 1000 * 5;

    private static final String THROW_SNOWBALL_BUTTON_TEXT = "Швырнуть снежок в ";
    private static final String ZERO_SYMBOL = ""+'\u200B';

    private BSMessageHandler messageHandler;

    private Timer timer;

    private long lastSnowballThrow;
    private boolean isThrowingSnowball;
    private boolean snowballThrown;
    private RunningScenario lastScenario;

    public SnowballThrower(BSMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        lastSnowballThrow = System.currentTimeMillis() - MINUTE_IN_MILLIS + TEN_SEC_IN_MILLIS;
    }

    private BSSender getSender() {
        return messageHandler.getSender();
    }

    public void refresh() {
        lastSnowballThrow = System.currentTimeMillis() - MINUTE_IN_MILLIS + TEN_SEC_IN_MILLIS;
        isThrowingSnowball = false;
        snowballThrown = false;
        lastScenario = null;

        createTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (canThrowSnowball()) {
                    throwSnowball();
                }
            }
        }, TEN_SEC_IN_MILLIS);
    }

    public void stop() {
        cancelTimer();
        isThrowingSnowball = false;
        snowballThrown = false;
        lastScenario = null;
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void createTimer() {
        cancelTimer();
        timer = new Timer();
    }

    public boolean canThrowSnowball() {
        return Settings.isPlaySnowballs() && (System.currentTimeMillis() - MINUTE_IN_MILLIS) >= lastSnowballThrow;
    }

    public boolean isThrowingSnowball() {
        return isThrowingSnowball;
    }

    public void throwSnowball() {
        isThrowingSnowball = true;
        snowballThrown = false;
        if (lastScenario != null) {
            if (messageHandler.getRunningScenario() != null) {
                lastScenario = messageHandler.getRunningScenario();
                lastScenario.stop();
                messageHandler.setRunningScenario(null);
            }
        } else {
            lastScenario = messageHandler.getRunningScenario();
            if (lastScenario != null) {
                lastScenario.stop();
            }
            messageHandler.setRunningScenario(null);
        }


        createTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getSender().sendMessage(Util.CONTROL_UP);
            }
        }, FIVE_SEC_IN_MILLIS);
    }

    public void processThrowingSnowballMessage(TLMessage message) {
        cancelTimer();
        if (!snowballThrown) {
            if (!canThrowSnowballAt(message)) {
                getSender().sendMessage(Util.CONTROL_UP);
                return;
            }

            getSender().pressThrowSnowballButton(message);
            snowballThrown = true;
        } else {
            if (!message.getMessage().startsWith("❄")) {
                createTimer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        snowballThrown = false;
                        isThrowingSnowball = false;
                        throwSnowball();
                    }
                }, MINUTE_IN_MILLIS);
                return;
            }

            if (message.getMessage().contains("злиться")) {
                lastSnowballThrow = System.currentTimeMillis() - MINUTE_IN_MILLIS + TEN_SEC_IN_MILLIS;
                snowballThrown = false;
                createTimer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!canThrowSnowballAt(message)) {
                            getSender().sendMessage(Util.CONTROL_UP);
                            return;
                        }

                        getSender().pressThrowSnowballButton(message);
                        snowballThrown = true;
                    }
                }, TEN_SEC_IN_MILLIS);
            } else {
                createTimer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (canThrowSnowball() && !isThrowingSnowball()) {
                            throwSnowball();
                        }
                    }
                }, MINUTE_IN_MILLIS);

                RunningScenario scenario = createScenario();

                lastSnowballThrow = System.currentTimeMillis();
                snowballThrown = false;
                isThrowingSnowball = false;
                lastScenario = null;

                if (scenario != null) {
                    messageHandler.setRunningScenario(scenario);
                    scenario.start();
                }
            }
        }
    }

    private RunningScenario createScenario() {
        if (lastScenario == null) {
            return null;
        }
        if (lastScenario instanceof BuildingScenario) {
            return new BuildingScenario(messageHandler);
        }
        if (lastScenario instanceof FindingScenario) {
            return new FindingScenario(messageHandler);
        }
        if (lastScenario instanceof RecoverScenario) {
            return new RecoverScenario(messageHandler);
        }
        return null;
    }

    private boolean canThrowSnowballAt(TLMessage message) {
        String playerName = retrievePlayer(message);

        if (playerName == null) {
            return false;
        }

        String noSnowballsFor = Settings.getNoSnowballsFor();
        if (noSnowballsFor == null || noSnowballsFor.isEmpty()) {
            return true;
        }

        String[] array = noSnowballsFor.split(";");

        for (String s : array) {
            if (s.equals(playerName)) {
                return false;
            }
        }

        return true;
    }

    private String retrievePlayer(TLMessage message) {
        try {
            TLReplayKeyboardMarkup replyMarkup = (TLReplayKeyboardMarkup) message.getReplyMarkup();
            TLKeyboardButton button = (TLKeyboardButton) replyMarkup.getRows().get(2).buttons.get(0);

            if (!button.getText().startsWith("☃")) {
                return null;
            }

            String result = button.getText().substring(button.getText().indexOf(THROW_SNOWBALL_BUTTON_TEXT) + THROW_SNOWBALL_BUTTON_TEXT.length());
            if (result.startsWith(ZERO_SYMBOL)) {
                result = result.substring(1);
            }
            return result;
        } catch (Throwable t) {
            return null;
        }
    }
}
