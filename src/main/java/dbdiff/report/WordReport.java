package dbdiff.report;

import dbdiff.domain.db.Column;
import dbdiff.domain.db.Constraint;
import dbdiff.domain.db.Index;
import dbdiff.domain.db.Table;
import dbdiff.domain.diff.Difference;
import dbdiff.domain.diff.StateChange;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WordReport implements ReportCreater {
    private final String filePath;
    private final static String CREATED_TABLE_TEXT = "Новые таблицы:";
    private final static String EDITED_TABLE_TEXT = "Измененные таблицы:";

    @SneakyThrows
    @Override
    public void createAndSave(List<Difference> diff) {
        XWPFDocument report = new XWPFDocument();
        List<Difference> differences = diff.stream()
                .filter(one -> StateChange.CREATION == one.getState())
                .collect(Collectors.toList());
        handleDifferences(differences, report, CREATED_TABLE_TEXT);

        differences = diff.stream()
                .filter(one -> StateChange.EDITING == one.getState())
                .collect(Collectors.toList());
        handleDifferences(differences, report, EDITED_TABLE_TEXT);

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            report.write(out);
        }
    }

    private void handleDifferences(List<Difference> diff, XWPFDocument report, String title) {
        if (!diff.isEmpty()) {
            XWPFParagraph paragraph = report.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(title);
            diff.forEach(difference -> handleTable(difference.getTable(), report));
        }
    }

    private void handleTable(Table table, XWPFDocument report) {
        createTableDesc(table, report);
        createColumnsTable(table.getColumns(), report);
        createIndicesDesc(table.getIndices(), report);
        createConstraintsDesc(table.getConstraints(), report);
    }

    private void createIndicesDesc(List<Index> indices, XWPFDocument report) {
        if (!indices.isEmpty()) {
            XWPFParagraph paragraph = report.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Индексы");

            CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
            cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));
            CTLvl cTLvl = cTAbstractNum.addNewLvl();
            cTLvl.addNewNumFmt().setVal(STNumberFormat.BULLET);

            XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
            XWPFNumbering numbering = report.createNumbering();
            BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
            BigInteger numId = numbering.addNum(abstractNumID);
            indices.forEach(index -> {
                XWPFParagraph bulletList = report.createParagraph();
                XWPFRun item = bulletList.createRun();
                //item.setFontFamily(ARIAL);
                //item.setFontSize(10);
                item.setText(MessageFormat.format("{0}.{1}", index.getScheme(), index.getName()));
                bulletList.setNumID(numId);
            });
            /*XWPFNumbering numbering = report.createNumbering();
            BigInteger abstractNumId = BigInteger.valueOf(0);
            numbering.addNum(abstractNumId;
            */
        }
    }

    private void createConstraintsDesc(List<Constraint> constraints, XWPFDocument report) {
        if (!constraints.isEmpty()) {
            XWPFParagraph paragraph = report.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Ограничения:");

            CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
            cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));
            CTLvl cTLvl = cTAbstractNum.addNewLvl();
            cTLvl.addNewNumFmt().setVal(STNumberFormat.BULLET);

            XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
            XWPFNumbering numbering = report.createNumbering();
            BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
            BigInteger numId = numbering.addNum(abstractNumID);
            constraints.forEach(constraint -> {
                XWPFParagraph bulletList = report.createParagraph();
                XWPFRun item = bulletList.createRun();
                item.setText(MessageFormat.format("{0} - {1}", constraint.getName(), constraint.getTypeName()));
                bulletList.setNumID(numId);
            });
        }
    }

    private void createColumnsTable(List<Column> columns, XWPFDocument report) {
        if (!columns.isEmpty()) {
            XWPFTable docTable = report.createTable();
            createColumnsHeader(docTable);
            columns.forEach(column -> {
                XWPFTableRow row = docTable.createRow();
                row.getCell(0).setText(column.getName());
                row.getCell(1).setText(column.getType());
                row.getCell(2).setText(column.getDesc());
            });
        }
    }

    private void createTableDesc(Table table, XWPFDocument report) {
        XWPFParagraph paragraph = report.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(MessageFormat.format("{0}.{1} ({2})", table.getScheme(), table.getName(), table.getDesc()));
    }

    private void createColumnsHeader(XWPFTable table) {
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("Имя поля");
        header.addNewTableCell().setText("Тип");
        header.addNewTableCell().setText("Описание");
    }
}
