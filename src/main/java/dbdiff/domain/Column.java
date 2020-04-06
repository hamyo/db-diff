package dbdiff.domain;

import lombok.Data;
import lombok.Getter;

@Getter
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

}
