package org.example.adventuretime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean available;

    @SuppressWarnings("checkstyle:Indentation")
    @ManyToMany(mappedBy = "countries", cascade = {CascadeType.PERSIST,
            CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Tour> tours = new HashSet<>();

    public Country() {}

    public Country(String name, boolean available) {
        this.name = name;
        this.available = available;
    }

}
