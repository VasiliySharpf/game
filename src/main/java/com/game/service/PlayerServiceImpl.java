package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.xml.transform.sax.SAXResult;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.Date;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerServiceImpl() {
    }

    @Override
    public List<Player> findAll(Pageable pageable) {
        Page<Player> pages = playerRepository.findAll(pageable);
        List<Player> players = pages.stream().collect(Collectors.toList());
        return players;
        //return playerRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    @Override
    public List<Player> findByParams(Map<String, String> params) {

        String name = (String) params.getOrDefault("name", null);
        String title = (String) params.getOrDefault("title", null);

        Race race = params.containsKey("race") ? Race.valueOf((String) params.get("race")) : null;
        Profession profession = params.containsKey("profession") ? Profession.valueOf((String) params.get("profession")) : null;

        Calendar calendar = Calendar.getInstance();
        Date after = null;
        if (params.containsKey("after")) {
            after = new Date(Long.parseLong(params.get("after")));
        }

        Date before = null;
        if (params.containsKey("before")) {
            before = new Date(Long.parseLong(params.get("before")));
        }

        Boolean banned = params.containsKey("banned") ? Boolean.parseBoolean(params.get("banned")) : null;

        Integer minExperience = params.containsKey("minExperience") ? Integer.parseInt(params.get("minExperience")) : null;
        Integer maxExperience = params.containsKey("maxExperience") ? Integer.parseInt(params.get("maxExperience")) : null;

        Integer minLevel = params.containsKey("minLevel") ? Integer.parseInt(params.get("minLevel")) : null;
        Integer maxLevel = params.containsKey("maxLevel") ? Integer.parseInt(params.get("maxLevel")) : null;

        Pageable pageable;
        int pageNumber = Integer.parseInt(params.getOrDefault("pageNumber", "0"));
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "3"));

        if (params.containsKey("order")) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, (PlayerOrder.valueOf(params.get("order"))).getFieldName());
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }

        return playerRepository.findAllByParams(
                name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel, pageable).stream().collect(Collectors.toList());
    }

    @Override
    public Player add(Map<String, String> params) {
        try {
            String name = params.getOrDefault("name", null);
            String title = params.getOrDefault("title", null);
            Race race = Race.valueOf(params.get("race"));
            Profession profession = Profession.valueOf(params.get("profession"));
            Date birthday = new Date(Long.parseLong(params.get("birthday")));
            Boolean banned = params.containsKey("banned") && "true".equals(params.get("banned"));
            Integer experience = Integer.parseInt(params.get("experience"));
            //Integer level = Integer.parseInt(params.get("level"));
           // Integer untilNextLevel = Integer.parseInt(params.get("untilNextLevel"));

            Player player = new Player(name, title, race, profession, experience, birthday, banned);
            player.updateLevelInformation();

            return playerRepository.save(player);
        } catch (NullPointerException | IllegalArgumentException | ClassCastException e) {
            return null;
        }
    }

    @Override
    public Player updatePlayer(Long id, Map<String, String> params) {

        if (!playerRepository.findById(id).isPresent() || params == null)
            return null;

        Player result = playerRepository.findById(id).get();
        String name = params.getOrDefault("name", null);

        String title = params.getOrDefault("title", null);
        Profession profession = params.containsKey("profession") ? Profession.valueOf(params.get("profession")) : null;
        Race race = params.containsKey("race") ? Race.valueOf(params.get("race")) : null;
        Date birthday = params.containsKey("birthday") ? new Date(Long.parseLong(params.get("birthday"))) : null;
        Boolean banned = params.containsKey("banned") ? "true".equals(params.get("banned")) : null;
        Integer experience = params.containsKey("experience") ? Integer.parseInt(params.get("experience")) : null;

        if (name != null && isNameValid(name)) result.setName(name);
        if (title != null && isTitleValid(title)) result.setTitle(title);
        if (profession != null) result.setProfession(profession);
        if (race != null) result.setRace(race);
        if (birthday != null && isBirthdayValid(getBirthdayFromParams(params))) result.setBirthday(birthday);
        if (banned != null) result.setBanned(banned);

        if (experience != null && isExperienceValid(experience)) {
            result.setExperience(experience);
            result.updateLevelInformation();
        }

        playerRepository.saveAndFlush(result);
        return result;
    }

    @Override
    public String deleteById(Long id) {
        boolean isShipExists = playerRepository.existsById(id);
        if (isShipExists) {
            playerRepository.deleteById(id);
            return "200";
        } else {
            return "404";
        }
    }

    @Override
    public Integer countByParams(Map<String, String> params) {

        String name = (String) params.getOrDefault("name", null);
        String title = (String) params.getOrDefault("title", null);

        Race race = params.containsKey("race") ? Race.valueOf((String) params.get("race")) : null;
        Profession profession = params.containsKey("profession") ? Profession.valueOf((String) params.get("profession")) : null;

        Calendar calendar = Calendar.getInstance();
        Date after = null;
        if (params.containsKey("after")) {
            after = new Date(Long.parseLong(params.get("after")));
        }

        Date before = null;
        if (params.containsKey("before")) {
            before = new Date(Long.parseLong(params.get("before")));
        }

        Boolean banned = params.containsKey("banned") ? Boolean.parseBoolean(params.get("banned")) : null;

        Integer minExperience = params.containsKey("minExperience") ? Integer.parseInt(params.get("minExperience")) : null;
        Integer maxExperience = params.containsKey("maxExperience") ? Integer.parseInt(params.get("maxExperience")) : null;

        Integer minLevel = params.containsKey("minLevel") ? Integer.parseInt(params.get("minLevel")) : null;
        Integer maxLevel = params.containsKey("maxLevel") ? Integer.parseInt(params.get("maxLevel")) : null;

        Pageable pageable;
        int pageNumber = Integer.parseInt(params.getOrDefault("pageNumber", "0"));
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "3"));

        if (params.containsKey("order")) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, (PlayerOrder.valueOf(params.get("order"))).getFieldName());
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }

        return playerRepository.countByParams(
                name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);

    }

    @Override
    public Integer count() {
        try {
            return Math.toIntExact(playerRepository.count());
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public Player findById(Long id) {
        return playerRepository.findById(id).get();
    }

    @Override
    public boolean existsById(Long id) {
        return playerRepository.existsById(id);
    }

    @Override
    public boolean isIdValid(Long id) {
        return id > 0;
    }

    @Override
    public boolean isParamsValid(Map<String, String> params) {

        String name = params.getOrDefault("name", null);
        String title = params.getOrDefault("title", null);
        Integer experience = Integer.parseInt(params.getOrDefault("experience", null));

        Integer birthday = getBirthdayFromParams(params);

        return name != null && isNameValid(name)
                && title != null && isTitleValid(title)
                && birthday != null && isBirthdayValid(birthday)
                && isExperienceValid(experience);
    }

    public boolean isParamsUpdatePlayerValid(Map<String, String> params) {

        if (params.isEmpty())
            return true;

        String name = params.getOrDefault("name", null);
        String title = params.getOrDefault("title", null);
        String expStr = params.getOrDefault("experience", null);
        Integer experience = null;
        if (expStr != null)
            experience = Integer.parseInt(expStr);

        Integer birthday = getBirthdayFromParams(params);

        if (name != null && !isNameValid(name))
            return false;
        else if (title != null && !isTitleValid(title))
            return false;
        else if (birthday != null && !isBirthdayValid(birthday))
            return false;
        else if (experience != null && !isExperienceValid(experience))
            return false;

        return true;
    }

    public Integer getBirthdayFromParams(Map<String, String> params) {

        Integer birthday = null;
        if (params.containsKey("birthday")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(params.get("birthday")));
            birthday = calendar.get(Calendar.YEAR);
        }

        return birthday;
    }

    public boolean isNameValid(String name) {
        return name.length() != 0 && name.length() <= 12;
    }

    public boolean isTitleValid(String title) {
        return title.length() != 0 && title.length() <= 30;
    }

    public boolean isBirthdayValid(Integer birthday) {
        return birthday >= 2000 && birthday <= 3000;
    }

    public boolean isExperienceValid(Integer experience) {
        return experience > 0 && experience <= 10_000_000;
    }

    @Override
    public boolean isAllParamsFound(Map<String, String> params) {
        return params.containsKey("name")
                && params.containsKey("title")
                && params.containsKey("race")
                && params.containsKey("profession")
                && params.containsKey("birthday")
                && params.containsKey("experience");
    }
}
