package com.grioni.launcher.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Represents an Application, with information such as the icon, the apk file, the title, and if it
 * is mounted. Also includes an instance of AppLoader to access a PackageManager object.
 *
 * Created by Matias Grioni on 5/11/14.
 */
public class AppModel extends AppDrawerItem {

    private final AppLoader loader;
    private final ApplicationInfo info;

    private final File apk;
    private boolean mounted;

    /**
     * Creates an AppModel.
     * @param loader - Used to load information such as label and icon of the application.
     * @param info - Info about the application being represented.
     */
    public AppModel(AppLoader loader, ApplicationInfo info) {
        this.loader = loader;
        this.info = info;

        apk = new File(info.sourceDir);
        icon = getIcon();
        loadLabel();
    }

    /**
     *
     * @param model
     */
    public AppModel(AppModel model) {
        this(model.getLoader(), model.getInfo());
    }

    /**
     * Loads the label of the application using the loader's package manager and assigns this label
     * to the member variable.
     */
    private void loadLabel() {
        // If the label has not already been loaded or the app is not mounted, then see if the label
        // can be retrieved.
        if(label == null || !mounted) {
            // If the application is not mounted then the label is simply the package name. Otherwise
            // the application label is loaded and if it is not null,this value is returned.
            if(!apk.exists()) {
                mounted = false;
                label = info.packageName;
            } else {
                mounted = true;
                String temp = info.loadLabel(loader.getPackageManager()).toString();
                label =  temp != null ? temp : info.packageName;
            }
        }
    }


    /**
     *
     */
    private void loadIcon() {
        // If the icon has not been loaded yet or it has been unsuccesful in the past then try again.
        // And otherwise if the app was not mounted check if this time the apk exists then load the
        // icon.
        if(icon == null) {

            // If the apk file exists then load the icon from the loader but otherwise simply set
            // mounted to false.
            if(apk.exists()) {
                Drawable temp = info.loadIcon(loader.getPackageManager());
                icon = temp;
            } else
                mounted = false;
        } else if(!mounted) {
            if(apk.exists()) {
                mounted = true;

                Drawable temp = info.loadIcon(loader.getPackageManager());
                icon = temp;
            } else
                // Sets the icon to the default application image if no icon can be found from the package.
                icon =  loader.getContext().getResources().getDrawable(android.R.drawable.sym_def_app_icon);
        }


    }

    /**
     * @return The loaded label.
     */
    public String getLabel() {
        loadLabel();
        return label;
    }

    /**
     * @return The loaded icon.
     */
    public Drawable getIcon() {
        loadIcon();
        return icon;
    }

    /**
     *
     * @return
     */
    public ApplicationInfo getInfo() {
        return info;
    }

    public AppLoader getLoader() {
        return loader;
    }

    /**
     * @return The package name of this application from the ApplicationInfo.
     */
    public String getPackageName() {
        return info.packageName;
    }
}
