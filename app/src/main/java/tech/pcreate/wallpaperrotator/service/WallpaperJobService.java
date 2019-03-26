package tech.pcreate.wallpaperrotator.service;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import javax.sql.StatementEvent;

import tech.pcreate.wallpaperrotator.database.Image;
import tech.pcreate.wallpaperrotator.database.ImageRepository;

public class WallpaperJobService extends JobService {

    private ImageRepository imageRepository;
    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
        imageRepository  = new ImageRepository(getApplication());
        AsyncTask.execute(this::changeWallpaper);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void changeWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Image currentSetWallpaper = imageRepository.getSetImage();

        if (currentSetWallpaper == null){
            Image firstWall = imageRepository.getFirstImage();
            Bitmap bitmap = BitmapFactory.decodeFile(firstWall.getPath());
            try {
                wallpaperManager.setBitmap(bitmap);
                firstWall.setIsSet(1);
                imageRepository.updateImage(firstWall);
                Log.e("First image set", "yes");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Image nextImage = imageRepository.getNextImage(currentSetWallpaper.getId());
            if (nextImage != null){
                Bitmap bitmap = BitmapFactory.decodeFile(nextImage.getPath());
                try {
                    wallpaperManager.setBitmap(bitmap);
                    currentSetWallpaper.setIsSet(0);
                    imageRepository.updateImage(currentSetWallpaper);
                    nextImage.setIsSet(1);
                    imageRepository.updateImage(nextImage);
                    Log.e("Next image set", "yes");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Log.e("Last image is set: ", "yes");
                Image firstWall = imageRepository.getFirstImage();
                Bitmap bitmap = BitmapFactory.decodeFile(firstWall.getPath());
                try {
                    wallpaperManager.setBitmap(bitmap);
                    firstWall.setIsSet(1);
                    currentSetWallpaper.setIsSet(0);
                    imageRepository.updateImage(firstWall);
                    imageRepository.updateImage(currentSetWallpaper);
                    Log.e("First image set", "yes");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        jobFinished(jobParameters, false);
    }

}
