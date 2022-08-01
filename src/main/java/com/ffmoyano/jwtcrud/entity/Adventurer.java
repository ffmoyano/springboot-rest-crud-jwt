package com.ffmoyano.jwtcrud.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "adventurer")
public class Adventurer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true)
    private String name;

    @Column(name="character_class")
    private String characterClass;

    @Column(name="hit_points")
    private int hitPoints;

    private String alignment;

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

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Adventurer adventurer = (Adventurer) o;

        return Objects.equals(id, adventurer.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return ("""
                Adventurer{
                    id='%s',
                    name='%s',
                    characterClass='%s',
                    hitPoints='%s',
                    alignment='%s'
                }""").formatted(this.id, this.name, this.characterClass,
                this.hitPoints, this.alignment);
    }
}