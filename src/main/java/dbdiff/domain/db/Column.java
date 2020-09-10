package dbdiff.domain.db;

import dbdiff.utils.Strings;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Column implements DatabaseObject {
    private String name;
    private String type;
    private String desc;

    private Column() {}

    public static Column ofName(String name, String type) {
        Column column = new Column();
        column.name = name;
        column.type = type;
        return column;
    }

    public static Column ofDesc(String desc) {
        Column column = new Column();
        column.desc = desc;
        return column;
    }

    public void merge(Column other) {
        this.desc = Strings.concat(this.desc, other.desc);
        this.name = Strings.concat(this.name, other.name);
        this.type = Strings.concat(this.type, other.type);
    }

}
