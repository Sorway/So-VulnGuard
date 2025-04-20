package fr.sorway.soguard;

import com.google.gson.Gson;
import fr.sorway.soguard.bot.DiscordAPIManager;
import fr.sorway.soguard.configuration.Configuration;
import fr.sorway.soguard.services.VulnerabilityService;
import fr.sorway.soguard.services.commands.CommandService;
import fr.sorway.soguard.services.database.DatabaseService;
import fr.sorway.soguard.task.SearchCVETask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SoVulnGuard {
    private final Logger logger = LoggerFactory.getLogger(SoVulnGuard.class);
    private final Gson gson = new Gson();
    private final DiscordAPIManager discordAPIManager = new DiscordAPIManager(this);
    private Configuration configuration;
    private DatabaseService databaseService;
    private CommandService commandService;
    private VulnerabilityService vulnerabilityService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        new SoVulnGuard().init(args);
    }

    private void init(String[] args) {
        logger.info("Starting preparations of the bot...");
        long loadStart = System.nanoTime();

        initConfiguration();

        databaseService = new DatabaseService(configuration);
        databaseService.init();

        commandService = new CommandService(this);

        vulnerabilityService = new VulnerabilityService(this);

        logger.info("Creating JDA instance.");
        discordAPIManager.createBot();

        long loadDuration = ((System.nanoTime() - loadStart) / 1000000);
        logger.info("Started in {} second(s)", loadDuration / 1000);
    }

    private void initConfiguration() {
        logger.info("Loading bot configuration...");

        try (FileReader reader = new FileReader(new File(new File(SoVulnGuard.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent(), "configuration.json"))) {
            configuration = gson.fromJson(reader, Configuration.class);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public DiscordAPIManager getDiscordAPIManager() {
        return discordAPIManager;
    }

    public Gson getGson() {
        return gson;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public CommandService getCommandService() {
        return commandService;
    }

    public VulnerabilityService getVulnerabilityService() {
        return vulnerabilityService;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }
}
