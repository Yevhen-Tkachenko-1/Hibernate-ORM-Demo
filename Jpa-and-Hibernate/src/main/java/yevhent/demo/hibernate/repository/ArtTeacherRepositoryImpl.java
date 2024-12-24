package yevhent.demo.hibernate.repository;

import jakarta.persistence.EntityManager;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;

import java.util.List;

public class ArtTeacherRepositoryImpl extends BaseCrudRepository<ArtTeacher> implements ArtTeacherRepository {

    public ArtTeacherRepositoryImpl() {
        super(ArtTeacher.class);
    }

    @Override
    public List<ArtTeacher> findEagerTeachersByIds(EntityManager entityManager, List<Integer> teacherIds) {
        return entityManager.createQuery("""
                                SELECT t FROM ArtTeacher t
                                LEFT JOIN FETCH t.artReviews
                                WHERE t.id IN :teacherIds
                                """, ArtTeacher.class)
                .setParameter("teacherIds", teacherIds)
                .getResultList();
    }

}