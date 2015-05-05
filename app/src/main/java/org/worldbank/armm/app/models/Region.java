package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Regions")
public class Region extends Model {

    public class Columns extends Model.Columns {
        public static final String NAME = "name";
        public static final String PSGC = "psgc";
    }

    @Column(name = Columns.NAME)
    public String name;

    @Column(name = Columns.PSGC)
    public Integer psgc;

    public static Integer count() {
        return count(Region.class);
    }

    public static List<Region> all() {
        return all(Region.class, Columns.NAME, true);
    }

    public List<Province> provinces() {
        return all(Province.class, this, Province.Columns.NAME, true);
    }

    @Override
    public boolean matches(String text) {
        return name.equalsIgnoreCase(text) || name.toLowerCase().startsWith(text.toLowerCase());
    }

    public static Region find(Integer psgc) {
        if (psgc != null) {
            return find(Region.class, String.format("%s = ?", Columns.PSGC), psgc.toString());
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
