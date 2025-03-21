package hellojpa;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@SequenceGenerator(name = "member_seq_generator", sequenceName = "member_seq")
public class Member {

    public Member() {
    }

    public Member(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String username;

    @Column
    private BigDecimal age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date laseModifiedDate;

    @Lob
    private String description;

    @Transient
    private int temp;
}
