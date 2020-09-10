package dbdiff.domain.db;

import dbdiff.utils.Strings;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Table implements DatabaseObject {
    String tablespace;
    String scheme;
    String name;
    String desc;
    @ToString.Exclude
    List<Column> columns = new ArrayList<>();
    @ToString.Exclude
    List<Index> indices = new ArrayList<>();

    PrimaryKey primaryKey;
    List<ForeignKey> foreignKeys = new ArrayList<>();

    private Table() {}

    public static Table of(Table table) {
        Table res = new Table();
        res.tablespace = table.tablespace;
        res.desc = table.desc;
        res.name = table.name;
        res.scheme = table.scheme;
        return res;
    }

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

    public List<Constraint> getConstraints() {
        List<Constraint> constraints = new ArrayList<>(foreignKeys);
        if (primaryKey != null) {
            constraints.add(primaryKey);
        }

        return constraints;
    }

    public boolean isEmpty() {
        return indices.isEmpty() && foreignKeys.isEmpty() && columns.isEmpty();
    }
}
