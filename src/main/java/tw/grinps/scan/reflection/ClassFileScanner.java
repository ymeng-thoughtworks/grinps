package tw.grinps.scan.reflection;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;
import static java.text.MessageFormat.format;

public class ClassFileScanner {

    private String baseNamespace;

    public ClassFileScanner(String baseNamespace) {
        this.baseNamespace = baseNamespace;
    }

    public String[] allClassFullNames() {
        File codeFolder = getSampleCodeFolder();
        File[] classFiles = getClassFiles(codeFolder, baseNamespace);
        return extractClassFileInfos(classFiles);
    }

    private String[] extractClassFileInfos(File[] classFiles) {
        List<String> classes = new ArrayList<String>();
        for (File classFile : classFiles) {
            classes.add(getClassFullName(baseNamespace, classFile.getName()));
        }

        return classes.toArray(new String[0]);
    }

    private String getClassFullName(String baseNamespace, String fileName) {
        String fileNameWithoutExt = removeExtension(fileName);
        return format("{0}.{1}", baseNamespace, fileNameWithoutExt);
    }

    private String removeExtension(String fileName) {
        int dotSeparator = fileName.lastIndexOf('.');
        return fileName.substring(0, dotSeparator);
    }

    private File[] getClassFiles(File baseFolder, String baseNamespace) {
        File classFolder = getClassFolder(baseFolder, baseNamespace);
        return classFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String fileName) {
                return fileName.endsWith(".class");
            }
        });
    }

    private File getClassFolder(File baseFolder, String baseNamespace) {
        String classPath = baseNamespace.replaceAll("\\.", fileSeparator());
        String fullPath = format("{0}/{1}", baseFolder.getAbsoluteFile(), classPath);

        return new File(fullPath);
    }

    private File getSampleCodeFolder() {
        File file = getCurrentClassRootFolder();
        File[] testFolders = file.getParentFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String fileName) {
                return fileName.equalsIgnoreCase("test-classes");
            }
        });
        return testFolders[0];
    }

    private File getCurrentClassRootFolder() {
        URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        return new File(location.getPath());
    }

    private String fileSeparator() {
        return getProperty("file.separator");
    }

}
