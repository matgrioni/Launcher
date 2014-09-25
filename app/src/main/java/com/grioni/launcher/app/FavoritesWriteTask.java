package com.grioni.launcher.app;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matias Grioni on 8/14/14.
 */
public class FavoritesWriteTask extends AsyncTask<List<AppDrawerItem>, Void, Void> {
    private Context context;

    /**
     *
     * @param context
     */
    public FavoritesWriteTask(Context context) {
        this.context = context;
    }

    @Override
    public Void doInBackground(List<AppDrawerItem>... favs) {
        writeOutFavorites(favs[0]);

        return null;
    }

    /**
     *
     */
    private void writeOutFavorites(List<AppDrawerItem> favorites) {
        String output = "";

        for(int i = 0; i < favorites.size(); i++) {
            AppDrawerItem current = favorites.get(i);

            if(current instanceof AppModel)
                output += ((AppModel) current).getPackageName() + "\n";
            else if(current instanceof AppDrawerFolder) {
                List<AppModel> folderItems = ((AppDrawerFolder) current).getMemberApps();

                // This is to show that in the favorites page there is a folder, followed by the
                // amount of items in this folder.
                output += "folder " + Integer.toString(folderItems.size()) + "\n";

                for(int j = 0; j < folderItems.size(); j++)
                    output += folderItems.get(j).getPackageName() + "\n";
            }
        }

        try {
            FileOutputStream fos = context.openFileOutput(AppLoader.APP_FAVORITES_CONFIG_FILE, Context.MODE_PRIVATE);
            fos.write(output.getBytes());
            fos.close();
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
