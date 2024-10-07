package com.vmail.domain.dto.response;

import lombok.Value;

@Value
public class AuthenticationResponse {

    String refreshToken;

    String accessToken;

}
