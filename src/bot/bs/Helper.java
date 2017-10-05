package bot.bs;

/**
 * @author SeniorJD
 */
public class Helper {
    public static final String COMMAND_FIND = "find";
    public static final String COMMAND_BUILD = "build";
    public static final String COMMAND_STOP = "stop";
    public static final String COMMAND_HELP = "help";
    public static final String COMMAND_SETGOLD = "setgold";
    public static final String COMMAND_AUTOATTACK = "autoattack";
    public static final String COMMAND_AUTOSEARCH = "autosearch";
    public static final String COMMAND_AUTOBUILD = "autobuild";
    public static final String COMMAND_RECOVER = "recover";

    public static final String RESPONSE_HELP =
            "find - найти противника по нику, по альянсу, либо по карме (0-1 кармы и 1/2 твоей территории) \n" +
            "build - процесс слива денег, поддержания достаточного количества еды, а также постройки зданий \n" +
            "stop - остановить любой запущенный процесс \n" +
            "setgold X - установить минимальную сумму золота (X), после которой бот будет пытаться от золота избавиться, насколько хватит склада \n" +
            "autoattack true/false - атаковать противника сразу после нахождения \n" +
            "recover - восстановить армию \n" +
            "autosearch true/false - искать противника каждых 10 минут \n" +
            "autobuild true/false - стройка сразу после восстановления армий";

    public static final String EXPLORING_1 = "Разведчики докладывают, что неподалеку расположился ";
    public static final String EXPLORING_2 = " в своих владениях ";
    public static final String EXPLORING_3 = " размером ";
    public static final String EXPLORING_4 = ". За победу ты получишь ";

}
