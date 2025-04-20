package fr.sorway.soguard.events;

import fr.sorway.soguard.SoVulnGuard;
import fr.sorway.soguard.task.SearchCVETask;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ReadyListener extends ListenerAdapter {
    private final SoVulnGuard instance;

    public ReadyListener(SoVulnGuard instance) {
        this.instance = instance;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        instance.getCommandService().initSlashCommand(event.getJDA());
        instance.getLogger().info("\uD83E\uDD16 Connected as {}", event.getJDA().getSelfUser().getName());

        instance.getScheduler().scheduleAtFixedRate(new SearchCVETask(instance), 0, 15, TimeUnit.MINUTES);
        instance.getScheduler().scheduleAtFixedRate(() -> instance.getDiscordAPIManager().updateActivities(), 0, 5, TimeUnit.MINUTES);
    }
}
