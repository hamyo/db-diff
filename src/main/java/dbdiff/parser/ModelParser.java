package dbdiff.parser;

import dbdiff.domain.db.DatabaseObject;

import java.util.List;
import java.util.stream.Stream;

public interface ModelParser {
    List<DatabaseObject> parse(Stream<String> lines);
}
