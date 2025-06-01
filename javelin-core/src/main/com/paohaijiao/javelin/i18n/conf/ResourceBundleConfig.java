package com.paohaijiao.javelin.i18n.conf;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
public class ResourceBundleConfig {
    private final String baseName;
    private final ClassLoader loader;
    private final ResourceBundle.Control control;

    public ResourceBundleConfig(String baseName) {
        this(baseName, Thread.currentThread().getContextClassLoader(), null);
    }

    public ResourceBundleConfig(String baseName, ClassLoader loader, ResourceBundle.Control control) {
        this.baseName = Objects.requireNonNull(baseName);
        this.loader = loader != null ? loader : Thread.currentThread().getContextClassLoader();
        this.control = control != null ? control : ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);
    }

    public String getBaseName() {
        return baseName;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public ResourceBundle.Control getControl() {
        return control;
    }
}
