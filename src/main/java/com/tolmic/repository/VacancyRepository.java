package com.tolmic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tolmic.entity.Vacancy;

// SELECT v, CAST(v.vectors AS vec) <=> CAST(:vect AS vector1) FROM vacancy v
@Repository
public interface VacancyRepository extends CrudRepository<Vacancy, Long> {
    @Query(value = "SELECT v FROM vacancy v", nativeQuery = true)
    public List<Object[]> findCosinus(@Param("vect") float[] vect);
}
