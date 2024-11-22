package yevhent.demo.hibernate.repository;

import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Data access layer for CRUD operations over Art Entities
 */
public interface ArtCrudRepository<T> {

    /**
     * Creates and saves new Entity to DB.
     * @param entityManager data source and transaction holder
     * @param entity holder of values to save, ID field is ignored.
     * Whether underling Entities will be created is not managed by this method (depends on Entity schema).
     * @return the same instance as Entity parameter which can be used to get generated ID.
     */
    T create(EntityManager entityManager, T entity);

    /**
     * Selects Entity by ID from DB.
     * Whether underling Entities will be loaded is not managed by this method (depends on Entity schema).
     * @param entityManager data source and transaction holder
     * @param id primary key (from 1 to Integer.MAX_VALUE)
     * @return Entity if exists with given ID
     */
    Optional<T> read(EntityManager entityManager, int id);

    /**
     * Optionally updates Entity (by its ID) in DB.
     * Whether underling Entities will be updated is not managed by this method (depends on Entity schema).
     * @param entityManager data source and transaction holder
     * @param entity holder of Entity ID to find target and Entity new values to set
     * @return True if any field of existing Entity was updated.
     * False if Entity is not in DB or Entity is already has the same values.
     */
    boolean update(EntityManager entityManager, T entity);

    /**
     * Deletes Entity by ID from DB.
     * Only this Entity itself is deleted (underling Entities should be deleted explicitly).
     * @param entityManager data source and transaction holder
     * @param id primary key (from 1 to Integer.MAX_VALUE)
     * @return True if Entity was in DB and successfully deleted.
     * False if no Entity with given ID to delete.
     */
    boolean delete(EntityManager entityManager, int id);
}
