package dbdiff.service;
import dbdiff.domain.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DbZosParser implements ModelParser {
    private static final Pattern TABLE_DESC_REG_EXP = Pattern.compile("^-{2}\\s*(?<desc>[\\w|[ЁёА-я]|\\s]+)\\s*:\\s*(?<tablespace>[A-Za-z]+)$");
    private static final Pattern COLUMN_DESC_REG_EXP = Pattern.compile("^-{2}\\s*(?<desc>[\\w|[ЁёА-я]|\\s]+)\\s*$");
    private static final Pattern TABLE_REG_EXP = Pattern.compile("^CREATE\\s+TABLE\\s+(?<scheme>[A-Za-z_]+).(?<name>[A-Za-z_0-9]+)\\s*\\($", Pattern.CASE_INSENSITIVE);
    private static final Pattern INDEX_REG_EXP = Pattern.compile("^CREATE\\s+(?:UNIQUE)?\\s*INDEX\\s+(?<scheme>[A-Za-z_]+).(?<name>[A-Za-z_]+)\\s+ON\\s+(?<tablescheme>[A-Za-z_]+).(?<table>[A-Za-z_0-9]+)\\s+.*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRIMARY_KEY_REG_EXP = Pattern.compile("^CONSTRAINT\\s+(?<name>[A-Za-z_]+)\\s+PRIMARY\\s+KEY\\s+\\((?<columns>[A-Za-z_, ]+)\\)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern FOREIGN_KEY_REG_EXP = Pattern.compile("^ALTER\\s+TABLE\\s+(?<scheme>[A-Za-z_]+).(?<tablename>[A-Za-z_0-9]+)\\s+ADD\\s+CONSTRAINT\\s+(?<name>[A-Za-z_0-9]+)\\s+FOREIGN\\s+KEY.*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern COLUMN_REG_EXP = Pattern.compile("^(?<name>[A-Za-z_]+)\\s+(?<type>[A-Za-z_0-9()]+)\\s+.*$");

    private void check(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(MessageFormat.format("File {0} not exists", path));
        }
    }

    @SneakyThrows
    public Database parse(String path) {
        check(path);
        Files.lines(Paths.get(path), StandardCharsets.UTF_8)
                .map(String::trim)
                .filter(str -> !str.isEmpty());

        return new Database();
    }

    private Optional<DatabaseObject> handleOne(String line) {
        Matcher match = COLUMN_DESC_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(Column.ofDesc(match.group("desc")));
        }

        match = COLUMN_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(Column.ofName(match.group("name"), match.group("type")));
        }

        match = TABLE_DESC_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(Table.ofDesc(match.group("desc"), match.group("tablespace")));
        }

        match = TABLE_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(Table.ofName(match.group("desc"), match.group("tablespace")));
        }

        match = FOREIGN_KEY_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(new ForeignKey(match.group("name")));
        }

        match = INDEX_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(new Index(match.group("scheme"), match.group("name")));
        }

        match = PRIMARY_KEY_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(new PrimaryKey(match.group("name")));
        }

        return Optional.empty();
    }
}
