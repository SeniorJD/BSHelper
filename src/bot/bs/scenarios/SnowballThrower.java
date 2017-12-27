package bot.bs.scenarios;

import bot.bs.Util;
import bot.bs.handler.BSMessageHandler;
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
        return (System.currentTimeMillis() - MINUTE_IN_MILLIS) >= lastSnowballThrow;
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
        if (!snowballThrown) {
            boolean thrown = getSender().pressThrowSnowballButton(message);
            if (!thrown) {
                throwSnowball();
                return;
            }
            snowballThrown = true;
        } else {
            if (!message.getMessage().startsWith("❄")) {
                createTimer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        snowballThrown = false;
                        isThrowingSnowball = false;
                        lastScenario = null;
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
                        boolean thrown = getSender().pressThrowSnowballButton(message);
                        if (!thrown) {
                            throwSnowball();
                            return;
                        }
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
}
