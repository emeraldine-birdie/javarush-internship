package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import com.game.specification.PlayerSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/rest")
public class PlayersController {
    private PlayerService playerService;
    private PlayerSpecification playerSpecification;

    public PlayersController(PlayerService playerService, PlayerSpecification playerSpecification){
        this.playerService = playerService;
        this.playerSpecification = playerSpecification;
    }

    @GetMapping("/players")
    public Iterable<Player> getPlayersList(
            @RequestParam (required = false) String name,
            @RequestParam (required = false) String title,
            @RequestParam (required = false) Race race,
            @RequestParam (required = false) Profession profession,
            @RequestParam (required = false) Long after,
            @RequestParam (required = false) Long before,
            @RequestParam (required = false) Boolean banned,
            @RequestParam (required = false) Integer minExperience,
            @RequestParam (required = false) Integer maxExperience,
            @RequestParam (required = false) Integer minLevel,
            @RequestParam (required = false) Integer maxLevel,
            @RequestParam (required = false, defaultValue = "ID") PlayerOrder playerOrder,
            @RequestParam (required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam (required = false, defaultValue = "3") Integer pageSize
    ){

        Specification<Player> specification = Specification
                .where(playerSpecification.filterLikeByName(name))
                .and(playerSpecification.filterLikeByTitle(title))
                .and(playerSpecification.filterEqualsByRace(race))
                .and(playerSpecification.filterEqualsByProfession(profession))
                .and(playerSpecification.filterBetweenByBirthday(after, before))
                .and(playerSpecification.filterByBanned(banned))
                .and(playerSpecification.filterBetweenByExperience(minExperience, maxExperience))
                .and(playerSpecification.filterBetweenByLevel(minLevel, maxLevel));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(playerOrder.getFieldName()));

        return playerService.getPlayers(specification, pageable);
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    @PostMapping("/players/{id}")
    public Player updatePlayerById(@PathVariable("id") Long id, @RequestBody Player player) {
        return playerService.updatePlayer(id, player);
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Object> deletePlayerById(@PathVariable("id") Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/players/{id}")
    public Player getPlayerById(@PathVariable("id") Long id){
        return playerService.getPlayerById(id);
    }

    @GetMapping("/players/count")
    public Integer getPlayersCount(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel
    ){
        Specification<Player> specification = Specification
                .where(playerSpecification.filterLikeByName(name))
                .and(playerSpecification.filterLikeByTitle(title))
                .and(playerSpecification.filterEqualsByRace(race))
                .and(playerSpecification.filterEqualsByProfession(profession))
                .and(playerSpecification.filterBetweenByBirthday(after, before))
                .and(playerSpecification.filterByBanned(banned))
                .and(playerSpecification.filterBetweenByExperience(minExperience, maxExperience))
                .and(playerSpecification.filterBetweenByLevel(minLevel, maxLevel));

        return playerService.getPlayersCount(specification);
    }
}
