package com.jwt.auth.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Response")
public class ErrorJsonResponse {

    private String appResponseCode;
    private String appMessageCode;
    private String description;

    public ErrorJsonResponse() {
    }

    public ErrorJsonResponse(String appResponseCode, String appMessageCode, String description) {
        this.appResponseCode = appResponseCode;
        this.appMessageCode = appMessageCode;
        this.description = description;
    }

    public ErrorJsonResponse(ErrorJsonResponse msgResponse) {
        this.appResponseCode = msgResponse.appResponseCode;
        this.appMessageCode = msgResponse.appMessageCode;
        this.description = msgResponse.description;
    }


}
