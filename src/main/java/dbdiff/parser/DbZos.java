package dbdiff.parser;

import dbdiff.domain.db.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DbZos implements ModelParser {
    private static final Pattern TABLE_DESC_REG_EXP = Pattern.compile("^-{2}\\s*(?<desc>.+)\\s*:\\s*(?<tablespace>[A-Za-z0-9]+)$");
    private static final Pattern COLUMN_DESC_REG_EXP = Pattern.compile("^-{2}\\s*(?<desc>.+)\\s*$");
    private static final Pattern TABLE_REG_EXP = Pattern.compile("^CREATE\\s+TABLE\\s+(?<scheme>[A-Za-z_]+).(?<name>[A-Za-z_0-9]+)\\s*\\($", Pattern.CASE_INSENSITIVE);
    private static final Pattern INDEX_REG_EXP = Pattern.compile("^CREATE\\s+(?:UNIQUE)?\\s*INDEX\\s+(?<scheme>[A-Za-z_]+).(?<name>[A-Za-z_]+)\\s+ON\\s+(?<tablescheme>[A-Za-z_]+).(?<table>[A-Za-z_0-9]+)[ (]{1}.*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRIMARY_KEY_REG_EXP = Pattern.compile("^CONSTRAINT\\s+(?<name>[A-Za-z_]+)\\s+PRIMARY\\s+KEY\\s+\\((?<columns>[A-Za-z_, ]+)\\)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern FOREIGN_KEY_REG_EXP = Pattern.compile("^ALTER\\s+TABLE\\s+(?<scheme>[A-Za-z_]+).(?<tablename>[A-Za-z_0-9]+)\\s+ADD\\s+CONSTRAINT\\s+(?<name>[A-Za-z_0-9]+)\\s+FOREIGN\\s+KEY.*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern COLUMN_REG_EXP = Pattern.compile("^(?<name>[A-Za-z_]+)\\s+.*$");
    private static final Pattern SCHEMA_REG_EXP = Pattern.compile("^set\\s+current\\s+schema\\s+[A-Za-z_0-9]+\\s*;$");
    private static final Set<String> ENABLE_DATA_TYPES = new HashSet<>(Arrays.asList(
            // String data types
            "CLOB", "BLOB", "DBCLOB", "CHARACTER", "VARCHAR", "GRAPHIC", "VARGRAPHIC", "BINARY", "VARBINARY",
            // Numeric data types
            "SMALLINT", "INTEGER", "INT", "BIGINT", "DECIMAL", "DEC", "NUMERIC", "DECFLOAT", "REAL", "DOUBLE",
            // Date, time, and timestamp data types
            "DATE", "TIME", "TIMESTAMP",
            // Xml
            "XML"
    ));

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
     *
     * @param line
     * @return
     */
    Optional<DatabaseObject> handleOne(String line) {
        Matcher match = SCHEMA_REG_EXP.matcher(line);
        if (match.matches()) {
            return Optional.empty();
        }

        match = TABLE_DESC_REG_EXP.matcher(line);
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
            return handleColumn(line);
        }

        return Optional.empty();
    }

    private Optional<DatabaseObject> handleColumn(String line) {
        String[] parts = line.split("\\s+");
        // Column name and type not specified
        if (parts.length < 2) {
            return Optional.empty();
        }

        String name = parts[0].trim();
        StringBuilder type = new StringBuilder(parts[1]);
        if (parts[1].contains("(") && !parts[1].contains(")") && parts.length > 2) {
            for (int i = 2; i < parts.length; i++) {
                String part = parts[i];
                type.append(part);
                if (part.contains(")")) {
                    break;
                }
            }

        }

        if (type.lastIndexOf(",") == type.length() - 1) {
            type.setLength(type.length() - 1);
        }

        String fullType = type.toString();
        return isColumnTypeEnable(fullType) ? Optional.of(Column.ofName(name, fullType)) : Optional.empty();
    }

    private boolean isColumnTypeEnable(@NonNull String type) {
        int brPos = type.indexOf("(");
        String typeOnly = brPos == -1 ? type : type.substring(0, brPos);
        return ENABLE_DATA_TYPES.contains(typeOnly.toUpperCase());
    }
}
