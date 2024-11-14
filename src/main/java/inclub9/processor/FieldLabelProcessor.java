package inclub9.processor;

import com.google.auto.service.AutoService;
import inclub9.annotation.FieldLabel;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import static net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.AGGREGATING;

@SupportedAnnotationTypes("inclub9.annotation.FieldLabel")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
@AutoService(Processor.class)
@IncrementalAnnotationProcessor(AGGREGATING)
public class FieldLabelProcessor extends AbstractProcessor {
    private static final int BUFFER_SIZE = 8192;
    private static final int INITIAL_LABELS_SIZE = 32;
    private static final char[] TEMPLATE_START = "package ".toCharArray();
    private static final char[] TEMPLATE_CLASS_START = ";\n\npublic final class ".toCharArray();
    private static final char[] TEMPLATE_LABEL = "Label {\n    public static final String CLASS_NAME = \"".toCharArray();
    private static final char[] TEMPLATE_CONSTRUCTOR = "\";\n\n    private ".toCharArray();
    private static final char[] TEMPLATE_CONSTRUCTOR_END = "Label() {}\n\n".toCharArray();
    private static final char[] TEMPLATE_FIELD = "    public static final String ".toCharArray();
    private static final char[] TEMPLATE_FIELD_MIDDLE = " = \"".toCharArray();
    private static final char[] TEMPLATE_FIELD_END = "\";\n".toCharArray();
    private static final char[] TEMPLATE_END = "}\n".toCharArray();

    private Filer filer;
    private final char[] upperChars = new char[64];
    private final char[] writeBuffer = new char[BUFFER_SIZE];
    private int writeBufferPos = 0;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) return false;

        Map<TypeElement, String[][]> labelsByClass = new IdentityHashMap<>(16);

        for (Element element : roundEnv.getElementsAnnotatedWith(FieldLabel.class)) {
            if (element.getKind() != ElementKind.FIELD) continue;

            TypeElement classElement = (TypeElement) element.getEnclosingElement();
            String[][] labels = labelsByClass.computeIfAbsent(classElement,
                    k -> new String[INITIAL_LABELS_SIZE][2]);

            int size = 0;
            while (size < labels.length && labels[size][0] != null) size++;

            if (size == labels.length) {
                String[][] newLabels = new String[size * 2][2];
                System.arraycopy(labels, 0, newLabels, 0, size);
                labels = newLabels;
                labelsByClass.put(classElement, labels);
            }

            labels[size][0] = toUpperUnderscore(element.getSimpleName().toString());
            labels[size][1] = element.getAnnotation(FieldLabel.class).value();
        }

        labelsByClass.forEach(this::generateClass);
        return true;
    }

    private String toUpperUnderscore(String input) {
        int len = input.length();
        if (len > upperChars.length) return input.toUpperCase();

        int j = 0;
        char last = 0;
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (i > 0 && Character.isUpperCase(c) && !Character.isUpperCase(last)) {
                upperChars[j++] = '_';
            }
            upperChars[j++] = Character.toUpperCase(c);
            last = c;
        }
        return new String(upperChars, 0, j);
    }

    private void writeToBuffer(char[] chars) throws IOException {
        if (writeBufferPos + chars.length > BUFFER_SIZE) {
            throw new IOException("Buffer overflow");
        }
        System.arraycopy(chars, 0, writeBuffer, writeBufferPos, chars.length);
        writeBufferPos += chars.length;
    }

    private void writeToBuffer(String str) throws IOException {
        int len = str.length();
        if (writeBufferPos + len > BUFFER_SIZE) {
            throw new IOException("Buffer overflow");
        }
        str.getChars(0, len, writeBuffer, writeBufferPos);
        writeBufferPos += len;
    }

    private void flushBuffer(Writer writer) throws IOException {
        if (writeBufferPos > 0) {
            writer.write(writeBuffer, 0, writeBufferPos);
            writeBufferPos = 0;
        }
    }

    private void generateClass(TypeElement classElement, String[][] labels) {
        String qualifiedName = classElement.getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        int lastDot = qualifiedName.lastIndexOf('.');
        String packageName = qualifiedName.substring(0, lastDot);

        try {
            JavaFileObject file = filer.createSourceFile(packageName + "." + className + "Label");
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(file.openOutputStream()))) {
                writeBufferPos = 0;

                writeToBuffer(TEMPLATE_START);
                writeToBuffer(packageName);
                writeToBuffer(TEMPLATE_CLASS_START);
                writeToBuffer(className);
                writeToBuffer(TEMPLATE_LABEL);
                writeToBuffer(className);
                writeToBuffer(TEMPLATE_CONSTRUCTOR);
                writeToBuffer(className);
                writeToBuffer(TEMPLATE_CONSTRUCTOR_END);

                for (int i = 0; labels[i][0] != null && i < labels.length; i++) {
                    writeToBuffer(TEMPLATE_FIELD);
                    writeToBuffer(labels[i][0]);
                    writeToBuffer(TEMPLATE_FIELD_MIDDLE);
                    writeToBuffer(labels[i][1]);
                    writeToBuffer(TEMPLATE_FIELD_END);
                }

                writeToBuffer(TEMPLATE_END);
                flushBuffer(writer);
            }
        } catch (IOException ignored) {}
    }
}