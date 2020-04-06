package dbdiff.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Index implements DatabaseObject {
    String scheme;
    String name;
}
