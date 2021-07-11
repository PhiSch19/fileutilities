package at.phisch.fileutilities.csv.data;

import at.phisch.fileutilities.csv.FieldFormat;
import at.phisch.fileutilities.csv.exporter.CsvExportField;
import at.phisch.fileutilities.csv.exporter.CsvExportParameter;

import java.util.Date;

@CsvExportParameter
public class TestExportData {

    @CsvExportField(position = 2)
    private String str1;

    @CsvExportField(position = 3)
    private Integer i1;

    @FieldFormat(format = "HH:mm")
    @CsvExportField(position = 1)
    private Date d1;

    public TestExportData(String str1, Integer i1, Date d1) {
        this.str1 = str1;
        this.i1 = i1;
        this.d1 = d1;
    }
}
