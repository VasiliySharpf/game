package com.game.controller;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import com.game.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rest/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping()
    public @ResponseBody ResponseEntity<List<Player>> getPlayersList(@RequestParam Map<String, String> params) {
        if (params.isEmpty()) {
            return new ResponseEntity<List<Player>>(playerService.findAll(PageRequest.of(0, 3)), HttpStatus.OK);
        } else {
            return new ResponseEntity<List<Player>>(playerService.findByParams(params), HttpStatus.OK);
        }
    }

    @GetMapping("/count")
    public @ResponseBody Integer getPlayersCount(@RequestParam Map<String, String> params){
        if (params.isEmpty()) {
            return playerService.count();
        } else {
            return playerService.countByParams(params);
        }
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<Player> getPlayer(
            @PathVariable Long id
    ){
        try {
            if (!playerService.isIdValid(id)) {
                return new ResponseEntity<Player>(HttpStatus.BAD_REQUEST);
            }
            if (!playerService.existsById(id)) {
                return new ResponseEntity<Player>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<Player>(playerService.findById(id), HttpStatus.OK);
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            return new ResponseEntity<Player>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ResponseEntity<Player> addPlayer(@RequestBody Map<String, String> params){
        if (!playerService.isAllParamsFound(params) || !playerService.isParamsValid(params))
            return new ResponseEntity<Player>(HttpStatus.BAD_REQUEST);
        Player player = playerService.add(params);
        ResponseEntity<Player> result = player == null ?
                new ResponseEntity<Player>(HttpStatus.BAD_REQUEST) : new ResponseEntity<Player>(player, HttpStatus.OK);
        return result;
    }

    @DeleteMapping("/{id}")
    public @ResponseBody
    ResponseEntity<Player> deletePlayer(@PathVariable Long id){
        ResponseEntity<Player> okResponse = new ResponseEntity<Player>(HttpStatus.OK);
        ResponseEntity<Player> badResponse = new ResponseEntity<Player>(HttpStatus.BAD_REQUEST);
        ResponseEntity<Player> nfResponse = new ResponseEntity<Player>(HttpStatus.NOT_FOUND);
        try {
            if (!playerService.isIdValid(id)) return badResponse;
            String result = playerService.deleteById(id);
            if (result == null) return badResponse;
            if ("404".equals(result)) return nfResponse;
            if ("200".equals(result)) return okResponse;
        } catch (NullPointerException | IllegalArgumentException e) {
            return badResponse;
        }
        return badResponse;
    }

    @PostMapping("/{id}")
    public @ResponseBody
    ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Map<String, String> params){

        if (id == null
                || !playerService.isIdValid(id)
                || !playerService.isParamsUpdatePlayerValid(params)) {
            return new ResponseEntity<Player>(HttpStatus.BAD_REQUEST);
        }

        Player result = playerService.updatePlayer(id, params);

        if (result == null) {
            return new ResponseEntity<Player>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<Player>(result, HttpStatus.OK);
        }
    }


}
