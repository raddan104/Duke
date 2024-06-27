package com.raddan.OldVK.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.json.Json;
import javax.json.JsonObject;

public record AuthDTO(@JsonProperty("username") String username, @JsonProperty("password") String password) {

    // for tests
    public JsonObject convertToJSON() {
        return Json.createObjectBuilder()
                .add("username", username())
                .add("password", password())
                .build();
    }
}
