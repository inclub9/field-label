package inclub9.processor;

import com.google.auto.service.AutoService;
import inclub9.annotation.FieldLabel;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SupportedAnnotationTypes("inclub9.annotation.FieldLabel")
@SupportedSourceVersion(SourceVersion.RELEASE_24)
@AutoService(Processor.class)
public class FieldLabelProcessor extends AbstractProcessor {
    private final Map<String, StringBuilder> constantBuilders = new ConcurrentHashMap<>();
    private final Map<String, StringBuilder> fieldBuilders = new ConcurrentHashMap<>();
    private static final int INITIAL_CAPACITY = 16;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) return false;

        annotations.forEach(annotation ->
                roundEnv.getElementsAnnotatedWith(annotation).stream()
                        .filter(element -> element.getKind() == ElementKind.FIELD)
                        .forEach(this::processElement)
        );

        // Generate all files at once
        constantBuilders.forEach(this::generateLabelClass);
        constantBuilders.clear();
        fieldBuilders.clear();

        return true;
    }

    private void processElement(Element element) {
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String className = classElement.getQualifiedName().toString();
        String fieldName = element.getSimpleName().toString();
        String label = element.getAnnotation(FieldLabel.class).value();

        // Add uppercase constant
        constantBuilders.computeIfAbsent(className, k -> new StringBuilder(INITIAL_CAPACITY))
                .append(String.format("    public static final String %s = \"%s\";%n",
                        camelCaseToUpperUnderscore(fieldName), label));

        // Add original field name constant
        fieldBuilders.computeIfAbsent(className, k -> new StringBuilder(INITIAL_CAPACITY))
                .append(String.format("    public static final String %s = \"%s\";%n",
                        fieldName, label));
    }

    private String camelCaseToUpperUnderscore(String input) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }

        return result.toString();
    }

    private void generateLabelClass(String className, StringBuilder constants) {
        try {
            String packageName = className.substring(0, className.lastIndexOf('.'));
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            String labelClassName = simpleClassName + "Label";

            JavaFileObject file = processingEnv.getFiler()
                    .createSourceFile(packageName + "." + labelClassName);

            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.printf("package %s;%n%n", packageName);
                out.printf("public final class %s {%n", labelClassName);
                out.printf("    public static final String CLASS_NAME = \"%s\";%n%n", simpleClassName);
                out.printf("    private %s() {}%n%n", labelClassName);

                // Add original field name constants
                out.println("    // Original field names");
                out.print(fieldBuilders.get(className));
                out.println();

                // Add uppercase constants
                out.println("    // Uppercase constants");
                out.print(constants);

                out.println("}");
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to create source file: " + e.getMessage()
            );
        }
    }
}