package org.worldbank.armm.app.models;

import android.content.Context;
import android.content.pm.PackageManager;

import com.activeandroid.query.Select;

import java.io.IOException;
import java.util.List;

import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;

public abstract class Model extends com.activeandroid.Model implements BaseModel {

    public static boolean allModelsAreValid(Context context) {
        try {
            for (Class<? extends Model> modelClass : ca.dalezak.androidbase.utils.Runtime.getClasses(context, Model.class)) {
                for (Class columnClass : modelClass.getDeclaredClasses()) {
                    if (columnClass.getSimpleName().equals("Columns")) {
                        StringBuilder clause = new StringBuilder();
                        for (java.lang.reflect.Field field : columnClass.getFields()) {
                            try {
                                if (clause.length() > 0) {
                                    clause.append(" AND ");
                                }
                                clause.append(field.get(null).toString());
                                clause.append(" IS NOT NULL");
                            }
                            catch (Exception e) {
                                Log.i(Model.class, "Exception", e);
                            }
                        }
                        try {
                            Log.i(context, "%s %s", modelClass.getSimpleName(), clause);
                            new Select().
                                    from(modelClass).
                                    where(clause.toString()).
                                    count();
                        }
                        catch (Exception e) {
                            Log.i(modelClass, "Exception", e);
                            return false;
                        }
                    }
                }
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e(Model.class, "IOException", e);
        }
        catch (IOException e) {
            Log.e(Model.class, "IOException", e);
        }
        return true;
    }

    public class Columns {

    }

    public static <M extends Model> Integer count(Class<M> type) {
        return new Select().
                from(type).
                count();
    }

    public static <M extends Model> Integer count(Class<M> type, String clause, Object...args) {
        return new Select().
                from(type).
                where(clause, args).
                count();
    }

    public static <M extends Model> List<M> all(Class<M> type) {
        return new Select().
                from(type).
                execute();
    }

    public static <M extends Model> List<M> all(Class<M> type, String orderBy, Boolean asc) {
        return new Select().
                from(type).
                orderBy(String.format("%s %s", orderBy, asc ? "ASC" : "DESC")).
                execute();
    }

    public static <M extends Model> List<M> all(Class<M> type, Model parent, String orderBy, Boolean asc) {
        return new Select().
                from(type).
                where(String.format("%s = ?", parent.getClass().getSimpleName()), parent.getId()).
                orderBy(String.format("%s %s", orderBy, asc ? "ASC" : "DESC")).
                execute();
    }

    public static <M extends Model> List<M> where(Class<M> type, String clause, Object...args) {
        return new Select().
                from(type).
                where(clause, args).
                execute();
    }

    public static <M extends Model> List<M> where(Class<M> type, String orderBy, Boolean asc, String clause, Object...args) {
        return new Select().
                from(type).
                where(clause, args).
                orderBy(String.format("%s %s", orderBy, asc ? "ASC" : "DESC"))
                .execute();
    }

    protected static <M extends Model> M find(Class<M> type, String clause, String args) {
        return new Select().
                from(type).
                where(clause, args).
                executeSingle();
    }

    public abstract boolean matches(String text);
}
