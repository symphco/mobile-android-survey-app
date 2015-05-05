package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Barangays")
public class Barangay extends Model {

    public class Columns extends Model.Columns {
        public static final String NAME = "name";
        public static final String PSGC = "psgc";
        public static final String MUNICIPALITY = "municipality";
    }

    @Column(name = Columns.NAME)
    public String name;

    @Column(name = Columns.PSGC)
    public Integer psgc;

    @Column(name = Columns.MUNICIPALITY)
    public Municipality municipality;

    @Override
    public boolean matches(String text) {
        return name.equalsIgnoreCase(text) || name.toLowerCase().startsWith(text.toLowerCase());
    }

    public static Integer count() {
        return count(Barangay.class);
    }

    public static List<Barangay> all() {
        return all(Barangay.class, Columns.NAME, true);
    }

    public static Barangay find(Integer psgc) {
        if (psgc != null) {
            return find(Barangay.class, String.format("%s = ?", Columns.PSGC), psgc.toString());
        }
        return null;
    }

    public String toString(){
        return name;
    }

    public boolean isBlank() {
        return getId() == null;
    }
}