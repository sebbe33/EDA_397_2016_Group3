package chalmers.eda397_2016_group3.utils;

import android.support.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Created by hui on 03/05/16.
 */

public class Pairs {

    private static final Random RND = new Random();
   // private List<String> list =  new ArrayList<>();
    private ArrayList<String> arrayList = new ArrayList<String>();

    public void addPair( String key) {
   //     list.add(key);
        arrayList.add(key);
    }


    public String getRandomPair() {
        int i = RND.nextInt(arrayList.size());
        String first = arrayList.get(i);

        arrayList.remove(i);


        String second = arrayList.get(RND.nextInt(arrayList.size()));
        return first+":"+second;
    }
}

