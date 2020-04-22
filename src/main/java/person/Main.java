package person;

import com.github.javafaker.Faker;
import legoset.model.LegoSet;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.ZoneId;
import java.util.List;

@Log4j2
public class Main {

    private static Faker faker = new Faker();
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-example");

    private static Person randomPerson() {
        Person person = Person.builder()
                .name(faker.name().fullName())
                .dob(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .gender(faker.options().option(Person.Gender.class))
                .address(Address.builder()
                        .city(faker.address().city())
                        .streetAddress(faker.address().streetAddress())
                        .country(faker.address().country())
                        .state(faker.address().state())
                        .zip(faker.address().zipCode())
                        .build())
                .email(faker.internet().emailAddress())
                .profession(faker.company().profession())
                .build();
        return person;
    }

    public static void main(String[] args) {

        EntityManager em = emf.createEntityManager();

        int n;

        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        } else {
            n = 1000;
        }

        em.getTransaction().begin();
        try {
            for (int i = 0; i < n; i ++) {
                em.persist(randomPerson());
            }
            em.getTransaction().commit();
            
            List<Person> pList = em.createQuery("SELECT p FROM Person p ORDER BY p.id", Person.class).getResultList();;
            pList.forEach(log::info);
        } finally {
            em.close();
            emf.close();
        }
    }
}
