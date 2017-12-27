package bot.bs;

/**
 * @author SeniorJD
 */
public class Helper {
    public static final String COMMAND_FIND = "find";
    public static final String COMMAND_BUILD = "build";
    public static final String COMMAND_STOP = "stop";
    public static final String COMMAND_START = "start";
    public static final String COMMAND_HELP = "help";
    public static final String COMMAND_EXIT = "exit";
    public static final String COMMAND_SETGOLD = "setgold";
    public static final String COMMAND_AUTOATTACK = "autoattack";
    public static final String COMMAND_AUTOSEARCH = "autosearch";
    public static final String COMMAND_AUTOBUILD = "autobuild";
    public static final String COMMAND_RECOVER = "recover";
    public static final String COMMAND_OPPONENT = "opponent";
    public static final String COMMAND_ATTACK_IF_MEET = "attackifmeet";
    public static final String COMMAND_BUILDING_SCENARIO = "building";
    public static final String COMMAND_ADD_ALLY_ALLIANCE = "addallyalliance";
    public static final String COMMAND_REMOVE_ALLY_ALLIANCE = "removeallyalliance";
    public static final String COMMAND_ADD_ALLY_PLAYER = "addallyplayer";
    public static final String COMMAND_REMOVE_ALLY_PLAYER = "removeallyplayer";
    public static final String COMMAND_RISKY_ATTACK = "riskyattack";
    public static final String COMMAND_RISKY_ATTACK_ONLY = "riskyattackonly";
    public static final String COMMAND_SEARCH_APPROPRIATE = "searchappropriate";
    public static final String COMMAND_GIVE_IMMUN = "giveimmun";
    public static final String COMMAND_MAX_SEARCH = "maxsearch";
    public static final String COMMAND_ATTACK_CONQUEROR = "attackconqueror";
    public static final String COMMAND_JOIN_ALLIANCE_BATTLES = "joinalliancebattles";
    public static final String COMMAND_PLAY_SNOWBALLS = "playsnowballs";
    public static final String COMMAND_NO_SNOWBALLS_FOR = "nosnowballsfor";

    public static final String RESPONSE_HELP =
            "/start - старт. нужно юзать после /stop\n" +
            "/find - найти противника по нику, по альянсу, либо по карме (0-1 кармы)\n" +
            "/build - процесс слива денег, поддержания достаточного количества еды, а также постройки зданий\n" +
            "/stop - остановить бота\n" +
            "/recover - восстановить армию\n" +
            "*opponent* - задать имя ИЛИ альянс противника(-ов) для поиска. имя обнулится после атаки. можно задавать несколько имен/аллов через точку с запятой\n" +
            "*attackifmeet* - задать имя ИЛИ альянс противника(-ов). если бот его увидит в поиске, то атакует, не смотря на терру/карму, но специально искать его не будет. правила заполнения такие же как для *opponent*\n" +
            "*setgold X* - установить минимальную сумму золота (X), после которой бот будет пытаться от золота избавиться, насколько хватит склада\n" +
            "*autoattack true/false* - атаковать противника сразу после нахождения\n" +
            "*riskyattack true/false* - атаковать противника с любой кармой если его территория < 4000\n" +
            "*riskyattackonly true/false* - атаковать противников только в режиме *riskyattack*\n" +
            "*giveimmun true/false* - атаковать противника с целью дать ему иммун. атака будет раз в час, если противник в opponents один, дважды в час их двое и т.д. будет восстанавливать только 1 воина\n" +
            "*searchappropriate true/false* - искать среди Подходящих (true) либо по Всем (false)\n" +
            "*maxsearch X* - максимальное кол-во поисков противника по имени/альянсу, затем переключение на дефолтный поиск\n" +
            "*autosearch true/false* - включает режим поиска в цикл работы\n" +
            "*autobuild true/false* - включает режим стройки в цикл работы\n" +
            "*addallyalliance/removeallyalliance X* - добавить/удалить дружественный альянс в список\n" +
            "*addallyplayer/removeallyplayer X* - добавить/удалить дружественного игрока в список\n" +
            "*attackConqueror true/false* - атаковать завоевателя, если попадется (но не искать специально)\n" +
            "*joinAllianceBattles true/false* - присоединяться к альянсовым атакам, если в атаке учавствует кто-либо из *allyPlayers*\n" +
            "*playsnowballs true/false* - играть в снежки (новогодний ивент)\n" +
            "*nosnowballsfor* - не кидать снежками в противников (только НИКИ!). правила заполнения такие же как *opponents* (новогодний ивент)\n" +
            "*building X1 .. Xn* - приоритет стройки. склад ставить только в конец. индексы:\n" +
                    "\t0 - ратуша\n" +
                    "\t1 - склад\n" +
                    "\t2 - дом\n" +
                    "\t3 - ферма\n" +
                    "\t4 - лесопилка\n" +
                    "\t5 - каменоломня\n" +
                    "\t6 - бараки\n" +
                    "\t7 - стена\n" +
                    "\t8 - требушет\n" +
            "/exit - выйти из приложения (закроет консоль)";

    public static final String EXPLORING_1 = "Разведчики докладывают, что неподалеку расположился ";
    public static final String EXPLORING_2 = " в своих владениях ";
    public static final String EXPLORING_3 = " размером ";
    public static final String EXPLORING_4 = ". За победу ты получишь ";
    public static final String CONQUEROR = "\uD83D\uDDE1"; // завоеватель
    public static final String EXPLORING_6 = "\uD83D\uDE08"; // карма < 0

}
