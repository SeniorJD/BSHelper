package bot;

import bot.bs.Helper;
import bot.bs.Settings;
import bot.bs.handler.*;
import bot.bs.scenarios.BSSender;
import bot.plugins.BotConfigImpl;
import bot.plugins.handlers.TLMessageHandler;
import bot.plugins.structure.ChatUpdatesBuilderImpl;
import bot.plugins.structure.CustomUpdatesHandler;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;

import java.io.*;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author SeniorJD
 */
public class Main {
    private static int APIKEY = -1; // your api key
    private static String APIHASH; // your api hash
    private static String PHONENUMBER; // Your phone number

    public static void main(String[] args) {
        Logger.getGlobal().addHandler(new ConsoleHandler());
        Logger.getGlobal().setLevel(Level.ALL);

        readSettings();
        Settings.readSettings();

//        final DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();
        final BotConfig botConfig = new BotConfigImpl(PHONENUMBER);

        BSDatabaseManager databaseManager = new BSDatabaseManager();

        final IUsersHandler usersHandler = new BSUsersHandler(databaseManager);
        final IChatsHandler chatsHandler = new BSChatsHandler(databaseManager);
        final BSMessageHandler messageHandler = new BSMessageHandler();
        final TLMessageHandler tlMessageHandler = new TLMessageHandlerExt(messageHandler);

        final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(CustomUpdatesHandler.class);
        builder.setBotConfig(botConfig)
                .setDatabaseManager(databaseManager)
                .setUsersHandler(usersHandler)
                .setChatsHandler(chatsHandler)
                .setMessageHandler(messageHandler)
                .setTlMessageHandler(tlMessageHandler);

        try {
            final TelegramBot kernel = new TelegramBot(botConfig, builder, APIKEY, APIHASH);
            LoginStatus status = kernel.init();
//            messageHandler.setKernelComm(kernel.getKernelComm());
            if (status == LoginStatus.CODESENT) {
                System.out.println("enter message sent from telegram");
                Scanner in = new Scanner(System.in);
                String s = in.nextLine().trim();
                System.out.println("message accepted: " + s + ". Sending...");
                boolean success = kernel.getKernelAuth().setAuthCode(s);
                if (success) {
                    status = LoginStatus.ALREADYLOGGED;
                }
            }
            if (status == LoginStatus.ALREADYLOGGED) {
                kernel.startBot();
                BSSender sender = new BSSender(messageHandler, kernel.getKernelComm());
                messageHandler.setSender(sender);

                if (new File("settings.bs").exists()) {
                    Settings.printSettings(sender);
                } else {
                    sender.sendHelperMessage(Helper.RESPONSE_HELP);
                }
            } else {
                throw new Exception("Failed to log in: " + status);
            }
        } catch (Exception e) {
            BotLogger.severe("MAIN", e);
        }
    }

    private static void readSettings() {
        File file = new File("auth_settings.bs");
        if (!file.exists()) {
            System.out.println("enter your phone number");
            Scanner in = new Scanner(System.in);
            String phoneNumber = in.nextLine().trim();
            PHONENUMBER = phoneNumber;

            System.out.println("enter API key");
            while (APIKEY == -1) {
                String apiKeyS = in.nextLine().trim();
                try {
                    int apiKey = Integer.valueOf(apiKeyS);
                    APIKEY = apiKey;
                } catch (NumberFormatException e){
                    System.out.println("wrong API key, try again:");
                }
            }


            System.out.println("enter API hash");
            String apiHash = in.nextLine().trim();
            APIHASH = apiHash;

            try {
                file.createNewFile();

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(PHONENUMBER);
                    writer.write("\n");
                    writer.write(String.valueOf(APIKEY));
                    writer.write("\n");
                    writer.write(APIHASH);
                    writer.write("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            boolean error = false;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (PHONENUMBER == null) {
                        PHONENUMBER = line;
                    } else if (APIKEY == -1) {
                        try {
                            APIKEY = Integer.valueOf(line);
                        } catch (NumberFormatException e) {
                            error = true;
                            return;
                        }
                    } else if (APIHASH == null) {
                        APIHASH = line;
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (error) {
                    file.delete();
                    readSettings();
                }
            }

        }
    }
}
