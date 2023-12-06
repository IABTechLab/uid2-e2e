package helper;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JsonAssert {
    private JsonAssert() {
    }

    public static boolean hasTopLevelFields(JsonNode jsonNode, List<String> fieldNames, boolean isStrict) {
        final List<String> actualFieldNames = new ArrayList<>();
        final Iterator<String> iterator = jsonNode.fieldNames();
        iterator.forEachRemaining(actualFieldNames::add);

        if (isStrict) {
            return actualFieldNames.size() == fieldNames.size()
                    && actualFieldNames.containsAll(fieldNames);
        } else {
            return actualFieldNames.containsAll(fieldNames);
        }
    }

    public static boolean hasContentInFields(JsonNode jsonNode, List<String> jsonPtrExprs) {
        for (String jsonPtrExpr : jsonPtrExprs) {
            JsonNode node = jsonNode.at(jsonPtrExpr);
            String nodeText = node.asText();
            if (nodeText == null || nodeText.isBlank()) {
                return false;
            }
        }
        return true;
    }
}
