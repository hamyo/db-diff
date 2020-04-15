package dbdiff.report;

import dbdiff.domain.diff.Difference;

import java.util.List;

public interface ReportCreater {
    void createAndSave(List<Difference> diff);
}
