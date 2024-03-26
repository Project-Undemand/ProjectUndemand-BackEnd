package PU.pushop.members.repository;

import PU.pushop.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepositoryV1 extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    // username을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    Member findByUsername(String username);

    Member findByNickname(String nickname);

    Boolean existsByEmail(String email);
}
