package com.example.xpack.bestbuy.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Favorite {

    public static boolean exists(SQLiteDatabase db, String key) {

        Cursor c = db.rawQuery("SELECT COUNT(*) AS number FROM favorites WHERE key=?", new String[]{key});

        c.moveToFirst();

        int columnIndex = c.getColumnIndex("number");
        int number = c.getInt(columnIndex);

        c.close();

        return number == 1;
    }

    public static void add(SQLiteDatabase db, String key,String name,String image,String salePrice,String ratings,String available) {

        ContentValues values = new ContentValues();

        values.put("key", key);
        values.put("added", System.currentTimeMillis() / 1000L);
        values.put("name",name);
        values.put("image",image);
        values.put("salePrice",salePrice);
        values.put("ratings",ratings);
        values.put("available",available);

        try {
            db.insertOrThrow("favorites", null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void remove(SQLiteDatabase db, String key) {
        db.delete("favorites", "key=?", new String[]{key});
    }

    /**
     * Retourne les favorits du plus récent au plus ancien
     *
     * @param db Base de données à utiliser
     * @return
     */
    public static Cursor list(SQLiteDatabase db) {
        return db.rawQuery("SELECT _id, key,datetime(added, 'unixepoch'),name,image,salePrice,ratings,available AS added FROM favorites ORDER BY added DESC", null);
    }
}
