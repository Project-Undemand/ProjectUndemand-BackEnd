package PU.pushop.members.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinService {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Transactional
    public Long joinMember(Member member) {

        validateExistedMember(member);

        Member newMember = Member.createNewMember(
                member.getEmail(),
                bCryptPasswordEncoder.encode(member.getPassword()),
                member.getUsername(),
                member.getNickname(),
                member.getRole());

        memberRepositoryV1.save(newMember);
        return newMember.getId();
    }

    private void validateExistedMember(Member member) {
        Boolean isExistMember = memberRepositoryV1.existsByEmail(member.getEmail());
        if (isExistMember) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
}
