package net.loyalnetwork.survive.persistance.database;

import net.loyalnetwork.survive.config.DatabaseConfig;
import net.loyalnetwork.survive.persistance.entity.MatchEntity;
import net.loyalnetwork.survive.persistance.entity.MatchPlayerEntity;
import net.loyalnetwork.survive.persistance.entity.PlayerStatsEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseManager {

    private static SessionFactory sessionFactory;
    private final Logger logger;

    public DatabaseManager(Logger logger) {
        this.logger = logger;
    }

    public void connect() {
        try {
            Properties props = buildProperties();

            Configuration config = new Configuration()
                    .addProperties(props)
                    .addAnnotatedClass(PlayerStatsEntity.class)
                    .addAnnotatedClass(MatchEntity.class)
                    .addAnnotatedClass(MatchPlayerEntity.class);

            sessionFactory = config.buildSessionFactory();
            logger.info("Banco de dados conectado com sucesso.");
        } catch (Exception e) {
            logger.severe("Falha ao conectar ao banco de dados: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
            logger.info("Conexão com banco de dados encerrada.");
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || !sessionFactory.isOpen()) {
            throw new IllegalStateException("SessionFactory não inicializado. Chame connect() primeiro.");
        }
        return sessionFactory;
    }

    private Properties buildProperties() {
        String url = String.format(
                "jdbc:postgresql://%s:%d/%s",
                DatabaseConfig.HOST.get(),
                DatabaseConfig.PORT.get(),
                DatabaseConfig.DATABASE.get()
        );

        Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.connection.url",          url);
        props.setProperty("hibernate.connection.username",     DatabaseConfig.USERNAME.get());
        props.setProperty("hibernate.connection.password",     DatabaseConfig.PASSWORD.get());
        props.setProperty("hibernate.dialect",                 "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.connection.pool_size",    String.valueOf(DatabaseConfig.POOL_SIZE.get()));
        props.setProperty("hibernate.hbm2ddl.auto",           DatabaseConfig.HBM2DDL.get());
        props.setProperty("hibernate.show_sql",               String.valueOf(DatabaseConfig.SHOW_SQL.get()));
        props.setProperty("hibernate.format_sql",             "false");

        props.setProperty("hibernate.jdbc.batch_size",        "50");
        props.setProperty("hibernate.order_inserts",          "true");
        props.setProperty("hibernate.order_updates",          "true");
        props.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        return props;
    }
}