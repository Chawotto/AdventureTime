package org.example.adventuretime.country;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.example.adventuretime.tour.Tour;

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

}
