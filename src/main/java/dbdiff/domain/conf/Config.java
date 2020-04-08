package dbdiff.domain.conf;

import lombok.Data;

@Data
public class Config {
    Models models = new Models();

    public Config copy() {
        Config res = new Config();
        res.getModels().setOld(this.getModels().getOld());
        res.getModels().setCurrent(this.getModels().getCurrent());
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

        return res;
    }
}
