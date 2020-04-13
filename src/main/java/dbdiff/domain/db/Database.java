package dbdiff.domain.db;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Database {
    private List<Table> tables = new ArrayList<>();
}
