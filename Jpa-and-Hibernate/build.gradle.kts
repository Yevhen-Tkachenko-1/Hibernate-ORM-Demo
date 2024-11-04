plugins {
    id("java")
}

group = "yevhent.demo.hibernate.jpa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core
    implementation("org.hibernate.orm:hibernate-core:6.6.1.Final")

    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.4")

}

tasks.test {
    useJUnitPlatform()
}