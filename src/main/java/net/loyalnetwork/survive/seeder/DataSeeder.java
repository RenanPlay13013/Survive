/*
package net.loyalnetwork.survive.seeder;

import net.loyalnetwork.survive.persistance.database.DatabaseManager;
import net.loyalnetwork.survive.persistance.entity.MatchEntity;
import net.loyalnetwork.survive.persistance.entity.MatchPlayerEntity;
import net.loyalnetwork.survive.persistance.entity.PlayerStatsEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

*/
/**
 * Gera dados fake (jogadores + partidas) para popular o banco — útil para
 * testar dashboards, queries, gráficos etc. sem precisar jogar 5000 partidas
 * de verdade.
 *
 * Uso (no onEnable, normalmente atrás de uma flag de config ou comando):
 *
 *   new DataSeeder(getLogger()).seed(5000);
 *
 * Cada "match" recebe entre 2 e 8 jogadores aleatórios de um pool fixo de
 * nomes fake, com timestamps distribuídos nos últimos 90 dias, vencedor
 * sorteado, e os PlayerStats agregados são atualizados de acordo.
 *//*

public class DataSeeder {

    private static final String[] FIRST_NAMES = {
            "Shadow", "Dark", "Light", "Fire", "Ice", "Storm", "Night", "Sky",
            "Iron", "Steel", "Blood", "Ghost", "Wolf", "Dragon", "Phoenix", "Raven",
            "Silent", "Swift", "Crimson", "Frost", "Toxic", "Mystic", "Royal", "Savage",
            "Lunar", "Solar", "Cyber", "Pixel", "Neon", "Venom"
    };

    private static final String[] LAST_NAMES = {
            "Wolf", "Hunter", "Slayer", "Knight", "Reaper", "Walker", "Strike",
            "Blade", "Storm", "Fang", "Claw", "Shot", "Master", "King", "Lord",
            "Killer", "Ninja", "Warrior", "Beast", "Legend", "Pro", "Gamer",
            "Player", "Boss", "X", "Z", "07", "99", "TV", "YT"
    };

    private static final String GAME_NAME = "Survive";

    */
/** Quantos jogadores fake serão criados no pool. *//*

    private static final int PLAYER_POOL_SIZE = 80;

    */
/** Linhas inseridas por flush/transação — evita uma transação gigante. *//*

    private static final int BATCH_SIZE = 100;

    private final Logger logger;
    private final Random random = new Random();

    public DataSeeder(Logger logger) {
        this.logger = logger;
    }

    */
