package at.phisch.fileutilities.csv.exporter;

import at.phisch.fileutilities.csv.FieldFormat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CsvExporter<D> {

    private final CsvExportParameter exportParameter;
    private final Map<Integer, FieldInformations> fieldInformations;

    public CsvExporter(Class<D> resultClass) {
        exportParameter = resultClass.getAnnotation(CsvExportParameter.class);
        fieldInformations = Arrays.stream(resultClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CsvExportField.class))
                .collect(Collectors.toMap(field -> field.getAnnotation(CsvExportField.class).position(), FieldInformations::new));
    }

    public void createOrAppendToFile(List<D> data, Path file) throws IOException {
        if (Files.exists(file)) {
            appendToFile(data, file);
        } else {
            createFile(data, file);
        }
    }

    public void createFile(List<D> data, Path file) throws IOException {
        if (Files.exists(file)) throw new FileAlreadyExistsException(file.toString());
        List<String> lines = new ArrayList<>();
        if (exportParameter.header()) {
            lines.add(createHeader());
        }
        lines.addAll(createDataLines(data));
        writeToFile(lines, file);
    }

    public void appendToFile(List<D> data, Path file) throws IOException {
        if (!Files.exists(file)) throw new FileNotFoundException();
        writeToFile(createDataLines(data), file);
    }

    private List<String> createDataLines(List<D> data) {
        return data.stream().map(this::createDataLine).collect(Collectors.toList());
    }

    private void writeToFile(List<String> lines, Path file) throws IOException {
        Files.write(file, lines, Charset.forName(exportParameter.charset()), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private int getMaxPosition() {
        return fieldInformations.keySet().stream().mapToInt(p -> p).max().orElseThrow(IllegalStateException::new);
    }

    private String createHeader() {
        return createLine(fi -> fi.getExportField().header());
    }

    private String createDataLine(D data) {
        return createLine(fi -> fi.extractDataAndFormat(data));
    }

    private String createLine(Function<FieldInformations, String> lineMapping) {
        List<String> fields = new ArrayList<>();
        for (int i = 1; i <= getMaxPosition(); i++) {
            fields.add(lineMapping.apply(fieldInformations.get(i)));
        }
        return fields.stream().collect(Collectors.joining(exportParameter.delimiter()));
    }

    private static class FieldInformations {

        private final Field field;
        private Format format;

        public FieldInformations(Field field) {
            field.setAccessible(true);
            this.field = field;
            initializeFormat();
        }

        public CsvExportField getExportField() {
            return field.getAnnotation(CsvExportField.class);
        }

        public String extractDataAndFormat(Object data) {
            if (data == null) return "";
            try {
                return formatDataForField(field.get(data));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        private String formatDataForField(Object data) {
            if (data == null) return "";
            if (format != null) return format.format(data);
            return data.toString();
        }

        private void initializeFormat() {
            Class<?> type = field.getType();
            FieldFormat fieldFormat = field.getDeclaredAnnotation(FieldFormat.class);
            if (fieldFormat != null) {
                if (Number.class.isAssignableFrom(type)) {
                    format = new DecimalFormat(fieldFormat.format());
                } else if (Date.class.isAssignableFrom(type)) {
                    format = new SimpleDateFormat(fieldFormat.format());
                }
            }
        }
    }

}
