package dbdiff.service;

import dbdiff.domain.AppException;
import dbdiff.domain.UndefinedBehaviorException;
import dbdiff.domain.db.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class DbFormer {
    public Database form(List<DatabaseObject> dbObjects) {
        Database db = new Database();
        for(DatabaseObject dbObject : dbObjects) {
            handleOne(dbObject, db);
        }
        return db;
    }

    private void handleOne(DatabaseObject dbObject, Database db) {
        if (dbObject instanceof Column) {
            handleColumn((Column) dbObject, db);
        } else if (dbObject instanceof Table) {
            handleTable((Table) dbObject, db);
        } else if (dbObject instanceof Index) {
            handleIndex((Index) dbObject, db);
        } else if (dbObject instanceof ForeignKey) {
            handleForeignKey((ForeignKey) dbObject, db);
        } else if (dbObject instanceof PrimaryKey) {
            handlePrimaryKey((PrimaryKey) dbObject, db);
        }
    }

    private void handleForeignKey(ForeignKey fKey, Database db) {
        getLastTable(db).ifPresentOrElse(lastTable -> lastTable.getForeignKeys().add(fKey),
                () -> { throw new UndefinedBehaviorException(fKey.toString()); });
    }

    private void handlePrimaryKey(PrimaryKey pKey, Database db) {
        getLastTable(db).ifPresentOrElse(lastTable -> {
                    if (lastTable.getPrimaryKey() != null) {
                        throw new UndefinedBehaviorException(pKey.toString());
                    } else {
                        lastTable.setPrimaryKey(pKey);
                    }
                },
                () -> { throw new UndefinedBehaviorException(pKey.toString()); });
    }

    private void handleIndex(Index index, Database db) {
        getLastTable(db).ifPresentOrElse(lastTable -> lastTable.getIndiсes().add(index),
                () -> { throw new UndefinedBehaviorException(index.toString()); });
    }

    private void handleTable(Table table, Database db) {
        getLastTable(db).ifPresentOrElse(lastTable -> {
            if (StringUtils.isNotEmpty(lastTable.getName())) {
                db.getTables().add(table);
            } else {
                lastTable.merge(table);
            }
        }, () -> db.getTables().add(table));
    }

    private void handleColumn(Column column, Database db) {
        getLastColumn(db).ifPresentOrElse(lastColumn -> {
                    if (StringUtils.isNotEmpty(lastColumn.getName())) {
                        getLastTable(db).ifPresent(table -> table.getColumns().add(column));
                    } else {
                        lastColumn.merge(column);
                    }
                }, () -> getLastTable(db).ifPresent(table -> table.getColumns().add(column))
        );
    }

    private Optional<Table> getLastTable(Database db) {
        return db.getTables().isEmpty() ? Optional.empty() : Optional.of(db.getTables().get(db.getTables().size() - 1));
    }

    private Optional<Column> getLastColumn(Database db) {
        Optional<Table> lastTable = getLastTable(db);
        if (lastTable.isEmpty()) {
            throw new AppException("There are no tables in database");
        }
        List<Column> columns = lastTable.get().getColumns();
        return columns.isEmpty() ? Optional.empty() : Optional.of(columns.get(columns.size() - 1));
    }
}
