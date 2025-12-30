package common.mrp.media;

import java.util.Collections;
import java.util.List;

public class SqlWithParams {
    public final String sql;
    public final List<Object> params;

    public SqlWithParams(String sql, List<Object> params) {
        this.sql = sql;
        this.params = Collections.unmodifiableList(params);
    }
}
