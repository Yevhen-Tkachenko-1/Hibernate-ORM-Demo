package yevhent.demo.hibernate.configuration;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.sql.DataSource;

public class ArtSchoolPersistence {


    public static String getPersistenceUnitName() {
        return "art_school";
    }

    public static String getPersistenceProviderClassName() {
        return HibernatePersistenceProvider.class.getName();
    }

    public static PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    public static DataSource getNonJtaDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/hibernate");
        dataSource.setUsername(System.getenv("POSTGRESQL_USER_NAME"));
        dataSource.setPassword(System.getenv("POSTGRESQL_USER_PASSWORD"));
        return dataSource;
    }

}