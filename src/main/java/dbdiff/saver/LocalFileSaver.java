package dbdiff.saver;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;

@AllArgsConstructor
@Slf4j
public class LocalFileSaver implements ReportSaver {
    private final String filePath;

    @SneakyThrows
    @Override
    public void save(byte[] report) {
        log.info("Saving started");
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            out.write(report);
        }
        log.info("Saving finished");
    }
}
