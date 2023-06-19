package com.jwt.auth.model.json.response;

import lombok.Data;

@Data
public class JsonResponse {

    private String appResponseCode;
    private String appMessageCode;
    private String description;

    public JsonResponse(String appResponseCode, String appMessageCode, String description) {
        this.appResponseCode = appResponseCode;
        this.appMessageCode = appMessageCode;
        this.description = description;
    }

    public JsonResponse(JsonResponse msgResponse) {
        this.appResponseCode = msgResponse.appResponseCode;
        this.appMessageCode = msgResponse.appMessageCode;
        this.description = msgResponse.description;
    }


}