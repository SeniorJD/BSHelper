package bot.bs.scenarios;

import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;

/**
 * @author SeniorJD
 */
public interface RunningScenario {
    void start();
    void stop();
    void handleMessage(TLMessage tlMessage);
    void handleMessage(TLUpdateShortMessage tlUpdateShortMessage);
}
