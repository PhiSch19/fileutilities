package at.phisch.fileutilities.csv.exporter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * every fields which should be exported needs to be declared with this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvExportField {

    /**
     * defines the position where the field should be exported.
     * The first position within the created file is 1.
     *
     * @return the position in which the field should be exported
     */
    int position();

    /**
     * only relevant if an header should be created.
     * Headers can be activated with {@link CsvExportParameter#header()}.
     *
     * @return the header which should be exported
     */
    String header() default "";

}
