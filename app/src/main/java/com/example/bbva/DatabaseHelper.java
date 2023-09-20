package com.example.bbva;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "login.db";

    public DatabaseHelper(Context context) {
        super(context, "login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(correo TEXT primary key, password TEXT, nombre TEXT)");
        MyDB.execSQL("create Table cuentas(cuenta TEXT primary key, alias TEXT, tipo TEXT, correo TEXT, FOREIGN KEY(correo) REFERENCES users(correo))");
        MyDB.execSQL("create Table creditos(id INTEGER PRIMARY KEY AUTOINCREMENT, tipoCred TEXT, monto REAL, fechainicio TEXT, plazo TEXT, saldo REAL, estatus TEXT, correo TEXT, FOREIGN KEY(correo) REFERENCES users(correo))");
        MyDB.execSQL("create Table transacciones(id INTEGER PRIMARY KEY AUTOINCREMENT, cuenta TEXT, descripcion TEXT, monto REAL, fecha DATE, FOREIGN KEY(cuenta) REFERENCES cuentas(cuenta))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
    }

    //Inserta una nueva fila en la tabla "users" con los valores de "cuenta", "password" y "nombre" especificados.
    public Boolean insertDataUsers (String password, String nombre, String correo){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", password);
        contentValues.put("nombre", nombre);
        contentValues.put("correo", correo);
        long result = MyDB.insert("users",null,contentValues);

        if (result == -1){
            return false;

        } else {
            return true;
        }
    }

    //Inserta una nueva fila en la tabla "creditos" con los valores especificados.
    public Boolean insertDataCredits (String tipoCred, double monto, String fechainicio, String plazo, double saldo, String estatus, String correo){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tipoCred", tipoCred);
        contentValues.put("monto", monto);
        contentValues.put("fechainicio", fechainicio);
        contentValues.put("plazo", plazo);
        contentValues.put("saldo", saldo);
        contentValues.put("estatus", estatus);
        contentValues.put("correo", correo);
        long result = MyDB.insert("creditos",null,contentValues);

        if (result == -1){
            return false;

        } else {
            return true;
        }
    }

    //Inserta una nueva fila en la tabla "cuentas" con los valores especificados.
    public Boolean insertDataCuentas (String cuenta, String alias, String tipo, String correo){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cuenta", cuenta);
        contentValues.put("alias", alias);
        contentValues.put("tipo", tipo);
        contentValues.put("correo", correo);
        long result = MyDB.insert("cuentas",null,contentValues);

        if (result == -1){
            return false;

        } else {
            return true;
        }
    }

    //Verifica si existe una fila en la tabla "users" con el valor de "cuenta" especificado.
    public Boolean checkUser(String correo){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where correo = ?",new String[]{correo});

        if (cursor.getCount() > 0){
            return true;
        } else{
            return false;
        }
    }

    //Verifica si existe una fila en la tabla "users" con los valores de "cuenta" y "password" especificados.
     public Boolean checkUserPassword(String correo, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where correo = ? and password = ?",new String[]{correo, password});

        if (cursor.getCount() > 0){
            return true;
        } else {
            return false;
        }
    }

    //Retorna el valor de la columna especificada para la fila en la tabla "users" con el valor de "cuenta" especificado.
    public String getParameter(String correo, String parameterName) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT " + parameterName + " FROM users WHERE correo=?", new String[]{correo});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    }

    public String getCuenta(String correo, String parameterName) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT " + parameterName + " FROM cuentas WHERE correo=?", new String[]{correo});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    }


    public String getTipo(String cuenta, String parameterName) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT " + parameterName + " FROM cuentas WHERE cuenta=?", new String[]{cuenta});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    }

    public String obtenerCuenta(String correo, String alias) {
        SQLiteDatabase db = this.getReadableDatabase();
        String cuenta = null;
        Cursor cursor = db.rawQuery("SELECT cuenta FROM cuentas WHERE correo = ? AND alias = ?", new String[] { correo, alias });
        if (cursor.moveToFirst()) {
            cuenta = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return cuenta;
    }

    //Inserta una nueva fila en la tabla "transacciones" con los valores de "cuenta", "monto", "fecha" y "descripcion" especificados.
    public boolean insertTransaction(String cuenta, double monto, String fecha, String descripcion) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cuenta", cuenta);
        contentValues.put("monto", monto);
        contentValues.put("fecha", fecha);
        contentValues.put("descripcion", descripcion);
        long result = MyDB.insert("transacciones", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    //Obtiene todas las cuentas que tiene un usuario
    public Cursor getCuentas(String correo) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        String[] columns = {"cuenta", "alias", "tipo", "correo"};
        String selection = "correo=?";
        String[] selectionArgs = {correo};
        Cursor cursor = MyDB.query("cuentas", columns, selection, selectionArgs, null, null, null);
        return cursor;
    }


    //Retorna el saldo actual de la cuenta especificada,
    //calculado como la suma de todos los valores de "monto" en la tabla "transacciones" para esa cuenta.
    public double getBalance(String cuenta) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT SUM(monto) FROM transacciones WHERE cuenta=?", new String[]{cuenta});
        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }
        return 0;
    }

    //Obtiene todos los movimientos de una cuenta especificada
    public List<Movimiento> getMovimientosPorCuenta(String cuenta) {
        List<Movimiento> movimientos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"fecha", "descripcion", "monto"};
        String whereClause = "cuenta=?";
        String[] whereArgs = {cuenta};
        String orderBy = "id DESC";
        Cursor cursor = db.query("transacciones", columns, whereClause, whereArgs, null, null, orderBy);
        while (cursor.moveToNext()) {
            String fecha = cursor.getString(0);
            String descripcion = cursor.getString(1);
            String monto = cursor.getString(2);
            movimientos.add(new Movimiento(fecha, descripcion, monto));
        }
        cursor.close();
        db.close();
        return movimientos;
    }

    //Obtiene el ID del ultimo credito solicitado
    public int obtenerMaximoIdCredito(String correo) {
        int maximoId = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(id) FROM creditos WHERE correo = ?", new String[]{correo});
        if (cursor.moveToFirst()) {
            maximoId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return maximoId;
    }

    //Obtiene el valor de un atributo tipo String del ultimo credito solicitado
    public String consultarCredito(String correo, int id, String atributo) {
        String valor = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + atributo + " FROM creditos WHERE correo = ? AND id = (SELECT MAX(id) FROM creditos WHERE correo = ?)", new String[]{correo, correo});
        if (cursor.moveToFirst()) {
            valor = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return valor;
    }

    //Obtiene el valor de un atributo numerico del ultimo credito solicitado
    public double consultarCreditomonto(String correo, int id, String atributo) {
        double valor = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + atributo + " FROM creditos WHERE correo = ? AND id = (SELECT MAX(id) FROM creditos WHERE correo = ?)", new String[]{correo, correo});
        if (cursor.moveToFirst()) {
            valor = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return valor;
    }

    //Modifica el valor de un string en el último crédito solicitado
    public boolean modificarCredito(String correo, String atributo, String nuevoValor) {
        boolean exito = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(atributo, nuevoValor);
        int rowsAffected = db.update("creditos", valores, "correo = ? AND id = (SELECT MAX(id) FROM creditos WHERE correo = ?)", new String[]{correo, correo});
        if (rowsAffected > 0) {
            exito = true;
        }
        db.close();
        return exito;
    }

    //Modifica un valor numerico en el último crédito solicitado
    public boolean modificarDeuda(String correo, String atributo, Double nuevoValor) {
        boolean exito = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(atributo, nuevoValor);
        int rowsAffected = db.update("creditos", valores, "correo = ? AND id = (SELECT MAX(id) FROM creditos WHERE correo = ?)", new String[]{correo, correo});
        if (rowsAffected > 0) {
            exito = true;
        }
        db.close();
        return exito;
    }
    //Verifica si el usuario ya tiene una tarjeta de crédito
    public boolean existeCuentaCredito(String correo) {
        boolean existe = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM cuentas WHERE correo = ? AND tipo = 'crédito'", new String[]{correo});
        if (cursor.moveToFirst()) {
            int cuenta = cursor.getInt(0);
            existe = cuenta > 0;
        }
        cursor.close();
        db.close();
        return existe;
    }
}
