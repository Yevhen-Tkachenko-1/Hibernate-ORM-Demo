package yevhent.demo.hibernate.repository;

import jakarta.persistence.EntityManager;
import yevhent.demo.hibernate.entity.Identifiable;

import java.util.Optional;

public class BaseCrudRepository<T extends Identifiable> implements CrudRepository<T>{

    private final Class<T> entityType;

    public BaseCrudRepository(Class<T> entityType) {
        this.entityType = entityType;
    }

    @Override
    public T create(EntityManager entityManager, T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Optional<T> read(EntityManager entityManager, int id) {
        return Optional.ofNullable(entityManager.find(entityType, id));
    }

    @Override
    public boolean update(EntityManager entityManager, T entity) {
        T existingEntity = entityManager.find(entityType, entity.getId());
        if (existingEntity == null) {
            return false;
        }
        if (existingEntity.equals(entity)) {
            return false;
        }
        entityManager.merge(entity);
        return true;
    }

    @Override
    public boolean delete(EntityManager entityManager, int id) {
        T entity = entityManager.find(entityType, id);
        if (entity == null) {
            return false;
        }
        entityManager.remove(entity);
        return true;
    }
}
