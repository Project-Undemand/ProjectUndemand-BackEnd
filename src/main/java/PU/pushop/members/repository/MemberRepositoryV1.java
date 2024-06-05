package PU.pushop.members.repository;

import PU.pushop.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepositoryV1 extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    // username을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    Optional<Member> findByUsername(String username);

    Boolean existsByEmail(String email);
    // 식별자를 Email 에서 Social Id 로 변경함으로써, email 은 2개이상의 계정으로 중복될 수 있음.
    Optional<Member> findByEmail(String email);

    List<Member> findAllByEmail(String email);

    Optional<Member> findBySocialId(String socialId);

    Optional<Member> findByToken(String token);

}
