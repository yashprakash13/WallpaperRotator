package tech.pcreate.wallpaperrotator.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Image.class}, exportSchema = false, version = 1)
public abstract class ImageDatabase extends RoomDatabase {

    public abstract ImageDao imageDao();
    private static ImageDatabase INSTANCE;

    public static ImageDatabase getDatabase(final Context context){

        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ImageDatabase.class, "image")
                        .allowMainThreadQueries()
                        .build();

        }


        return INSTANCE;
    }


}
