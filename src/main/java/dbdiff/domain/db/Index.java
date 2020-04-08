package dbdiff.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Index implements DatabaseObject {
    String scheme;
    String name;
}
