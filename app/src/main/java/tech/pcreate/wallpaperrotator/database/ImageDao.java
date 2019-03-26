package tech.pcreate.wallpaperrotator.database;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ImageDao {

    @Insert
    void insert(Image image);

    @Delete
    void delete(Image image);

    @Query("Delete from image")
    void deleteAll();

    @Update
    void update(Image image);

    @Query("Select * from image")
    LiveData<List<Image>> getAllImages();

    @Query("Select * from image where isSet = 1" )
    Image getSetImage();

    @Query("Select count(*) from image")
    int countPics();

    @Query("Select * from image where id > :imageId limit 1 ")
    Image getNextImage(int imageId);

    @Query("Select * from image order by id ASC limit 1")
    Image getFirstImage();

}
