package dbdiff.domain.db;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Index implements DatabaseObject {
    String scheme;
    String name;
}
