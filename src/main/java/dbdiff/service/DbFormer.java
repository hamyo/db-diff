package dbdiff.service;

import dbdiff.domain.db.Database;
import dbdiff.domain.db.DatabaseObject;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DbFormer {
    private List<DatabaseObject> objects;
    public Database form() {
        return new Database();
    }
}
