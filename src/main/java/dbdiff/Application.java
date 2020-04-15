package dbdiff;

import dbdiff.domain.conf.Config;
import dbdiff.parser.DbZos;
import dbdiff.parser.ModelParser;
import dbdiff.report.WordReport;
import dbdiff.service.Comparator;
import dbdiff.service.DbFormer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Application {

    private static final String PROPERTIES_FILE_NAME = "app.yml";
    public static final String OLD_MODEL_PARAMETERS = "old";
    public static final String CURRENT_MODEL_PARAMETERS = "current";

    public static void main(String[] args) {
        try {
            Config config = getConfig(args);
            Comparator comparator = new Comparator(new DbZos(), new DbFormer(), new WordReport(config.getReportPath()), config);
            comparator.run();
        } catch (Exception e) {
            log.error("Critical error.", e);
        }
    }

    private static Config getConfig(String[] args) {
        Config fileConfig = getConfigFromFile();
        Config argsConfig = getConfigFromArgs(args);
        Config config = fileConfig.merge(argsConfig);

        checkFileExists(config.getModels().getCurrent());
        checkFileExists(config.getModels().getOld());
        checkRequiredParams(config);
        return config;
    }

    private static void checkRequiredParams(Config config) {
        checkNotEmpty(config.getReportPath(), "reportPath");
        checkNotEmpty(config.getModels().getCurrent(), "model:current");
        checkNotEmpty(config.getModels().getOld(), "model:old");
    }

    private static void checkNotEmpty(String param, String paramName) {
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException(MessageFormat.format("Params {0} is empty", paramName));
        }
    }

    private static void checkFileExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(MessageFormat.format("File {0} not exists", path));
        }
    }

    private static Config getConfigFromArgs(String[] args) {
        Map<String, String> params = Arrays.stream(args)
                .filter(arg -> arg.contains("="))
                .map(arg -> arg.split("="))
                .filter(arg -> arg.length == 2 && !arg[1].isEmpty())
                .collect(Collectors.toMap(arg -> arg[0], arg -> arg[1]));
        Config config = new Config();
        if (params.containsKey(OLD_MODEL_PARAMETERS)) {
            config.getModels().setOld(params.get(OLD_MODEL_PARAMETERS));
        }

        if (params.containsKey(CURRENT_MODEL_PARAMETERS)) {
            config.getModels().setCurrent(params.get(CURRENT_MODEL_PARAMETERS));
        }

        return config;
    }

    @SneakyThrows
    private static Config getConfigFromFile() {
        Yaml yaml = new Yaml();
        try (InputStream in = Application.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            Config config = yaml.loadAs(in, Config.class);
            return config;
        }
    }
}
