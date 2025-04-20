package fr.sorway.soguard.configuration.data;

public record DatabaseConfiguration(String host, int port, String database, String username, String password) {
}
