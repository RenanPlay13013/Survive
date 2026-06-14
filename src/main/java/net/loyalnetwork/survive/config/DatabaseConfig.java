package net.loyalnetwork.survive.config;

import net.loyalnetwork.coffeelib.config.annotation.Comment;
import net.loyalnetwork.coffeelib.config.annotation.ConfigFile;
import net.loyalnetwork.coffeelib.config.annotation.Key;
import net.loyalnetwork.coffeelib.config.node.ConfigValue;

@ConfigFile("database.yml")
public final class DatabaseConfig {

    @Key("host")
    @Comment("Endereço do servidor PostgreSQL")
    public static final ConfigValue<String> HOST = ConfigValue.of("localhost");

    @Key("port")
    @Comment("Porta do PostgreSQL (padrão: 5432)")
    public static final ConfigValue<Integer> PORT = ConfigValue.of(5432);

    @Key("database")
    @Comment("Nome do banco de dados")
    public static final ConfigValue<String> DATABASE = ConfigValue.of("survive");

    @Key("username")
    @Comment("Usuário do banco de dados")
    public static final ConfigValue<String> USERNAME = ConfigValue.of("postgres");

    @Key("password")
    @Comment("Senha do banco de dados")
    public static final ConfigValue<String> PASSWORD = ConfigValue.of("senha");

    @Key("pool-size")
    @Comment("Número máximo de conexões simultâneas")
    public static final ConfigValue<Integer> POOL_SIZE = ConfigValue.of(5);

    @Key("show-sql")
    @Comment("Exibe queries SQL no console (use apenas em desenvolvimento)")
    public static final ConfigValue<Boolean> SHOW_SQL = ConfigValue.of(false);

    @Key("hbm2ddl-auto")
    @Comment({"Estratégia de criação de tabelas: update, create, validate, none",
            "Use 'update' em produção para manter os dados existentes"})
    public static final ConfigValue<String> HBM2DDL = ConfigValue.of("update");

    private DatabaseConfig() {}
}