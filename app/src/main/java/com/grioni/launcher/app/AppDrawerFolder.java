package com.grioni.launcher.app;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matias Grioni on 8/6/14.
 */
public class AppDrawerFolder extends AppDrawerItem {
    private List<AppModel> memberApps;

    /**
     *
     */
    public AppDrawerFolder(Context context) {
        this(context, "");
    }

    /**
     *
     * @param label
     */
    public AppDrawerFolder(Context context, String label) {
        this(context, label, null);
    }

    /**
     *
     * @param apps
     */
    public AppDrawerFolder(Context context, List<AppModel> apps) {
        this(context, "", apps);
    }

    /**
     *
     * @param label
     * @param apps
     */
    public AppDrawerFolder(Context context, String label, List<AppModel> apps) {
        this.label = label;

        if(apps != null)
            this.memberApps = apps;
        else
            this.memberApps = new ArrayList<AppModel>();

        icon = context.getResources().getDrawable(R.drawable.folder);
    }

    /**
     *
     * @return
     */
    public List<AppModel> getMemberApps() {
        return memberApps;
    }

    /**
     *
     * @param member
     */
    public void addMemberApp(AppModel member) {
        memberApps.add(member);
    }

    /**
     *
     * @param members
     */
    public void addMemberApps(List<AppModel> members) {
        memberApps.addAll(members);
    }

    /**
     *
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @return
     */
    public Drawable getIcon() {
        return icon;
    }
}
