package com.hrusch.webapp.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hrusch.webapp.model.Track;
import com.hrusch.webapp.model.serialization.CustomDurationDeserializer;
import com.hrusch.webapp.model.serialization.CustomDurationSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeRequest {

    @NotNull
    private String userId;

    @NotNull
    private Track track;

    @NotNull
    @JsonDeserialize(using = CustomDurationDeserializer.class)
    @JsonSerialize(using = CustomDurationSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Duration time;
}
