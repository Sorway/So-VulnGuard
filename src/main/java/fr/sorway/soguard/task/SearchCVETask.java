package fr.sorway.soguard.task;

import fr.sorway.soguard.SoVulnGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchCVETask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(SearchCVETask.class);
    private final SoVulnGuard instance;

    public SearchCVETask(SoVulnGuard instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        instance.getVulnerabilityService().searchVulnerabilities();
    }
}
