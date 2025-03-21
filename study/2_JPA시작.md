> 예제 코드를 작성하며 JPA 의 동작 과 사용에 대해 알아본다.
>

### 데이터베이스 방언

- JPA는 특정 데이터베이스에 종속 X
- 각각의 데이터베이스가 제공하는 SQL 문법과 함수는 조금씩 다름
- 가변 문자: MySQL은 VARCHAR, Oracle은 VARCHAR2
- 문자열을 자르는 함수: SQL 표준은 SUBSTRING(), Oracle은 SUBSTR()
- 페이징: MySQL은 LIMIT , Oracle은 ROWNUM
- 방언: SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능

![스크린샷 2025-03-19 오후 4.46.52.png](attachment:6c6d9502-7256-4745-b8bb-00791cb004f2:스크린샷_2025-03-19_오후_4.46.52.png)

### JPA 의 구동방식

![스크린샷 2025-03-19 오후 4.48.14.png](attachment:ad8f539b-0a88-4610-9740-16756602de83:스크린샷_2025-03-19_오후_4.48.14.png)

### 실습 코드

객체와 테이블 생성 및 매핑

```java
@Entity -> JPA 가 관리할 객체
public class Member {

    @Id -> PK 와 매핑
    private Long id;
    private String name;
		...
}
```

CRUD 실습

```java
public class JpaMain {
    public static void main(String[] args) {
		    // EntityManagerFactory 생성, persistence-unit name 속성으로 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // 요청 당 하나씩 생성하여 사용후 반납하는 EntityManager
        EntityManager em = emf.createEntityManager();
        // JPA 의 모든 데이터 변경은 트랜잭션 안에서 이루어 진다.
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // insert
//            Member member = new Member();
//            member.setId(2L);
//            member.setName("helloB");
//            em.persist(member);

            // select
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember id : " + findMember.getId());
//            System.out.println("findMember name : " + findMember.getName());

            // update -> 더티 체킹
//            Member findMember = em.find(Member.class, 1L);
//            findMember.setName("helloJPA");

            // condition select -> JPQL
            List<Member> members = em.createQuery("select m from Member as m", Member.class)
										// Mysql, Oracle 등 밴더에 종속적이지 않음 Dialect 에 따라 맞춰서 SQL 작성해줌
                    .setFirstResult(5)
                    .setMaxResults(8)
                    .getResultList();

            for (Member member : members) {
                System.out.println("member.name = " + member.getName());
            }

				// 커밋, 롤백, EntityManager 반납 등 반드시 수행 (Spring 이 해준다...)
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
```

*****주의 할 점*****

- **엔티티 매니저 팩토리**는 하나만 생성해서 애플리케이션 전체에서 공유
- **엔티티 매니저**는 쓰레드간에 공유X (사용하고 버려야 한다).
- **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**

### JPQL

- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
- SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
- **JPQL은 엔티티 객체**를 대상으로 쿼리
- **SQL은 데이터베이스 테이블**을 대상으로 쿼리

- 테이블이 아닌 **객체를 대상으로 검색하는 객체 지향 쿼리**
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존 X
- JPQL을 한마디로 정의하면 객체 지향 SQL