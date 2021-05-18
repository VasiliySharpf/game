package com.game.service;

import com.game.entity.Player;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface PlayerService {

    List<Player> findAll(Pageable pageable);
    Player add(Map<String, String> params);
    Player updatePlayer(Long id, Map<String, String> params);
    String deleteById(Long id);
    List<Player> findByParams(Map<String, String> params);
    Integer countByParams(Map<String, String> params);
    Integer count();
    Player findById(Long id);
    boolean existsById(Long id);
    boolean isIdValid(Long id);
    boolean isParamsValid(Map<String, String> params);
    boolean isAllParamsFound(Map<String, String> params);
    boolean isParamsUpdatePlayerValid(Map<String, String> params);

}
