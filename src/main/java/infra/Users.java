package infra;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true)
    private String email;

    @Column
    private String name;
    @Column
    private String birthDay;

    @Column(columnDefinition = "TEXT")
    private String passwordHash;

    @Column(unique = true ,nullable = false, length = 50)
    private String nickname;

    @Column
    private String profileImgUrl;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Builder
    public Users(String email, String passwordHash,String name,String birthDay){
        this.email=email;
        this.passwordHash=passwordHash;
        this.name=name;
        this.birthDay=birthDay;
    }
}
