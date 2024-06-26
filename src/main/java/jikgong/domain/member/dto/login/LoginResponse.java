package jikgong.domain.member.dto.login;

import jikgong.domain.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Role role;
}
