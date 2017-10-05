package bot.plugins.structure;

import org.telegram.bot.structure.Chat;

/**
 * @author SeniorJD
 */
public class ChatImpl implements Chat {
    private int id;
    private Long accessHash;
    private boolean isChannel;

    public ChatImpl(int id) {
        this.id = id;
    }

    public ChatImpl() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Long getAccessHash() {
        return accessHash;
    }

    @Override
    public boolean isChannel() {
        return isChannel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccessHash(Long accessHash) {
        this.accessHash = accessHash;
    }

    public void setChannel(boolean channel) {
        isChannel = channel;
    }
}
