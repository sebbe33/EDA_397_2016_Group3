package chalmers.eda397_2016_group3;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by N10 on 4/19/2016.
 */
public class FragmentTrello extends Fragment {




    Context c;
    public static Fragment newInstance(Context context) {
        FragmentTrello f = new FragmentTrello();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_trello, null);








        return root;
    }



}
