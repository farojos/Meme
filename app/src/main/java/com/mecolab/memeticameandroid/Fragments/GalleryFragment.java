package com.mecolab.memeticameandroid.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.GridView;

import com.mecolab.memeticameandroid.Activities.ConversationActivity;
import com.mecolab.memeticameandroid.Models.Gallery;
import com.mecolab.memeticameandroid.R;
import com.mecolab.memeticameandroid.Views.GridViewAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {
    public final static String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MemeticaMe";
    private static final String ARG_SECTION_NUMBER = "section_number";
    //@Bind(R.id.fragment_gallery)
    //ListView mGalleryView;

    private OnFragmentInteractionListener mListener;
    //@Bind(R.id.Ga)
    private GridView gridView;
    private GridViewAdapter gridAdapter;


    public GalleryFragment() {
        // Required empty public constructor
    }

    public static GalleryFragment newInstance(int sectionNumber) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);


        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, view);

        // TODO: check if this is correct
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, getPictures());

        gridView.setAdapter(gridAdapter);

        return view;
    }

    private ArrayList<Gallery> getPictures() {
        final ArrayList<Gallery> gallerys = new ArrayList<>();
        String path = BASE_PATH;
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()){
                Log.e("DIRECTORY","Problems with directory creation");
            }
            else
                Log.d("DIRECTORY","Creation done");

        }/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck ==  PackageManager.PERMISSION_DENIED ){
                return gallerys;
            }
        }*/
        File[] files = directory.listFiles();
//        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            if(files[i].isDirectory())
                continue;
            Log.d("Files", "FileName:" + files[i].getName());
            Log.d("Files", "Mime:" +getMimeType(files[i].getAbsolutePath()));
            Bitmap bb=null;
            //Log.d("Files", "Mime:" +files[i].getAbsolutePath().replace(" ",""));
            String mime="";

            if (files[i].getName().contains("mma") )
                mime="fotoaudio";
            else
              mime=getMimeType(files[i].getAbsolutePath()).split("/")[0];
            if (mime.equals("application")){
            mime=getMimeType(files[i].getAbsolutePath());
            }
            final int THUMBSIZE = 100;
            if(mime.equals("image"))
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(files[i].getAbsolutePath(),bmOptions),
                        THUMBSIZE, THUMBSIZE);

                //bitmap = Bitmap.createScaledBitmap(bitmap,100dp,dp,true);
               // bb=
            }
            else if(mime.equals("fotoaudio")){
                String fotopath = Uri.fromFile(files[i]).getPath().toString();
                fotopath=fotopath.replace(".mma","");
                fotopath = fotopath + "/";
                File dir= new File(fotopath);
                if (!dir.exists()) {
                    if (!dir.mkdirs()){
                        Log.e("DIRECTORY","Problems with directory creation");
                    }
                    else
                    {
                        String[] unpacked =  ConversationActivity.unpackZip(path,"/"+files[i].getName());
                        Gallery g = new Gallery(null,files[i].getName(),mime,Uri.parse(unpacked[0]));
                        g.setSongUri(Uri.parse(unpacked[1]));
                        gallerys.add(g);
                        continue;

                    }

                }
                else{
                    String[] unpacked =  ConversationActivity.getOnlyStrin(path,"/"+files[i].getName());
                    if(!new File(unpacked[0]).exists())
                        unpacked =  ConversationActivity.unpackZip(path,"/"+files[i].getName());
                    if(!new File(unpacked[1]).exists())
                        unpacked =  ConversationActivity.unpackZip(path,"/"+files[i].getName());
                    File audio =  new File(unpacked[1]);
                    File image = new File(unpacked[0]);
                    Gallery g = new Gallery(null,files[i].getName(),mime,Uri.fromFile(image));
                    g.setSongUri(Uri.fromFile(audio));
                    gallerys.add(g);
                    continue;

                }



            }
            else if(mime.equals("video")) {
                bb=ThumbnailUtils.createVideoThumbnail(Uri.fromFile(files[i]).getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            }
            else if (mime.equals("audio"))
            {
                Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.audio_im);
                bb=icon;
            }
            else{
                Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.clip_icon);
                bb=icon;
            }
            //getContext().getContentResolver().getType(Uri.fromFile(files[i]));
            ///gallerys.add(new Gallery())

            gallerys.add(new Gallery(bb,files[i].getName(),mime,Uri.fromFile(files[i])));

        }

        return gallerys;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}