package yevhent.demo.hibernate.repository;

import jakarta.persistence.EntityManager;
import yevhent.demo.hibernate.entity.ArtTeacher;

import java.util.Optional;

public class ArtTeacherRepositoryImpl implements ArtTeacherRepository {

    @Override
    public ArtTeacher create(EntityManager entityManager, ArtTeacher entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Optional<ArtTeacher> read(EntityManager entityManager, int id) {
        return Optional.ofNullable(entityManager.find(ArtTeacher.class, id));
    }

    @Override
    public boolean update(EntityManager entityManager, ArtTeacher entity) {
        ArtTeacher artTeacher = entityManager.find(ArtTeacher.class, entity.getId());
        if (artTeacher == null) {
            return false;
        }
        if (artTeacher.equals(entity)) {
            return false;
        }
        entityManager.merge(entity);
        return true;
    }

    @Override
    public boolean delete(EntityManager entityManager, int id) {
        ArtTeacher artTeacher = entityManager.find(ArtTeacher.class, id);
        if (artTeacher == null) {
            return false;
        }
        entityManager.remove(artTeacher);
        return true;
    }

    @Override
    public Double getAverageRating(EntityManager entityManager, int teacherId) {
        return null;
    }

    @Override
    public boolean deleteRatingsBelow(EntityManager entityManager, int teacherId, int minAllowedRating) {
        return false;
    }
}