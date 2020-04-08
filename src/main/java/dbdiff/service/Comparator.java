package dbdiff.service;

import dbdiff.domain.db.Database;
import dbdiff.domain.db.DatabaseObject;
import dbdiff.parser.ModelParser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;

@AllArgsConstructor
public class Comparator {
    private final ModelParser parser;

    private static final String OLD_MODEL_NAME = "old.ddl";
    private static final String NEW_MODEL_NAME = "current.ddl";

    private void checkFileExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(MessageFormat.format("File {0} not exists", path));
        }
    }

    private void check() {
        checkFileExists(OLD_MODEL_NAME);
        checkFileExists(NEW_MODEL_NAME);
    }

    public void run() {
        check();
        Database old = formDb(OLD_MODEL_NAME);
        Database current = formDb(NEW_MODEL_NAME);
    }

    @SneakyThrows
    private Database formDb(String path) {
        List<DatabaseObject> objects = parser.parse(Files.lines(Paths.get(path), StandardCharsets.UTF_8));
        return new DbFormer(objects).form();
    }

    private void compare(Database old, Database current) {

    }

}
