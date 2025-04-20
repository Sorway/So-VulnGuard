package fr.sorway.soguard.bot;

import fr.sorway.soguard.SoVulnGuard;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;

public class DiscordAPIManager {
    private final Logger logger = LoggerFactory.getLogger(DiscordAPIManager.class);
    private final SoVulnGuard instance;
    private ShardManager shardManager;

    public DiscordAPIManager(SoVulnGuard instance) {
        this.instance = instance;
    }

    public void createBot() {
        this.shardManager = DefaultShardManagerBuilder
                .createDefault(instance.getConfiguration().token())
                .setLargeThreshold(50)
                .setAutoReconnect(true)
                .setShardsTotal(1)
                .enableCache(Arrays.asList(CacheFlag.values()))
                .enableIntents(Arrays.asList(GatewayIntent.values()))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        this.loadEvents();
    }

    private void loadEvents() {
        Reflections reflections = new Reflections("fr.sorway.soguard.events");
        Set<Class<? extends ListenerAdapter>> classes = reflections.getSubTypesOf(ListenerAdapter.class);

        for (Class<? extends ListenerAdapter> aClass : classes) {
            logger.info("Loading listener {}", aClass.getSimpleName());

            try {
                Constructor<? extends ListenerAdapter> constructor = aClass.getDeclaredConstructor(SoVulnGuard.class);

                if (Modifier.isPublic(constructor.getModifiers())) {
                    shardManager.addEventListener(constructor.newInstance(instance));
                } else {
                    logger.error("Constructor is not accessible for {}", aClass.getSimpleName());
                }
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                logger.error("Error loading listener {}: {}", aClass.getSimpleName(), e.getMessage());
            }
        }
    }

    public void updateActivities() {
        this.shardManager.setActivity(Activity.customStatus(String.format("👀 %s Vulnérabilités détectées aujourd'hui", instance.getVulnerabilityService().getVulnerabilitiesCount())));
    }

    public ShardManager getShardManager() {
        return shardManager;
    }
}
