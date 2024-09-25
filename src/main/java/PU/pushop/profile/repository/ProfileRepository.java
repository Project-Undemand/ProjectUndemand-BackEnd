package PU.pushop.profile.repository;

import PU.pushop.profile.entity.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profiles, Long> {

    Optional<Profiles> findByMemberId(Long memberId);
}
