package ru.vonabe.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@DialogViewAnnotation(value = SearchAnnotation.TypeDialog.ALERT_INFO)
public class SearchAnnotation extends AnAction {

    public static enum TypeDialog {
        REPLACE_PHONENUMBER, REPLACE_EMAIL, REPLACE_USERNAME, ALERT_INFO, GENCODE_VERIFY
    }

    public SearchAnnotation() {
        super("Поиск аннотаций ");
    }

    public SearchAnnotation(@Nullable String text) {
        super("Поиск аннотаций "+text);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        VirtualFile[] children = e.getData(PlatformDataKeys.PROJECT_FILE_DIRECTORY).getChildren();

        StringBuilder builder = new StringBuilder();
        for (VirtualFile child : children) {
            getFileIsDirectory(child);
        }

        for (VirtualFile virtualFile : list) {
            builder.append(virtualFile.getPath()).append("\n");

//           FROM     C:/Users/Rik62/Desktop/VonabeAnnotationPlugin/src/main/java/ru/vonabe/plugin/SearchAnnotation.java
//           TO       com.angrbt.lapki.dialogs.DialogCodeVerification

//           FROM SearchAnnotation.TypeDialog.ALERT_INFO
//           TO TypeDialog.ALERT_INFO

            String formatPathClass = formatClass(virtualFile.getPath());
            final PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(formatPathClass, GlobalSearchScope.allScope(project));

            if(!psiClass.isAnnotationType()) {
                PsiAnnotation[] annotations = psiClass.getAnnotations();
                for (PsiAnnotation annotation : annotations) {
                    String annotationClassName = annotation.getQualifiedName();

                    if(annotationClassName.equalsIgnoreCase(DialogViewAnnotation.class.getName())) {
                        String value = annotation.findAttribute("value").getAttributeValue().getSourceElement().getText();
                        String name = TypeDialog.valueOf(formatAnnotation(value)).name();


//                    TypeDialog value1 = TypeDialog.valueOf();
//                        try {
//                            Class<?> aClass = Class.forName(annotationClassName);
//                            DialogViewAnnotation annotation1 = aClass.getAnnotation(DialogViewAnnotation.class);
//                            TypeDialog value1 = annotation1.value();
//                            System.out.println("Annotation " + annotation.getQualifiedName() + " " + value1);
//
//                        } catch (ClassNotFoundException e1) {
//                            e1.printStackTrace();
//                        }
                    }
                }
            } else {}
//                Class<?> aClass1 = Class.forName(name);
//                System.out.println("To class "+name+", "+aClass1.getCanonicalName());
//                builder.append("To class "+name+", "+aClass1.getCanonicalName()).append("\n");
//                if(aClass1.isAnnotationPresent(DialogViewAnnotation.class)){
//                    DialogViewAnnotation annotation = aClass1.getAnnotation(DialogViewAnnotation.class);
//                    TypeDialog value = annotation.value();
//                    builder.append("TypeClass: "+value).append("\n");
//                }
        }
        Messages.showMessageDialog(project, builder.toString(), "Greeting", Messages.getInformationIcon());
    }

    private String formatAnnotation(String path){
        final String prefix = "SearchAnnotation.TypeDialog.";
        if(path!=null && !path.isEmpty()){
            if(path.contains(prefix)){
                return path.substring(path.indexOf(prefix)+prefix.length());
            }
        }
        return path;
    }

    private String formatClass(String path){
        final String prefix = "/src/main/java/";
        if(path!=null && !path.isEmpty()){
            if(path.contains(prefix)){
                return path.substring(path.indexOf(prefix)+prefix.length()).replace("/",".").replaceAll(".java","");
            }
        }
        return path;
    }

    private List<VirtualFile> list = new LinkedList<>();
    public List<VirtualFile> getFileIsDirectory(VirtualFile dir){
        VfsUtilCore.iterateChildrenRecursively(dir, file -> {
            if(file.isDirectory()){
                return true;
            }else{
                if(file.getPath().contains("/ru/vonabe/plugin/") && file.getPath().endsWith(".java") && !list.contains(file)) {
                    list.add(file);
                }
                return false;
            }
        }, fileOrDir -> {
            for (VirtualFile virtualFile : fileOrDir.getChildren()) {
                getFileIsDirectory(virtualFile);
            }
            return true;
        });
        return list;
    }

}
