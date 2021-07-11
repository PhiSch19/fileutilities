package at.phisch.fileutilities.csv.exporter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation has to be set on each class which should be exported.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CsvExportParameter {

    /**
     * adds a header to the file.
     * Individual headers for every Field can be set with {@link CsvExportField#header()}
     *
     * @return if the header is activated
     */
    boolean header() default false;

    /**
     * the delimiter which is used to delimit the fields.
     *
     * @return the delimiter
     */
    String delimiter() default ";";

    /**
     * Charset must be available with the Method {@link java.nio.charset.Charset#forName(String)};
     *
     * @return the Charset name
     */
    String charset() default "UTF-8";

}
