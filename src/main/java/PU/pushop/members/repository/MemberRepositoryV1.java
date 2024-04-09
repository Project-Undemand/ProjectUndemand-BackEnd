package PU.pushop.members.repository;

import PU.pushop.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepositoryV1 extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    // username을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    Optional<Member> findByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findBySocialId(String socialId);

    Optional<Member> findByToken(String token);

}
