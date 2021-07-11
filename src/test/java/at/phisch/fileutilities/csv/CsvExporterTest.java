package at.phisch.fileutilities.csv;

import at.phisch.fileutilities.csv.data.TestExportData;
import at.phisch.fileutilities.csv.exporter.CsvExporter;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExporterTest {

    @Test
    public void testFail(@TempDir Path tempDir) throws IOException {

        List<TestExportData> data = new ArrayList<>();
        data.add(new TestExportData("t1", 5, Date.from(Instant.parse("2020-05-03T13:17:25.00Z"))));
        data.add(new TestExportData("t2", 2, Date.from(Instant.parse("2020-08-07T10:17:25.00Z"))));
        data.add(new TestExportData("testa", 4, Date.from(Instant.parse("2019-05-04T17:17:25.00Z"))));

        Path testFile = Files.createTempFile(tempDir, "test", ".csv");
        Files.delete(testFile);

        new CsvExporter<TestExportData>(TestExportData.class)
                .createFile(data, testFile);

        String writtenContent = Files.readAllLines(testFile)
                .stream()
                .collect(Collectors.joining("\\n"));

        String expectedContent = "15:17;t1;5\\n" +
                "12:17;t2;2\\n" +
                "19:17;testa;4";

        MatcherAssert.assertThat(writtenContent, CoreMatchers.is(expectedContent));
    }

}
