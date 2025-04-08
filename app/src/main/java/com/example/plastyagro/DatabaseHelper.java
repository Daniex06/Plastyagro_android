package com.example.plastyagro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "plastyagro.db";
    private static final int DATABASE_VERSION = 7;

    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "usuario_id";
    private static final String COLUMN_EMAIL = "correo";
    private static final String COLUMN_NAME = "nombre_usuario";
    private static final String COLUMN_PASSWORD = "contrasena";
    private static final String COLUMN_PROFILE = "perfil";

    private static final String TABLE_REGISTRO = "registro";
    private static final String COLUMN_REGISTRO_ID = "registro_id";
    private static final String COLUMN_FECHA = "fecha";
    private static final String COLUMN_HORA = "hora";
    private static final String COLUMN_DENTRO_HORARIO = "dentro_horario";
    private static final String COLUMN_TIPO_REGISTRO = "tipo_registro";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsuariosTable = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PROFILE + " TEXT, " +
                "dni TEXT)";
        db.execSQL(createUsuariosTable);

        String createRegistroTable = "CREATE TABLE " + TABLE_REGISTRO + " (" +
                COLUMN_REGISTRO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_FECHA + " TEXT, " +
                COLUMN_HORA + " TEXT, " +
                COLUMN_DENTRO_HORARIO + " INTEGER, " +
                COLUMN_TIPO_REGISTRO + " TEXT CHECK(" + COLUMN_TIPO_REGISTRO + " IN ('entrada', 'salida')), " +
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";
        db.execSQL(createRegistroTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    public boolean registrarUsuario(String email, String usuario, String password, String perfil, String dni) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_NAME, usuario);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PROFILE, perfil);
        values.put("dni", dni);

        long result = db.insert(TABLE_USUARIOS, null, values);
        db.close();
        return result != -1;
    }

    public boolean registrarRegistro(int usuarioId, String fecha, String hora, boolean dentroHorario, String tipoRegistro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, usuarioId);
        values.put(COLUMN_FECHA, fecha);
        values.put(COLUMN_HORA, hora);
        values.put(COLUMN_DENTRO_HORARIO, dentroHorario ? 1 : 0); // BOOLEAN cambiado a INTEGER
        values.put(COLUMN_TIPO_REGISTRO, tipoRegistro);

        long result = db.insert(TABLE_REGISTRO, null, values);
        db.close();
        return result != -1;
    }

    public boolean verificarUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public String obtenerContrasena(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});

        String password = null;
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(COLUMN_PASSWORD);
            if (index != -1) {
                password = cursor.getString(index);
            }
            cursor.close();
        }
        db.close();
        return password;
    }

    public String obtenerPerfil(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PROFILE + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});

        String perfil = "trabajador"; // Valor por defecto
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(COLUMN_PROFILE);
            if (index != -1) {
                perfil = cursor.getString(index);
            }
            cursor.close();
        }
        db.close();
        return perfil;
    }

    public List<Trabajador> obtenerTrabajadores() {
        List<Trabajador> trabajadores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_NAME + ", " +
                COLUMN_EMAIL + ", " + COLUMN_PROFILE + " FROM " + TABLE_USUARIOS, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Trabajador trabajador = new Trabajador();
                trabajador.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                trabajador.setNombre(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                trabajador.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                trabajador.setPerfil(cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE)));
                trabajadores.add(trabajador);
            }
            cursor.close();
        }
        db.close();

        return trabajadores;
    }

    public List<Registro> obtenerRegistros() {
        List<Registro> registros = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r." + COLUMN_REGISTRO_ID + ", r." + COLUMN_FECHA + ", r." + COLUMN_HORA + ", " +
                "r." + COLUMN_DENTRO_HORARIO + ", r." + COLUMN_TIPO_REGISTRO + ", " +
                "u." + COLUMN_ID + " AS trabajador_id, u." + COLUMN_NAME + " AS trabajador_nombre " +
                "FROM " + TABLE_REGISTRO + " r " +
                "JOIN " + TABLE_USUARIOS + " u ON r." + COLUMN_ID + " = u." + COLUMN_ID;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Registro registro = new Registro();
                registro.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_REGISTRO_ID)));
                registro.setFecha(cursor.getString(cursor.getColumnIndex(COLUMN_FECHA)));
                registro.setHora(cursor.getString(cursor.getColumnIndex(COLUMN_HORA)));
                registro.setDentroHorario(cursor.getInt(cursor.getColumnIndex(COLUMN_DENTRO_HORARIO)) == 1);
                registro.setTipoRegistro(cursor.getString(cursor.getColumnIndex(COLUMN_TIPO_REGISTRO)));
                registro.setIdTrabajador(cursor.getInt(cursor.getColumnIndex("trabajador_id")));
                registro.setNombreTrabajador(cursor.getString(cursor.getColumnIndex("trabajador_nombre")));
                registros.add(registro);
            }
            cursor.close();
        }

        db.close();
        return registros;
    }




    public int obtenerUsuarioId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int usuarioId = -1;

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});

        if (cursor.moveToFirst()) {
            usuarioId = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return usuarioId;
    }


    // Añade este método a tu DatabaseHelper
    public void exportDatabase(Context context) throws IOException {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory() + "/plastyagro_export.db";
        OutputStream output = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        fis.close();
    }

}

