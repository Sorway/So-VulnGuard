package fr.sorway.soguard.configuration;

import fr.sorway.soguard.configuration.data.DatabaseConfiguration;

public record Configuration(String token, String guild, String channel, DatabaseConfiguration databaseConfiguration) {
}