/**
     * Gera {@code matchCount} partidas fake, cada uma com 2-8 jogadores
     * sorteados de um pool de {@value #PLAYER_POOL_SIZE} jogadores.
     *
     * Roda de forma síncrona — chame de dentro de uma task assíncrona
     * (ex: Bukkit.getScheduler().runTaskAsynchronously) se for usar no onEnable,
     * para não travar o servidor durante o seed.
     *//*

    public void seed(int matchCount) {
        long start = System.currentTimeMillis();
        logger.info("[Survive] Iniciando seed de " + matchCount + " partidas fake...");

        List<FakePlayer> playerPool = generatePlayerPool();
        Map<String, PlayerAggregate> aggregates = new HashMap<>();
        for (FakePlayer fp : playerPool) {
            aggregates.put(fp.uuid, new PlayerAggregate(fp.name));
        }

        int created = 0;
        while (created < matchCount) {
            int batch = Math.min(BATCH_SIZE, matchCount - created);
            seedBatch(batch, playerPool, aggregates);
            created += batch;

            if (created % 1000 == 0 || created == matchCount) {
                logger.info("[Survive] Seed: " + created + "/" + matchCount + " partidas criadas...");
            }
        }

        // Persiste os PlayerStats agregados de uma vez, no final
        persistPlayerStats(aggregates);

        long elapsed = System.currentTimeMillis() - start;
        logger.info("[Survive] Seed concluído: " + matchCount + " partidas, "
                + playerPool.size() + " jogadores, em " + elapsed + "ms.");
    }

    // -------------------------------------------------------------------------
    // Pool de jogadores fake
    // -------------------------------------------------------------------------

    private List<FakePlayer> generatePlayerPool() {
        List<FakePlayer> pool = new ArrayList<>(PLAYER_POOL_SIZE);
        Set<String> usedNames = new HashSet<>();

        while (pool.size() < PLAYER_POOL_SIZE) {
            String name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)]
                    + LAST_NAMES[random.nextInt(LAST_NAMES.length)]
                    + random.nextInt(1000);

            if (!usedNames.add(name)) continue; // evita duplicado

            pool.add(new FakePlayer(UUID.randomUUID().toString(), name));
        }

        return pool;
    }

    // -------------------------------------------------------------------------
    // Geração de partidas em lote
    // -------------------------------------------------------------------------

    private void seedBatch(int count, List<FakePlayer> playerPool, Map<String, PlayerAggregate> aggregates) {
        Transaction tx = null;

        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            for (int i = 0; i < count; i++) {
                createFakeMatch(session, playerPool, aggregates);

                // Flush + clear periódico para não acumular entidades na sessão
                if (i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.severe("[Survive] Erro ao gerar seed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createFakeMatch(Session session, List<FakePlayer> playerPool, Map<String, PlayerAggregate> aggregates) {
        // Sorteia entre 2 e 8 jogadores distintos para essa partida
        int playerCount = 2 + random.nextInt(7);
        List<FakePlayer> shuffled = new ArrayList<>(playerPool);
        Collections.shuffle(shuffled, random);
        List<FakePlayer> matchPlayers = shuffled.subList(0, playerCount);

        // Timestamps espalhados nos últimos 90 dias
        long secondsAgo = random.nextInt(90 * 24 * 60 * 60);
        Instant startedAt = Instant.now().minus(secondsAgo, ChronoUnit.SECONDS);
        int durationSeconds = 60 + random.nextInt(240); // partida dura 1-5 min
        Instant endedAt = startedAt.plus(durationSeconds, ChronoUnit.SECONDS);

        // 1 lobbyId qualquer só pra registro (não precisa ser real/único)
        String lobbyId = GAME_NAME + "-" + (1 + random.nextInt(20));

        MatchEntity match = new MatchEntity(lobbyId, GAME_NAME);
        match.setStartedAt(startedAt);
        match.setEndedAt(endedAt);
        match.setStatus("FINISHED");

        // Sorteia o vencedor (95% das vezes tem vencedor, 5% empate/sem vencedor)
        FakePlayer winner = random.nextDouble() < 0.95
                            ? matchPlayers.get(random.nextInt(matchPlayers.size()))
                            : null;

        match.setWinnerUuid(winner != null ? winner.uuid : null);
        match.setWinnerName(winner != null ? winner.name : null);

        session.persist(match);

        for (FakePlayer player : matchPlayers) {
            MatchPlayerEntity mp = new MatchPlayerEntity(match, player.uuid, player.name);
            mp.setJoinedAt(startedAt);

            PlayerAggregate agg = aggregates.get(player.uuid);
            agg.matchesPlayed++;

            if (player.equals(winner)) {
                mp.setStatus("WON");
                agg.wins++;
            } else {
                // 70% morreu, 30% saiu (quit) antes do fim
                boolean died = random.nextDouble() < 0.7;
                mp.setStatus(died ? "DEAD" : "QUIT");
                mp.setEliminationReason(died ? "DEATH" : "QUIT");

                // Eliminado em algum momento entre o início e o fim da partida
                long eliminatedOffset = random.nextInt(Math.max(1, durationSeconds));
                mp.setEliminatedAt(startedAt.plus(eliminatedOffset, ChronoUnit.SECONDS));

                agg.deaths++;
                if (!died) agg.quits++;
            }

            session.persist(mp);
        }
    }

    // -------------------------------------------------------------------------
    // PlayerStats agregados
    // -------------------------------------------------------------------------

    private void persistPlayerStats(Map<String, PlayerAggregate> aggregates) {
        Transaction tx = null;

        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            int i = 0;
            for (Map.Entry<String, PlayerAggregate> entry : aggregates.entrySet()) {
                String uuid = entry.getKey();
                PlayerAggregate agg = entry.getValue();

                PlayerStatsEntity stats = session.get(PlayerStatsEntity.class, uuid);
                if (stats == null) {
                    stats = new PlayerStatsEntity(UUID.fromString(uuid), agg.name);
                    session.persist(stats);
                }

                stats.setMatchesPlayed(agg.matchesPlayed);
                stats.setWins(agg.wins);
                stats.setDeaths(agg.deaths);
                stats.setQuits(agg.quits);

                if (++i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.severe("[Survive] Erro ao persistir player stats do seed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private record FakePlayer(String uuid, String name) {}

    private static final class PlayerAggregate {
        final String name;
        int matchesPlayed = 0;
        int wins = 0;
        int deaths = 0;
        int quits = 0;

        PlayerAggregate(String name) {
            this.name = name;
        }
    }
}*/
