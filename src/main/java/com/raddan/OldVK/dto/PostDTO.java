package com.raddan.OldVK.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Optional;

public record PostDTO(@JsonProperty("content") String content,
                      @JsonProperty("mediaType") Optional<String> mediaType,
                      @JsonProperty("mediaUrl") Optional<String> mediaUrl
) {
    // for tests
    public JsonObject convertToJSON() {
        return Json.createObjectBuilder()
                .add("content", content())
                .add("mediaType", String.valueOf(mediaType()))
                .add("mediaUrl", String.valueOf(mediaUrl()))
                .build();
    }
}
