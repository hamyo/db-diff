package dbdiff.domain.db;

import dbdiff.utils.Strings;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Table implements DatabaseObject {
    String tablespace;
    String scheme;
    String name;
    String desc;
    List<Column> columns = new ArrayList<>();
    List<Index> indiсes = new ArrayList<>();

    PrimaryKey primaryKey;
    List<ForeignKey> foreignKeys = new ArrayList<>();

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

    public void merge(Table other) {
        this.desc = Strings.concat(this.desc, other.desc);
        this.name = Strings.concat(this.name, other.name);
        this.tablespace = Strings.concat(this.tablespace, other.tablespace);
        this.scheme = Strings.concat(this.scheme, other.scheme);
    }

    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }
}
