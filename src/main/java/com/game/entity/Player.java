package com.game.entity;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import com.game.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.game.repository.PlayerRepository.*;

@Entity
@Table(name="player")
public class Player {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name="race")
    private Race race;

    @Enumerated(EnumType.STRING)
    @Column(name="profession")
    private Profession profession;

    @Column(name="experience")
    private Integer experience;

    @Column(name="level")
    private Integer level;

    @Column(name="untilNextLevel")
    private Integer untilNextLevel;

    @Column(name="birthday")
    @DateTimeFormat(pattern = "dd-MMM-yyyy")
    private Date birthday;

    @Column(name="banned")
    private Boolean banned;

    public Player() {
    }

    public Player(String name, String title, Race race, Profession profession, Integer experience, Date birthday, Boolean banned) {
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.experience = experience;
        this.birthday = birthday;
        this.banned = banned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", race=" + race +
                ", profession=" + profession +
                ", experience=" + experience +
                ", level=" + level +
                ", untilNextLevel=" + untilNextLevel +
                ", birthday=" + birthday +
                ", banned=" + banned +
                '}';
    }

    public void updateLevelInformation() {
        this.updateLevel();
        this.updateUntilNextLevel();
    }

    private void updateLevel() {
        this.level = (int)(Math.sqrt(2500 + 200 * getExperience()) - 50) / 100;
    }

    private void updateUntilNextLevel() {
        this.untilNextLevel = 50 * (getLevel() + 1) * (getLevel() + 2) - getExperience();
    }

}

