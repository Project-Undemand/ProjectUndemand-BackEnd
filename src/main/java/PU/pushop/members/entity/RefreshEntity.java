package PU.pushop.members.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "REFRESH")
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String refresh;

//    @Column(columnDefinition = "TIMESTAMP") // MySQL의 경우
    @Column(columnDefinition = "DATETIME") // H2Database의 경우
    private LocalDateTime expiration;
}
