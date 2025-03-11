package org.example.adventuretime.repository;

import java.util.List;
import org.example.adventuretime.model.Tour;
import org.example.adventuretime.model.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByTransport(Transport transport);

    @Query("SELECT t FROM Tour t WHERE t.transport.name = :name")
    List<Tour> findToursByTransportTypeJpql(@Param("name") String name);
}
