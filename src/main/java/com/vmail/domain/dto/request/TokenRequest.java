package com.vmail.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class TokenRequest {

    String oldToken;

    @JsonCreator
    public TokenRequest(@JsonProperty("oldToken") String oldToken) {
        this.oldToken = oldToken;
    }

}
