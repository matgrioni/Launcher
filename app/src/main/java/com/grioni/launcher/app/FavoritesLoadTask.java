package com.grioni.launcher.app;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matias Grioni on 8/13/14.
 */
public class FavoritesLoadTask extends AsyncTask<AppModel, Void, List<AppDrawerItem>> {
    private Context context;

    /**
     *
     * @param context
     */
    public FavoritesLoadTask(Context context) {
        this.context = context;
    }

    /**
     *
     * @param temp
     * @return
     */
    public List<AppDrawerItem> doInBackground(AppModel... temp) {
        List<AppModel> apps = Arrays.asList(temp);

        return initFavorites(apps);
    }

    /**
     *
     * @param apps
     * @return
     */
    public List<AppDrawerItem> initFavorites(List<AppModel> apps) {
        List<AppDrawerItem> favorites = new ArrayList<AppDrawerItem>();
        String order = readInFavorites();

        if(order.length() != 0) {
            List<String> packages = new ArrayList<String>();

            for (int i = 0; i < apps.size(); i++)
                packages.add(apps.get(i).getPackageName());

            if (packages != null) {
                String[] orders = order.split("\n");
                AppDrawerFolder nextFolder = new AppDrawerFolder(context);

                int currentPos = 0;
                while(currentPos < orders.length) {
                    String line = orders[currentPos];

                    // This line indicates that the packages that follow are a folder.
                    if(line.matches("folder [0-9]+")) {
                        int folderSize = Integer.parseInt(line.substring(7));

                        for(int k = 0; k < folderSize; k++) {
                            int appPosition = packages.indexOf(orders[currentPos + k + 1]);
                            nextFolder.addMemberApp(apps.get(appPosition));
                        }

                        currentPos += folderSize;

                        favorites.add(nextFolder);
                    } else {
                        int appPosition = packages.indexOf(line);
                        favorites.add(apps.get(appPosition));
                    }

                    currentPos++;
                }
            }
        }

        return favorites;
    }

    /**
     *
     */
    private String readInFavorites() {
        String input = "";
        int content;

        try {
            FileInputStream fis = context.openFileInput(AppLoader.APP_FAVORITES_CONFIG_FILE);

            while((content = fis.read()) != -1)
                input += (char) content;

            fis.close();
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return input;
    }
}
