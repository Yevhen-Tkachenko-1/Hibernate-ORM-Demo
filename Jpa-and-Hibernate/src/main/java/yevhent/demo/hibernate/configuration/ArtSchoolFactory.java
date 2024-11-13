package yevhent.demo.hibernate.configuration;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class ArtSchoolFactory {

    public static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.user", System.getenv("POSTGRESQL_USER_NAME"));
        properties.put("jakarta.persistence.jdbc.password", System.getenv("POSTGRESQL_USER_PASSWORD"));
        return Persistence.createEntityManagerFactory("art_school", properties);
    }
}