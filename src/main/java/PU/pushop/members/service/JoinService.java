package PU.pushop.members.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinService {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Transactional
    public Long joinMember(Member member) {

        validateExistedMemberByEmail(member);

        Member newMember = Member.createGeneralMember(
                member.getEmail(),
                member.getUsername(),
                member.getNickname(),
                bCryptPasswordEncoder.encode(member.getPassword())
        );


        memberRepositoryV1.save(newMember);
        return newMember.getId();
    }

    private void validateExistedMemberByEmail(Member member) {
        boolean isExistMember = memberRepositoryV1.existsByEmail(member.getEmail());
        if (isExistMember) {
            throw new ExistingMemberException();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class ExistingMemberException extends IllegalStateException {
        public ExistingMemberException() {
            super("이미 존재하는 회원입니다.");
        }
    }
}
