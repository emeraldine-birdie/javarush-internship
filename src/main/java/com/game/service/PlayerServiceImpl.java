package com.game.service;

import com.game.entity.Player;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.NotFoundRequestException;
import com.game.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService{
    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }

    @Override
    public Iterable<Player> getPlayers(Specification<Player> specification, Pageable pageable) {
        Page<Player> playerPage = playerRepository.findAll(specification, pageable);
        return playerPage.getContent();
    }

    @Override
    public Integer getPlayersCount(Specification<Player> specification) {
        long count = playerRepository.count(specification);
        return (int) count;
    }

    @Override
    public Player createPlayer(Player player) {
        if (!isValidPlayer(player)){
            throw new BadRequestException("Data params are not valid");
        }

        if (player.getId() != null)
            player.setId(null);

        if (player.getBanned() == null){
            player.setBanned(false);
        }

        calculateLevel(player);

        return playerRepository.save(player);
    }

    @Override
    public Player getPlayerById(Long id) {
        if (id <= 0){
            throw new BadRequestException("Id is not valid");
        }
        Optional<Player> player = playerRepository.findById(id);

        if (!player.isPresent()){
            throw new NotFoundRequestException("Player is not found in the database");
        }
        return player.get();
    }

    @Override
    public Player updatePlayer(Long id, Player player) {
        Player updatedPlayer = getPlayerById(id);

        if (player.getName() != null)
            updatedPlayer.setName(player.getName());
        if (player.getTitle() != null)
            updatedPlayer.setTitle(player.getTitle());
        if (player.getRace() != null)
            updatedPlayer.setRace(player.getRace());
        if (player.getProfession() != null)
            updatedPlayer.setProfession(player.getProfession());
        if (player.getBirthday() != null){
            if (!isValidDate(player.getBirthday()))
                throw new BadRequestException("Birthday is not valid");
            updatedPlayer.setBirthday(player.getBirthday());
        }
        if (player.getBanned() != null)
            updatedPlayer.setBanned(player.getBanned());
        if (player.getExperience() != null) {
            if (!isValidExperience(player.getExperience()))
                throw new BadRequestException("Experience is not valid");
            updatedPlayer.setExperience(player.getExperience());
            calculateLevel(updatedPlayer);
        }

        return playerRepository.save(updatedPlayer);
    }

    @Override
    public void deletePlayer(Long id) {
        if (id <= 0) throw new BadRequestException("Id is not valid");

        Player deletedPlayer = getPlayerById(id);
        playerRepository.delete(deletedPlayer);
    }

    private boolean isValidPlayer(Player player){
        boolean result = false;

        if (player != null &&
            player.getName() != null &&
            player.getTitle() != null &&
            player.getRace() != null &&
            player.getProfession() != null &&
            player.getBirthday() != null &&
            isValidExperience(player.getExperience()) &&
            !player.getName().trim().isEmpty() &&
            player.getName().length() <= 12 &&
            player.getTitle().length() <= 30){

            result = true;
        }
        return result;
    }

    private boolean isValidExperience(Integer experience){
        boolean result = false;

        if (experience != null &&
            experience > 0 &&
            experience <= 10_000_000){

            result = true;
        }
        return result;
    }

    private boolean isValidDate(Date date){
        boolean result = false;

        if (date != null && date.getTime() >= 0){
            result = true;
        }
        return result;
    }

    private void calculateLevel(Player player){
        int level  = (int) (Math.sqrt(2500 + (200 * player.getExperience())) - 50) / 100;
        player.setLevel(level);

        int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
        player.setUntilNextLevel(untilNextLevel);
    }
}
