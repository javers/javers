package org.javers.api;

import org.javers.core.Javers;
import org.javers.core.json.JsonConverter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author pawel szymczyk
 */
class SnapshotResponseMessageConverter extends AbstractHttpMessageConverter<SnapshotsResponse> {

    private final JsonConverter jsonConverter;

    public SnapshotResponseMessageConverter(Javers javers) {
        this(javers.getJsonConverter());
    }

    public SnapshotResponseMessageConverter(JsonConverter jsonConverter) {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8);
        this.jsonConverter = jsonConverter;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return SnapshotsResponse.class.equals(clazz);
    }

    @Override
    protected SnapshotsResponse readInternal(Class<? extends SnapshotsResponse> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return jsonConverter.fromJson(new InputStreamReader(inputMessage.getBody()), SnapshotsResponse.class);
    }

    @Override
    protected void writeInternal(SnapshotsResponse snapshotsResponse, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        String snapshotsResponseJson = jsonConverter.toJson(snapshotsResponse);
        outputMessage.getBody().write(snapshotsResponseJson.getBytes());
    }
}
