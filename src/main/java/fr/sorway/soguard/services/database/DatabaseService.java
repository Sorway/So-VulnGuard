package fr.sorway.soguard.services.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.sorway.soguard.SoVulnGuard;
import fr.sorway.soguard.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class DatabaseService {
    private final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private final Configuration configuration;
    private HikariDataSource dataSource;

    public DatabaseService(Configuration configuration) {
        this.configuration = configuration;
    }

    public void init() {
        logger.info("Connecting to database...");

        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(20);
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(String.format("jdbc:mariadb://%s:%s/%s", configuration.databaseConfiguration().host(), configuration.databaseConfiguration().port(), configuration.databaseConfiguration().database()));
        config.setUsername(configuration.databaseConfiguration().username());
        config.setPassword(configuration.databaseConfiguration().password());
        config.setMaxLifetime(300000);
        config.setConnectionTimeout(120000);
        config.setLeakDetectionThreshold(60 * 5000);
        this.dataSource = new HikariDataSource(config);

        this.initTables();
        logger.info("Successfully connected to the database...");
    }

    public Connection getConnection() throws SQLException {
        if (this.dataSource == null || this.dataSource.isClosed())
            init();
        return dataSource.getConnection();
    }

    public void initTables() {
        this.logger.info("Building tables in database...");

        try (InputStream inputStream = SoVulnGuard.class.getResourceAsStream("/sql/requests.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
             Connection connection = this.dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);

                if (line.endsWith(";")) {
                    String sql = stringBuilder.toString();
                    statement.execute(sql);
                    stringBuilder = new StringBuilder();
                }
            }

            statement.getConnection().close();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
