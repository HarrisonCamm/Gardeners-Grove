package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long authorityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column()
    private String role;

    protected Authority() {
        // JPA empty constructor
    }
    public Authority(String role) {

        this.role = role;
    }

    public Authority(Long authorityId, User user, String role) {

        this.role = role;
        this.authorityId = authorityId;
        this.user = user;
    }


    public Long getAuthorityId() {return authorityId;}

    public String getRole() {
        return role;
    }

    public User getUser() { return user;}

    @Override
    public String toString() {
        return String.format(
                "Authority[authorityId=%d, role='%s', user=%s]",
                authorityId, role, user);
    }

}