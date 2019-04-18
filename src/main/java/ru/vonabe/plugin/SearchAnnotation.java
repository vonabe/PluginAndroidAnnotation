package ru.vonabe.plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class SearchAnnotation extends AnAction {

    private String annotationSearch = "";
    private String packagename = "";

    public SearchAnnotation() {
        super("Поиск аннотаций ");
    }

    public SearchAnnotation(@Nullable String text) {
        super("Поиск аннотаций "+text);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        ShowDialogQueryPackagePatch showDialogQueryPackagePatch = new ShowDialogQueryPackagePatch(project);
        showDialogQueryPackagePatch.addListener(result -> {
            packagename = result[0];
            annotationSearch = result[1];
            System.out.println("Package "+packagename+" "+annotationSearch);
        });
        boolean b = showDialogQueryPackagePatch.showAndGet();
        if(!b)return;

        VirtualFile[] children = e.getData(PlatformDataKeys.PROJECT_FILE_DIRECTORY).getChildren();

        StringBuilder builder = new StringBuilder();
        for (VirtualFile child : children) {
            getFileIsDirectory(child);
        }

        File fileData = null;
        JsonArray array = new JsonArray();

        for (VirtualFile virtualFile : list) {
            if(virtualFile.isDirectory()){
                fileData = new File(virtualFile.getPath(), "mapping.json");
            }

            String formatPathClass = formatClass(virtualFile.getPath());
            final PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(formatPathClass, GlobalSearchScope.allScope(project));

            try {
                if(psiClass != null) {
                    if (!psiClass.isAnnotationType()) {
                        PsiAnnotation[] annotations = psiClass.getAnnotations();

                        if(annotations.length <= 0){
                            System.out.println(psiClass.getName()+", "+psiClass.getQualifiedName()+" annotation 0");
                        }

                        for (PsiAnnotation annotation : annotations) {
                            String annotationClassName = annotation.getQualifiedName();

                            if (annotationClassName.equalsIgnoreCase(annotationSearch)) {
                                String value = annotation.findAttribute("value").getAttributeValue().getSourceElement().getText();
                                value = formatAnnotation(value);
//                                String name = TypeDialog.valueOf().name();

                                JsonObject object = new JsonObject();
                                object.addProperty("type", value);
                                object.addProperty("class", psiClass.getQualifiedName());
                                array.add(object);

                                builder.append(virtualFile.getPath()).append("\n");

                            } else {
                                System.out.println(psiClass.getName()+" Don't Annotation @Dialog");
                            }
                        }
                    } else {
                        System.out.println("PsiClass isAnnotationType "+psiClass.isAnnotationType()+" "+psiClass.getName());
                    }
                } else {
                    System.out.println("Psi class null ");
                }
            }catch (Exception ex){ex.printStackTrace();}
//                Class<?> aClass1 = Class.forName(name);
//                System.out.println("To class "+name+", "+aClass1.getCanonicalName());
//                builder.append("To class "+name+", "+aClass1.getCanonicalName()).append("\n");
//                if(aClass1.isAnnotationPresent(DialogViewAnnotation.class)){
//                    DialogViewAnnotation annotation = aClass1.getAnnotation(DialogViewAnnotation.class);
//                    TypeDialog value = annotation.value();
//                    builder.append("TypeClass: "+value).append("\n");
//                }
        }

        try {
            Files.write(fileData.toPath(), array.toString().getBytes());
            Messages.showMessageDialog(project, builder.toString(), "Save File Success", Messages.getInformationIcon());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

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
                String path = file.getPath();
                if(path.contains("/assets") && !list.contains(file)){
                    list.add(file);
                }
                return true;
            }else{
                if(file.getPath().contains(packagename) && file.getPath().endsWith(".java") && !list.contains(file)) {
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
