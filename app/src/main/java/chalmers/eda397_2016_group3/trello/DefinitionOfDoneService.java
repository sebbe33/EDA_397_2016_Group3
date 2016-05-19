package chalmers.eda397_2016_group3.trello;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chalmers.eda397_2016_group3.utils.ObjectSerializer;

/**
 * Created by sebastianblomberg on 2016-05-18.
 */
public class DefinitionOfDoneService {
    private static final String DOD_LIST_KEY = "DOD_LIST_KEY";
    private static final String DID_LIST_SHARED_PREFS_KEY = "DID_LIST_SHARED_PREFS_KEY";
    private static ArrayList<String> dodList = new ArrayList<>();
    private static boolean hasReadFromPrefs = false;

    public static List<String> getDodList(Context context) {
        if(!hasReadFromPrefs) {
            SharedPreferences sharedpreferences =
                    context.getSharedPreferences(DID_LIST_SHARED_PREFS_KEY, Context.MODE_PRIVATE);

            try {
                dodList = (ArrayList<String>) ObjectSerializer.deserialize(
                        sharedpreferences.getString(
                                DOD_LIST_KEY, ObjectSerializer.serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


        return dodList;
    }

    public static void addItem(String item, Context context) {
        dodList.add(item);
        persistDodList(context);
    }

    public static void removeItem(String item, Context context) {
        dodList.remove(item);
        persistDodList(context);
    }

    private static void persistDodList(Context context) {
        SharedPreferences sharedpreferences =
                context.getSharedPreferences(DID_LIST_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        try {
            editor.putString(DOD_LIST_KEY, ObjectSerializer.serialize(dodList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }
}
