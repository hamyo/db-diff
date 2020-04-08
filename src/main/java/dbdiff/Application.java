package dbdiff;

import dbdiff.domain.conf.Config;
import dbdiff.parser.DbZos;
import dbdiff.service.Comparator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
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
            Config config = getConfigFromFile();
            Config argsConfig = getConfigFromArgs(args);
            Comparator comparator = new Comparator(new DbZos(), config.merge(argsConfig));
            comparator.run();
        } catch (Exception e) {
            log.error("Critical error.", e);
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
