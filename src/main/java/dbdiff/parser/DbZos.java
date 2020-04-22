package dbdiff.parser;
import dbdiff.domain.db.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DbZos implements ModelParser {
    private static final Pattern TABLE_DESC_REG_EXP = Pattern.compile("^-{2}\\s*(?<desc>.+)\\s*:\\s*(?<tablespace>[A-Za-z]+)$");
    private static final Pattern COLUMN_DESC_REG_EXP = Pattern.compile("^-{2}\\s*(?<desc>.+)\\s*$");
    private static final Pattern TABLE_REG_EXP = Pattern.compile("^CREATE\\s+TABLE\\s+(?<scheme>[A-Za-z_]+).(?<name>[A-Za-z_0-9]+)\\s*\\($", Pattern.CASE_INSENSITIVE);
    private static final Pattern INDEX_REG_EXP = Pattern.compile("^CREATE\\s+(?:UNIQUE)?\\s*INDEX\\s+(?<scheme>[A-Za-z_]+).(?<name>[A-Za-z_]+)\\s+ON\\s+(?<tablescheme>[A-Za-z_]+).(?<table>[A-Za-z_0-9]+)\\s+.*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRIMARY_KEY_REG_EXP = Pattern.compile("^CONSTRAINT\\s+(?<name>[A-Za-z_]+)\\s+PRIMARY\\s+KEY\\s+\\((?<columns>[A-Za-z_, ]+)\\)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern FOREIGN_KEY_REG_EXP = Pattern.compile("^ALTER\\s+TABLE\\s+(?<scheme>[A-Za-z_]+).(?<tablename>[A-Za-z_0-9]+)\\s+ADD\\s+CONSTRAINT\\s+(?<name>[A-Za-z_0-9]+)\\s+FOREIGN\\s+KEY.*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern COLUMN_REG_EXP = Pattern.compile("^(?<name>[A-Za-z_]+)\\s+(?<type>[A-Za-z_0-9]+\\(?[0-9, ]*\\)?).*,{1}$");


    public List<DatabaseObject> parse(Stream<String> lines) {
        return lines.map(String::trim)
                .filter(str -> !str.isEmpty())
                .map(this::handleOne)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Match's order is important
     * @param line
     * @return
     */
    Optional<DatabaseObject> handleOne(String line) {
        Matcher match = TABLE_DESC_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(Table.ofDesc(match.group("desc").trim(), match.group("tablespace").trim()));
        }

        match = TABLE_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(Table.ofName(match.group("scheme").trim(), match.group("name").trim()));
        }

        match = FOREIGN_KEY_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(new ForeignKey(match.group("name").trim()));
        }

        match = INDEX_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(new Index(match.group("scheme").trim(), match.group("name").trim()));
        }

        match = PRIMARY_KEY_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(new PrimaryKey(match.group("name").trim()));
        }

        match = COLUMN_DESC_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(Column.ofDesc(match.group("desc").trim()));
        }

        match = COLUMN_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.of(Column.ofName(match.group("name").trim(), match.group("type").trim()));
        }

        return Optional.empty();
    }
}
