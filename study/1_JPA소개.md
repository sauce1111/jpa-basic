# JPA 소개

### 패러다임 불일치

- 객체지향 언어 ←→ 관계형 DB 사이의 패러다임 불일치가 있음.
- 객체 CRUD 의 반복적인 코드
    - 필드 추가시 모든 SQL 수정 작업

→ 개발자가 SQL Mapper 처럼 일하게 됨

- 객체 = 상속 가능, RDB = 상속 없음
    - 슈퍼타입 서브타입 관계로 설계시 데이터 저장/조회가 상당히 복잡해 짐
    - JAVA Collection 의 경우 아주 간단하게 저장/조회 가능

    ```java
    // 저장
    list.add(album);
    // 조회
    list.get(albumId);
    // 상속관계이용 다형성 조회 가능
    Item item = list.get(albumId);
    ```


- 객체다운 모델링이 불가능

    ```java
    /*
    * 테이블에 맞춘 객체 모델링
    */
    class Member {
    	String id; //MEMBER_ID 컬럼 사용
    	Long teamId; //TEAM_ID FK 컬럼 사용 //**
    	String username; //USERNAME 컬럼 사용
    }
    
    class Team {
    	Long id; //TEAM_ID PK 사용
    	String name; //NAME 컬럼 사용
    }
    
    -> INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES …
       이렇게 저장하고 member와 team join 으로 조회하는 등...
    ```

    ```java
    /*
    * 객체다운 모델링
    */
    class Member {
    	String id; //MEMBER_ID 컬럼 사용
    	Team team; //참조로 연관관계를 맺는다. //**
    	String username;//USERNAME 컬럼 사용
    
    	Team getTeam() {
    		return team;
    	}
    }
    
    class Team {
    	Long id; //TEAM_ID PK 사용
    	String name; //NAME 컬럼 사용
    }
    
    -> member.getTeam().getId(); 을 사용해서 저장가능함
    ```


- 객체는 그래프 탐색이 자유롭다 → member.getOder().getItem()….
- 엔티티의 신뢰 문제, 계층분할이 어렵다

```java
class MemberService {
	...
	public void process() {
		// find() 메소드가 어디까지 조회하는지 확인이 필요함
		Member member = memberDAO.find(memberId); 
		member.getTeam(); // ??? null?
		member.getOrder().getDelivery(); // ???
	}
}
```

위와같은 문제… JAVA Collection 에 저장하듯 DB 에 저장할 수 는 없을까 ?

→ **JPA 등장**

---

## JPA 소개

- Java Persistence API
- 자바 진영의 **ORM** 기술 표준

### ORM 이란?

- Object-relational mapping(객체 관계 매핑)
- 객체는 객체대로 설계
- 관계형 데이터베이스는 관계형 데이터베이스대로 설계
- ORM 프레임워크가 중간에서 매핑
- 대중적인 언어에는 대부분 ORM 기술이 존재

---

JPA는 애플리케이션과 JDBC 사이에서 동작

![스크린샷 2025-03-19 오후 3.17.36.png](JPA%20%E1%84%89%E1%85%A9%E1%84%80%E1%85%A2%201bb987be962e80919114ef10f007785e/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-03-19_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_3.17.36.png)

JPA 의 동작 - 저장

![스크린샷 2025-03-19 오후 3.19.01.png](JPA%20%E1%84%89%E1%85%A9%E1%84%80%E1%85%A2%201bb987be962e80919114ef10f007785e/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-03-19_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_3.19.01.png)

JPA 의 동작 - 조회

![스크린샷 2025-03-19 오후 3.19.32.png](JPA%20%E1%84%89%E1%85%A9%E1%84%80%E1%85%A2%201bb987be962e80919114ef10f007785e/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-03-19_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_3.19.32.png)

### JPA를 왜 사용해야 하는가?

- SQL 중심적인 개발에서 객체 중심으로 개발
- 생산성
- 유지보수
- 패러다임의 불일치 해결
- 성능
- 데이터 접근 추상화와 벤더 독립성
- 표준

JPA의 성능 최적화 기능

1. 1차 캐시와 동일성(identity) 보장

```java
1. 같은 트랜잭션 안에서는 같은 엔티티를 반환 - 약간의 조회 성능 향상
2. DB Isolation Level이 Read Commit이어도 애플리케이션에서 Repeatable Read 보장

String memberId = "100";
Member m1 = jpa.find(Member.class, memberId); //SQL
Member m2 = jpa.find(Member.class, memberId); //캐시

println(m1 == m2) //true

SQL 1번만 실행
```

1. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)

```java
1. 트랜잭션을 커밋할 때까지 INSERT SQL을 모음
2. JDBC BATCH SQL 기능을 사용해서 한번에 SQL 전송

transaction.begin(); // [트랜잭션] 시작
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
//커밋하는 순간 데이터베이스에 INSERT SQL을 모아서 보낸다.
transaction.commit(); // [트랜잭션] 커밋

1. UPDATE, DELETE로 인한 로우(ROW)락 시간 최소화
2. 트랜잭션 커밋 시 UPDATE, DELETE SQL 실행하고, 바로 커밋

transaction.begin(); // [트랜잭션] 시작
changeMember(memberA);
deleteMember(memberB);
비즈니스 로직 수행(); //비즈니스 로직 수행 동안 DB 로우 락이 걸리지 않는다.
//커밋하는 순간 데이터베이스에 UPDATE, DELETE SQL을 보낸다.
transaction.commit(); // [트랜잭션] 커밋
```

1. 지연 로딩(Lazy Loading)
    1. 지연 로딩: 객체가 실제 사용될 때 로딩
    2. 즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회