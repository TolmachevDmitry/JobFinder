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
    List<Object[]> findCosinus(@Param("vect") float[] vect, double threshold);

    @Query(value = "CALL find_missing_identifiers(:identifiers);", nativeQuery = true)
    List<Long> findMissingIdentifiers(@Param("identifiers") List<Long> identifiers);

    @Query(value = "CALL find_deprecated_indetifiers (:identifiers);", nativeQuery = true)
    List<Long> findDeprecatedIndetifiers (@Param("identifiers") List<Long> identifiers);

    @Query(value = "CALL defind_deprecated_vacancies (:identifiers);", nativeQuery = true)
    void defindDeprecatedVacancies(@Param("identifiers") List<Long> identifiers);

    @Query(value = "CALL defind_deprecated_vacancies (:identifiers);", nativeQuery = true)
    void deleteDeprecatedVacancies(@Param("identifiers") List<Long> identifiers);

}
