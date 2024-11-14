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

@SupportedAnnotationTypes("inclub9.annotation.FieldLabel")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
@AutoService(Processor.class)
public class FieldLabelProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "Processing " + annotations.size() + " annotations"
        );

        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Found " + elements.size() + " elements with annotation"
            );

            Map<String, Set<String>> labelsByClass = new HashMap<>();

            for (Element element : elements) {
                if (element.getKind() != ElementKind.FIELD) continue;

                TypeElement classElement = (TypeElement) element.getEnclosingElement();
                String className = classElement.getQualifiedName().toString();
                String fieldName = element.getSimpleName().toString();
                String label = element.getAnnotation(FieldLabel.class).value();

                // Convert camelCase to UPPER_CASE
                String constantName = camelCaseToUpperUnderscore(fieldName);

                labelsByClass.computeIfAbsent(className, k -> new TreeSet<>())
                        .add(String.format("    public static final String %s = \"%s\";",
                                constantName, label));
            }

            for (Map.Entry<String, Set<String>> entry : labelsByClass.entrySet()) {
                try {
                    generateLabelClass(entry.getKey(), entry.getValue());
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Failed to create source file: " + e.getMessage()
                    );
                }
            }
        }
        return true;
    }

    private String camelCaseToUpperUnderscore(String input) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            // Add underscore if it's an uppercase letter and not the first character
            if (i > 0 && Character.isUpperCase(currentChar)) {
                result.append('_');
            }

            result.append(Character.toUpperCase(currentChar));
        }

        return result.toString();
    }

    private void generateLabelClass(String className, Set<String> constants) throws IOException {
        String packageName = className.substring(0, className.lastIndexOf('.'));
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        String labelClassName = simpleClassName + "Label";

        String sourceFile = packageName + "." + labelClassName;
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "Generating " + sourceFile
        );

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(sourceFile);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.println("package " + packageName + ";");
            out.println();
            out.println("public final class " + labelClassName + " {");
            // Add CLASS_NAME constant
            out.println("    public static final String CLASS_NAME = \"" + simpleClassName + "\";");
            out.println();
            out.println("    private " + labelClassName + "() {}");
            out.println();
            constants.forEach(out::println);
            out.println("}");
        }
    }
}