package dbdiff.service;

import dbdiff.domain.conf.Config;
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
    private final DbFormer dbFormer;
    private final Config config;

    private void checkFileExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(MessageFormat.format("File {0} not exists", path));
        }
    }

    private void check() {
        checkFileExists(config.getModels().getOld());
        checkFileExists(config.getModels().getCurrent());
    }

    public void run() {
        check();
        Database old = formDb(config.getModels().getOld());
        Database current = formDb(config.getModels().getCurrent());
    }

    @SneakyThrows
    private Database formDb(String path) {
        List<DatabaseObject> objects = parser.parse(Files.lines(Paths.get(path), StandardCharsets.UTF_8));
        return dbFormer.form(objects);
    }

    private void compare(Database old, Database current) {

    }

}
