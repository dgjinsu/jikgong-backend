package jikgong.global.security.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.security.principal.MemberDto;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Cacheable(cacheNames = "loginMember", key = "#loginId", condition = "#loginId != null", cacheManager = "authCacheManager")
    public PrincipalDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        log.info("loadUserByUsername 실행");
        Member findMember = memberRepository.findByLoginId(loginId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        MemberDto memberDto = MemberDto.builder()
            .id(findMember.getId())
            .role(findMember.getRole())
            .build();
        return new PrincipalDetails(memberDto);
    }
}
