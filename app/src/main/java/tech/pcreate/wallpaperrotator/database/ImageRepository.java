package tech.pcreate.wallpaperrotator.database;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ImageRepository {

    private ImageDao imageDao;

    public ImageRepository(Application application) {
        ImageDatabase imageDatabase = ImageDatabase.getDatabase(application);
        imageDao = imageDatabase.imageDao();
    }

    public void insertImage(Image image) {
        AsyncTask.execute(() -> imageDao.insert(image));
    }

    public void deleteImage(Image image) {
        AsyncTask.execute(() -> imageDao.delete(image));
    }

    public void updateImage(Image image){
        AsyncTask.execute(()-> imageDao.update(image));
    }

    public void deleteAllImages() {
        AsyncTask.execute(() -> imageDao.deleteAll());
    }

    public LiveData<List<Image>> getAllImages() {
        return imageDao.getAllImages();
    }

    public Image getSetImage(){
        return imageDao.getSetImage();
    }

    public int countImages(){
        return imageDao.countPics();
    }

    public Image getNextImage(int id){
        return imageDao.getNextImage(id);
    }

    public Image getFirstImage(){
        return imageDao.getFirstImage();
    }

}
