package common;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class Mapper {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Mapper() {
    }
}
