package com.darkweb.android.twitterapi.Fragment;

import java.io.InputStream;
import java.net.URL;


import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.darkweb.android.twitterapi.ProfileActivity;
import com.darkweb.android.twitterapi.R;
import com.darkweb.android.twitterapi.ViewData;

public class ProfileFragment extends Fragment {
    TextView prof_name,viewdata;
    SharedPreferences pref;
    Bitmap bitmap;
    ImageView prof_img,signout;



    ProgressDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        prof_name = (TextView)view.findViewById(R.id.prof_name);
        pref = getActivity().getPreferences(0);
        prof_img = (ImageView)view.findViewById(R.id.prof_image);
        signout = (ImageView)view.findViewById(R.id.signout);

        viewdata = view.findViewById(R.id.viewdata);
        viewdata.setOnClickListener(new SearchData());

        signout.setOnClickListener(new SignOut());

        new LoadProfile().execute();
        return view;
    }

    private class SearchData implements OnClickListener{

        @Override
        public void onClick(View v) {
            Intent i = new Intent(ProfileFragment.this.getActivity(), ViewData.class);
            startActivity(i);

        }
    }


    private class SignOut implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("ACCESS_TOKEN", "");
            edit.putString("ACCESS_TOKEN_SECRET", "");
            edit.commit();
            Fragment login = new LoginFragment();
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, login);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();


        }

    }



    private class LoadProfile extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Loading Profile ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(pref.getString("IMAGE_URL","")).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            Bitmap image_circle = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            BitmapShader shader = new BitmapShader (bitmap,  TileMode.CLAMP, TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            Canvas c = new Canvas(image_circle);
            c.drawCircle(image.getWidth()/2, image.getHeight()/2, image.getWidth()/2, paint);
            prof_img.setImageBitmap(image_circle);
            prof_name.setText("Welcome " +pref.getString("NAME", ""));

            progress.hide();

        }
    }
}