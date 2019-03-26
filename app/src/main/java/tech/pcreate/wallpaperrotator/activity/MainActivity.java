package tech.pcreate.wallpaperrotator.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import tech.pcreate.wallpaperrotator.R;
import tech.pcreate.wallpaperrotator.database.Image;
import tech.pcreate.wallpaperrotator.database.ImageRepository;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private static final int IMAGE_CODE = 13;
    private String [] permissions = { Manifest.permission.SET_WALLPAPER, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private RecyclerView mRecyclerview;
    private ImageAdapter imageAdapter;
    private ImageRepository imageRepository;
    private TextView mEmptyView;
    private final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Wallpapaper Rotator/");
    private Button mGoToBtn;
    private ItemTouchHelper itemTouchHelper;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Select a Picture"), IMAGE_CODE);
        });

        imageRepository = new ImageRepository(getApplication());
        imageRepository.getAllImages().observe(this, images -> {
            if (images.size() > 0){
                if (imageAdapter == null){
                    imageAdapter = new ImageAdapter(images);
                    mRecyclerview.setAdapter(imageAdapter);
                    hideEmptyView();
                    showGoToSettingsButton();
                }else{
                    imageAdapter.setImageList(images);
                }
            }else showEmptyView();
        });
    }

    private void showGoToSettingsButton() {
        mGoToBtn.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerview.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerview.setVisibility(View.GONE);
        mGoToBtn.setVisibility(View.GONE);
    }

    private void initViews() {
        mRecyclerview = findViewById(R.id.picslist);
        mRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerview);

        mGoToBtn = findViewById(R.id.goToTimingActBtn);
        mGoToBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, TimingsActivity.class)));
        mEmptyView = findViewById(R.id.empty_view);
        checkPermissions();

    }

    private void showPermissionsNeededDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions Needed!");
        builder.setMessage("The following permissions are needed to set your wallpapers: WALLPAPER permission and STORAGE Permission.");
        builder.setCancelable(false);
        builder.setPositiveButton("Okay!", (dialogInterface, i) -> checkPermissions());
        builder.show();
    }

    private void checkPermissions() {
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                //DONE
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                showPermissionsNeededDialog();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CODE) {
            if(resultCode == RESULT_OK) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    saveBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }
    }

    private void saveBitmap(Bitmap bitmap) {
        AsyncTask.execute( () -> {
            File myDir = new File(PATH);
            myDir.mkdirs(); // creating dir

            String picNumber = getPicNumber();
            String fname = "PIC_" + picNumber +  ".png";
            File file = new File (myDir, fname);
            addToDatabase(file.getAbsolutePath());
            if (file.exists ()) file.delete (); // del file file if already exists
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                showImageAddedMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addToDatabase(String absolutePath) {
        AsyncTask.execute( ()->{
            Image image = new Image();
            image.setPath(absolutePath);
            image.setIsSet(0);
            imageRepository.insertImage(image);
        });
    }

    private void showImageAddedMessage() {
        Toast.makeText(this, "Image Added!", Toast.LENGTH_SHORT).show();
    }

    private String getPicNumber() {
        int currentlySetImage = imageRepository.countImages();
        return String.valueOf(currentlySetImage + 1);
    }

    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper =
            new ItemTouchHelper.SimpleCallback
                    (ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT){

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            deletePic(pos);
        }
    };

    private void deletePic(int pos) {
        Image image = imageAdapter.getImageAtPosition(pos);
        imageRepository.deleteImage(image);
        Toast.makeText(this, "Pic deleted!", Toast.LENGTH_SHORT).show();

    }

}
