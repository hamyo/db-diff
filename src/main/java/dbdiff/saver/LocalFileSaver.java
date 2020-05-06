package dbdiff.saver;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.FileOutputStream;

@AllArgsConstructor
public class LocalFileSaver implements ReportSaver {
    private final String filePath;

    @SneakyThrows
    @Override
    public void save(byte[] report) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            out.write(report);
        }
    }
}
