package net.loyalnetwork.survive.persistance.repository;

import net.loyalnetwork.survive.persistance.database.DatabaseManager;
import net.loyalnetwork.survive.persistance.entity.MatchEntity;
import net.loyalnetwork.survive.persistance.entity.MatchPlayerEntity;
import net.loyalnetwork.survive.persistance.entity.PlayerStatsEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persiste partidas e estatísticas.
 *
 * O "lobby_id" do MiniGameCore (ex: "Survive-1") é REAPROVEITADO entre partidas
 * — não é um identificador único ao longo do tempo. Por isso, mantemos um mapa
 * em memória (lobbyId -> matchId) para saber qual partida (linha do banco)
 * está atualmente em andamento para cada lobby.
 *
 * Esse mapa é populado em createMatch() e limpo em finishMatch().
 */
public class MatchRepository {

    /** lobbyId (ex: "Survive-1") -> matchId (UUID único da partida atual) */
    private final ConcurrentHashMap<String, String> activeMatches = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // Partida
    // -------------------------------------------------------------------------

    /**
     * Cria o registro da partida quando o lobby inicia (GameStartEvent).
     * Retorna o matchId gerado — também é guardado internamente para uso
     * pelos demais métodos enquanto a partida estiver em andamento.
     */
    public String createMatch(String lobbyId, String gameName) {
        MatchEntity match = new MatchEntity(lobbyId, gameName);
        match.setStartedAt(Instant.now());
        match.setStatus("IN_GAME");

        runTransaction(session -> session.persist(match));

        activeMatches.put(lobbyId, match.getMatchId());
        return match.getMatchId();
    }

    /** Encerra a partida com um vencedor (ou null em caso de empate/cancelamento). */
    public void finishMatch(String lobbyId, UUID winnerUuid, String winnerName) {
        // IMPORTANTE: NÃO remover a entrada de activeMatches aqui.
        //
        // O GameOverEvent é disparado de forma SÍNCRONA dentro do próprio
        // handler de PlayerDeathEvent do MGC (endGame() é chamado e dispara
        // o evento imediatamente). Isso significa que finishMatch() roda
        // ANTES do nosso onDeath (MONITOR) processar a eliminação do
        // jogador que acabou de morrer e venceu a checagem de "último vivo".
        //
        // Se removêssemos a entrada aqui, eliminateByDeath() chamado depois
        // para esse mesmo jogador não encontraria mais o matchId e o status
        // dele ficaria como ALIVE para sempre.
        //
        // A entrada é sobrescrita (put) na próxima vez que createMatch()
        // for chamado para o mesmo lobbyId, então não há acúmulo real —
        // o conjunto de lobbyIds (Survive-1, Survive-2...) é limitado.
        String matchId = activeMatches.get(lobbyId);
        if (matchId == null) {
            Bukkit.getLogger().warning("[Survive] finishMatch: nenhuma partida ativa encontrada para lobby " + lobbyId);
            return;
        }

        runTransaction(session -> {
            MatchEntity match = session.get(MatchEntity.class, matchId);
            if (match == null) return;

            match.setEndedAt(Instant.now());
            match.setStatus("FINISHED");
            match.setWinnerUuid(winnerUuid != null ? winnerUuid.toString() : null);
            match.setWinnerName(winnerName);

            if (winnerUuid != null) {
                session.createMutationQuery(
                                "UPDATE MatchPlayerEntity mp SET mp.status = 'WON' " +
                                        "WHERE mp.match.matchId = :matchId AND mp.playerUuid = :uuid"
                        )
                        .setParameter("matchId", matchId)
                        .setParameter("uuid", winnerUuid.toString())
                        .executeUpdate();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Jogadores na partida
    // -------------------------------------------------------------------------

    /** Registra um jogador como participante da partida atual (no GameStartEvent). */
    public void addPlayerToMatch(String lobbyId, Player player) {
        String matchId = activeMatches.get(lobbyId);
        if (matchId == null) {
            Bukkit.getLogger().warning("[Survive] addPlayerToMatch: nenhuma partida ativa para lobby " + lobbyId);
            return;
        }

        runTransaction(session -> {
            MatchEntity match = session.get(MatchEntity.class, matchId);
            if (match == null) return;

            MatchPlayerEntity mp = new MatchPlayerEntity(
                    match,
                    player.getUniqueId().toString(),
                    player.getName()
            );
            session.persist(mp);

            upsertPlayerStats(session, player);

            PlayerStatsEntity stats = session.get(PlayerStatsEntity.class, player.getUniqueId().toString());
            if (stats != null) stats.incrementMatchesPlayed();
        });
    }

    /** Marca o jogador como eliminado por morte na partida atual do lobby. */
    public void eliminateByDeath(String lobbyId, UUID playerUuid) {
        String matchId = activeMatches.get(lobbyId);
        if (matchId == null) return;

        runTransaction(session -> {
            updateMatchPlayer(session, matchId, playerUuid.toString(), "DEAD", "DEATH");

            PlayerStatsEntity stats = session.get(PlayerStatsEntity.class, playerUuid.toString());
            if (stats != null) stats.incrementDeaths();
        });
    }

    /** Marca o jogador como eliminado por quit na partida atual do lobby. */
    public void eliminateByQuit(String lobbyId, UUID playerUuid) {
        String matchId = activeMatches.get(lobbyId);
        if (matchId == null) return;

        runTransaction(session -> {
            updateMatchPlayer(session, matchId, playerUuid.toString(), "QUIT", "QUIT");

            PlayerStatsEntity stats = session.get(PlayerStatsEntity.class, playerUuid.toString());
            if (stats != null) {
                stats.incrementDeaths();
                stats.incrementQuits();
            }
        });
    }

    /** Incrementa wins no PlayerStats do vencedor. */
    public void registerWin(UUID winnerUuid) {
        runTransaction(session -> {
            PlayerStatsEntity stats = session.get(PlayerStatsEntity.class, winnerUuid.toString());
            if (stats != null) stats.incrementWins();
        });
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private void updateMatchPlayer(Session session, String matchId, String playerUuid,
            String status, String reason) {
        MatchPlayerEntity mp = session.createQuery(
                        "FROM MatchPlayerEntity mp WHERE mp.match.matchId = :matchId AND mp.playerUuid = :uuid",
                        MatchPlayerEntity.class
                )
                .setParameter("matchId", matchId)
                .setParameter("uuid", playerUuid)
                .uniqueResult();

        if (mp == null) return;

        mp.setStatus(status);
        mp.setEliminationReason(reason);
        mp.setEliminatedAt(Instant.now());
    }

    private void upsertPlayerStats(Session session, Player player) {
        String uuid = player.getUniqueId().toString();
        PlayerStatsEntity stats = session.get(PlayerStatsEntity.class, uuid);

        if (stats == null) {
            stats = new PlayerStatsEntity(player.getUniqueId(), player.getName());
            session.persist(stats);
        } else {
            stats.setLastKnownName(player.getName());
        }
    }

    private void runTransaction(SessionConsumer action) {
        Transaction tx = null;
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            action.accept(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            Bukkit.getLogger().severe("[Survive] Erro no banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface SessionConsumer {
        void accept(Session session);
    }
}