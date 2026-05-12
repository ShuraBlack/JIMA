package de.shurablack.jima.model.character.pet;

import de.shurablack.jima.model.shared.TimeRange;
import de.shurablack.jima.util.Nullable;
import de.shurablack.jima.util.types.Quality;
import de.shurablack.jima.util.types.SecondaryStatType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PetDetail {

    private Integer id;

    private String name;

    @Nullable
    private String customName;

    private int petId;

    private String petName;

    private String imageUrl;

    private int level;

    private int experience;

    private int totalExperience;

    private Quality quality;

    private Map<SecondaryStatType, Integer> stats;

    private Record health;

    private Record happiness;

    private Record hunger;

    private boolean equipped;

    private TimeRange battle;

    private Evolution evolution;

    private Location location;

    private LocalDateTime createdAt;
}