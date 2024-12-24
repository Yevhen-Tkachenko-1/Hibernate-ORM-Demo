package yevhent.demo.hibernate.repository;

import jakarta.persistence.EntityManager;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;

import java.util.List;

public interface ArtTeacherRepository extends CrudRepository<ArtTeacher> {

    List<ArtTeacher> findEagerTeachersByIds(EntityManager entityManager, List<Integer> teacherIds);

}
