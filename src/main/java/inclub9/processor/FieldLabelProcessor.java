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
    private static final int INITIAL_MAP_CAPACITY = 16;
    private static final int INITIAL_STRING_BUILDER_CAPACITY = 32;
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) return false;

        Map<String, Map<String, String>> labelsByClass = new HashMap<>(INITIAL_MAP_CAPACITY);

        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element element : elements) {
                if (element.getKind() != ElementKind.FIELD) continue;

                TypeElement classElement = (TypeElement) element.getEnclosingElement();
                String className = classElement.getQualifiedName().toString();

                Map<String, String> fieldLabels = labelsByClass.computeIfAbsent(
                        className,
                        k -> new TreeMap<>()
                );

                fieldLabels.put(
                        camelCaseToUpperUnderscore(element.getSimpleName().toString()),
                        element.getAnnotation(FieldLabel.class).value()
                );
            }
        }

        labelsByClass.forEach(this::generateLabelClass);
        return true;
    }

    private String camelCaseToUpperUnderscore(String input) {
        StringBuilder result = new StringBuilder(INITIAL_STRING_BUILDER_CAPACITY);
        int len = input.length();

        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }

        return result.toString();
    }

    private void generateLabelClass(String className, Map<String, String> fieldLabels) {
        int lastDot = className.lastIndexOf('.');
        String packageName = className.substring(0, lastDot);
        String simpleClassName = className.substring(lastDot + 1);
        String labelClassName = simpleClassName + "Label";
        String sourceFile = packageName + "." + labelClassName;

        try (PrintWriter out = new PrintWriter(filer.createSourceFile(sourceFile).openWriter())) {
            out.println("package " + packageName + ";\n");
            out.println("public final class " + labelClassName + " {");
            out.println("    public static final String CLASS_NAME = \"" + simpleClassName + "\";\n");
            out.println("    private " + labelClassName + "() {}\n");

            fieldLabels.forEach((fieldName, label) ->
                    out.println("    public static final String " + fieldName + " = \"" + label + "\";")
            );

            out.println("}");
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to generate " + sourceFile + ": " + e.getMessage());
        }
    }
}