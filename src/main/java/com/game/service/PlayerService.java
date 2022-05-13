package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    Iterable<Player> getPlayers(Specification<Player> specification, Pageable pageable);

    Integer getPlayersCount(Specification<Player> specification);

    Player createPlayer(Player player);

    Player getPlayerById(Long id);

    Player updatePlayer(Long id, Player player);

    void deletePlayer(Long id);

}
