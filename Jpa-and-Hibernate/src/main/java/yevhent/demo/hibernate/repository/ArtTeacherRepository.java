package yevhent.demo.hibernate.repository;

import jakarta.persistence.EntityManager;
import yevhent.demo.hibernate.entity.ArtTeacher;

import java.util.Optional;

public interface ArtTeacherRepository extends ArtCrudRepository<ArtTeacher> {

    /**
     * For given Teacher calculates average rating of all its Reviews
     * @param entityManager data source and transaction holder
     * @param id Teacher ID (from 1 to Integer.MAX_VALUE)
     * @return average Review rating for given Teacher if given Teacher is present and has at least one Review
     */
    Optional<Double> getAverageRating(EntityManager entityManager, int teacherId);

    /**
     * For given Teacher deletes any rating that is lower than given value exclusively
     * @param entityManager data source and transaction holder
     * @param teacherId ID (from 1 to Integer.MAX_VALUE)
     * @param minAllowedRating minimum possible rating to retain
     * @return
     */
    boolean deleteRatingsBelow(EntityManager entityManager, int teacherId, int minAllowedRating);
}
