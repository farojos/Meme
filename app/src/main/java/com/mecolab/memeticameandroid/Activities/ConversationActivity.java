package com.mecolab.memeticameandroid.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.mecolab.memeticameandroid.FileUtils.FileManager;
import com.mecolab.memeticameandroid.Fragments.ConversationFragment;
import com.mecolab.memeticameandroid.Fragments.GalleryFragment;
import com.mecolab.memeticameandroid.Meme.MemeCreatorActivity;
import com.mecolab.memeticameandroid.Models.User;
import com.mecolab.memeticameandroid.Networking.Listeners;
import com.mecolab.memeticameandroid.R;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConversationActivity extends AppCompatActivity  implements
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener  {
    public static final int GENERIC_FILE_CODE = 0;
    public static final int PICK_IMAGE_CODE = 1;
    public static final int TEXT_MEME_CODE = 2;
    public static final int PICK_MEME_IMAGE_CODE = 3;
    public static final int PICK_MEME_AUDIO_CODE = 4;
    public static final int PICK_CONTACT_CODE = 5;
    public static final int PICK_PUBLIC_MEME = 6;
    public static final int PICK_MMA = 7;
    public static final int PICK_MMA_FINAL=8;
    public static final int MOMO =  9 ;
    public static Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_create_mma){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_MMA);
            return true;
        }
        else if (id == R.id.action_send_photo) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_CODE);
            return true;
        }
        else if (id == R.id.action_send_generic_file) {
            // This always works
            Intent i = new Intent(this, FilePickerActivity.class);
            // This works if you defined the intent filter
            // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

            // Set these depending on your use case. These are the defaults.
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

            // Configure initial directory by specifying a String.
            // You could specify a String like "/storage/emulated/0/", but that can
            // dangerous. Always use Android's API calls to get paths to the SD-card or
            // internal memory.
            i.putExtra(FilePickerActivity.EXTRA_START_PATH,
                    Environment.getExternalStorageDirectory().getPath());

            startActivityForResult(i, GENERIC_FILE_CODE);
            return true;
        }
        else if (id == R.id.action_invite_contact) {
            ConversationFragment conversationFragment =
                    (ConversationFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.ChatActivity_ConversationFragment);

            if(!conversationFragment.isGroupConversation())
            {
                Toast.makeText(this, "This isn't a group conversation.", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivityForResult(intent, PICK_CONTACT_CODE);
            return true;
        }
        else if(id == R.id.action_meme_creator_conversation){
            Intent intent = new Intent(this, MemeCreatorActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("canvas act onActRes", "requestCode:"+"requestCode");// 2

        if (requestCode == GENERIC_FILE_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(String.valueOf(uri));
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            ConversationFragment conversationFragment =
                    (ConversationFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.ChatActivity_ConversationFragment);
            conversationFragment.sendFileMessage(uri, type);
        }
        else if ((requestCode == PICK_MMA && resultCode == RESULT_OK)){
            ConversationActivity.uri = data.getData();
           /* String a  = getRealPathFromURI(uri);
            Log.d("Uri",Uri.parse(uri.getPath()).getPath());*/
            Intent i = new Intent(this, FilePickerActivity.class);
            // This works if you defined the intent filter
            // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

            // Set these depending on your use case. These are the defaults.
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

            // Configure initial directory by specifying a String.
            // You could specify a String like "/storage/emulated/0/", but that can
            // dangerous. Always use Android's API calls to get paths to the SD-card or
            // internal memory.
            i.putExtra(FilePickerActivity.EXTRA_START_PATH,
                    Environment.getExternalStorageDirectory().getPath());

            startActivityForResult(i, PICK_MMA_FINAL);
        }
        else {
            if ((requestCode == PICK_MMA_FINAL && resultCode == RESULT_OK)) {
                Log.d("IMAGE", ConversationActivity.uri.getPath());
                Log.d("AUDIO", data.getData().getPath());
                if (GalleryFragment.getMimeType(data.getData().getPath()).split("/")[0].equals("audio")) {
                    Toast.makeText(ConversationActivity.this,
                            "Procesando", Toast.LENGTH_SHORT).show();
                        String path = "" ;

                        try {
                            String Image = FileManager.loadBase64(getBaseContext(), ConversationActivity.uri);
                            path = FileManager.saveBase64(Image,"image/jpeg")  ;
                            Log.d("PATH",path);
                        }
                        catch (IOException e){}
                       /* String audio = FileManager.loadBase64(getBaseContext(), data.getData());
                        String tot = Image+ "SEPARATOR" + audio ;
                        ConversationFragment conversationFragment =
                                (ConversationFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.ChatActivity_ConversationFragment);
                                        */
                        //String save = FileManager.saveBase64(tot,"mma");

                        //conversationFragment.sendFileMessage(Image, audio);
                       // Log.d("IMAGE", Image);
                       // Log.d("AUDIO", tot);
                        zip(new String[]{path,data.getData().getPath()},FileManager.BASE_PATH+"/momazo.zip");


                }
                else {
                    Toast.makeText(ConversationActivity.this,
                            R.string.not_audio_file, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String mimeType = getContentResolver().getType(uri);
                ConversationFragment conversationFragment =
                        (ConversationFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.ChatActivity_ConversationFragment);
                conversationFragment.sendFileMessage(uri, mimeType);
            } else if (requestCode == PICK_CONTACT_CODE && resultCode == RESULT_OK) {
                String number = data.getStringExtra(User.PHONE_NUMBER);

                ConversationFragment conversationFragment =
                        (ConversationFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.ChatActivity_ConversationFragment);

                boolean exists = conversationFragment.addParticipant(number, new Listeners.OnUserAddedListener() {
                    @Override
                    public void onUserAdded(boolean successful) {
                        if (successful) {
                            Toast.makeText(ConversationActivity.this,
                                    "Contact added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ConversationActivity.this,
                                    "Connection failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (!exists) {
                    Toast.makeText(ConversationActivity.this,
                            "Contact already in group", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View view) {
        ConversationFragment conversationFragment =
                (ConversationFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.ChatActivity_ConversationFragment);
        conversationFragment.onEmojiconBackspaceClicked(view);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        ConversationFragment conversationFragment =
                (ConversationFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.ChatActivity_ConversationFragment);
        conversationFragment.onEmojiconClicked(emojicon);
    }
    public void zip(String[] _files, String zipFileName) {
        int BUFFER = Base64.DEFAULT;
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getRealPathFromURI(Uri contentURI) {

        String thePath = "no-path-found";
        String[] filePathColumn = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            thePath = cursor.getString(columnIndex);
            Log.d("INTPATH",thePath);
            String a = cursor.getString(1);
        }

        cursor.close();
        return  thePath;
    }
}
