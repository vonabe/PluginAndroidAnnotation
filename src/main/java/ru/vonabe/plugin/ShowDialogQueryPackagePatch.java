package ru.vonabe.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ShowDialogQueryPackagePatch extends DialogWrapper {

    private JTextField editTextPackage = null, editTextAnnotationName = null;

    interface Listener {
        void result(String[] result);
    }

    private Listener listener;

    protected ShowDialogQueryPackagePatch(@Nullable Project project) {
        super(project);
        init();
        setTitle("Added package name");
    }

    public void addListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public boolean isOK() {
        listener.result(new String[]{editTextPackage.getText(), editTextAnnotationName.getText()});
        return super.isOK();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        editTextPackage = new JTextField();
        editTextPackage.setPreferredSize(new Dimension(100, 30));
        editTextPackage.setToolTipText("Default Package Path");

        editTextAnnotationName = new JTextField();
        editTextAnnotationName.setPreferredSize(new Dimension(100, 30));
        editTextAnnotationName.setToolTipText("Annotation class");

        panel.add(editTextPackage, BorderLayout.SOUTH);
        panel.add(editTextAnnotationName, BorderLayout.NORTH);

        return panel;
    }
}
