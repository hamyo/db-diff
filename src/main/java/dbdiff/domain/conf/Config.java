package dbdiff.domain.conf;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Config {
    private Models models = new Models();
    private String reportPath;

    public Config copy() {
        Config res = new Config();
        res.getModels().setOld(this.getModels().getOld());
        res.getModels().setCurrent(this.getModels().getCurrent());
        res.reportPath = this.reportPath;
        return res;
    }

    public Config merge(Config conf) {
        Config res = this.copy();
        if (conf.getModels().getOld() != null) {
            res.getModels().setOld(conf.getModels().getOld());
        }

        if (conf.getModels().getCurrent() != null) {
            res.getModels().setCurrent(conf.getModels().getCurrent());
        }

        if (StringUtils.isNotEmpty(conf.reportPath)) {
            res.reportPath = conf.reportPath;
        }

        return res;
    }
}
