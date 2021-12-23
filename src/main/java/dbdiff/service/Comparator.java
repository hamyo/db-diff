package dbdiff.service;

import dbdiff.domain.conf.Config;
import dbdiff.domain.db.*;
import dbdiff.domain.diff.Difference;
import dbdiff.parser.ModelParser;
import dbdiff.report.ReportCreater;
import dbdiff.saver.ReportSaver;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
@Slf4j
public class Comparator {
    private final ModelParser parser;
    private final DbFormer dbFormer;
    private final ReportCreater reportCreater;
    private List<ReportSaver> savers;
    private final Config config;

    public void run() {
        Database old = formDb(config.getModels().getOld());
        Database current = formDb(config.getModels().getCurrent());
        List<Difference> diff = compare(old, current);
        byte[] report = reportCreater.create(diff);
        savers.forEach(saver -> saver.save(report));
    }

    @SneakyThrows
    private Database formDb(String path) {
        log.info("form database structure started {}", path);
        List<DatabaseObject> objects = parser.parse(Files.lines(Paths.get(path), StandardCharsets.UTF_8));
        Database db = dbFormer.form(objects);
        log.info("form database structure finished {}", path);
        return db;
    }

    List<Difference> compare(Database old, Database current) {
        log.info("Comparing for two models started");
        Map<String, Table> oldTables = old.getTables().stream()
                .collect(toMap(Table::getName, Function.identity()));
        List<Difference> differences = current.getTables().stream()
                .map(curTable -> {
                    Table oldTable = oldTables.get(curTable.getName());
                    if (oldTable == null) {
                        return Difference.ofNew(curTable);
                    } else {
                        Optional<Table> difTable = compareTable(oldTable, curTable);
                        return difTable.isEmpty() ? null : Difference.ofEdit(difTable.get());
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        log.info("Comparing for two models finished");
        return differences;
    }

    private Optional<Table> compareTable(Table oldTable, Table curTable) {
        log.debug("Table ({}) comparing started", curTable.getName());
        Table diff = Table.of(oldTable);

        diff.getIndices().addAll(compareIndices(oldTable.getIndices(), curTable.getIndices()));
        diff.getForeignKeys().addAll(compareForeignKeys(oldTable.getForeignKeys(), curTable.getForeignKeys()));
        diff.getColumns().addAll(compareColumns(oldTable.getColumns(), curTable.getColumns()));

        log.debug("Table ({}) comparing finished", curTable.getName());
        return diff.isEmpty() ? Optional.empty() : Optional.of(diff);
    }

    private List<Index> compareIndices(List<Index> oldIndices, List<Index> curIndices) {
        Set<Index> oldIndicesSet = new HashSet<>(oldIndices);
        return curIndices.stream()
                .filter(index -> !oldIndicesSet.contains(index))
                .collect(Collectors.toList());
    }

    private List<ForeignKey> compareForeignKeys(List<ForeignKey> oldKeys, List<ForeignKey> curKeys) {
        Set<String> oldKeyNames = oldKeys.stream()
                .map(Constraint::getName)
                .collect(Collectors.toSet());
        return curKeys.stream()
                .filter(key -> !oldKeyNames.contains(key.getName()))
                .collect(Collectors.toList());
    }

    private List<Column> compareColumns(List<Column> oldColumns, List<Column> curColumns) {
        Set<String> oldColumnNames = oldColumns.stream()
                .map(Column::getName)
                .collect(Collectors.toSet());

        return curColumns.stream()
                .filter(column -> !oldColumnNames.contains(column.getName()))
                .collect(Collectors.toList());
    }

}
