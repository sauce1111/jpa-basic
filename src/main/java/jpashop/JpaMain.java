package jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpashop.domain.Address;
import jpashop.domain.Book;
import jpashop.domain.Member;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Member member = new Member();
            member.setName("member1");
            member.setHomeAddress(new Address("homeCity", "street", "100000"));

            member.getFavoriteFoods().add("김찌");
            member.getFavoriteFoods().add("된찌");
            member.getFavoriteFoods().add("삼겹살");

            member.getAddressHistory().add(new Address("oldCity1", "street1", "100000"));
            member.getAddressHistory().add(new Address("oldCity1", "street2", "100000"));

            em.persist(member);

            em.flush();
            em.close();

            Member findMember = em.find(Member.class, member.getId());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
