package dbdiff.domain.db;

import lombok.Getter;

import java.util.List;

@Getter
public class Table implements DatabaseObject {
    String tablespace;
    String scheme;
    String name;
    String desc;
    List<Column> columns;
    PrimaryKey primaryKey;
    List<ForeignKey> foreignKeys;

    private Table() {}

    public static Table ofDesc(String desc, String tablespace) {
        Table table = new Table();
        table.tablespace = tablespace;
        table.desc = desc;
        return table;
    }

    public static Table ofName(String scheme, String name) {
        Table table = new Table();
        table.scheme = scheme;
        table.name = name;
        return table;
    }
}
