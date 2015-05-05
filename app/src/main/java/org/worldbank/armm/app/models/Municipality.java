package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Municipalities")
public class Municipality extends Model {

    public class Columns extends Model.Columns {
        public static final String NAME = "name";
        public static final String PSGC = "psgc";
        public static final String PROVINCE = "province";
    }

    @Column(name = Columns.NAME)
    public String name;

    @Column(name = Columns.PSGC)
    public Integer psgc;

    @Column(name = Columns.PROVINCE)
    public Province province;

    @Override
    public boolean matches(String text) {
        return name.equalsIgnoreCase(text) || name.toLowerCase().startsWith(text.toLowerCase());
    }

    public static List<Municipality> all() {
        return all(Municipality.class, Columns.NAME, true);
    }

    public static Integer count() {
        return count(Municipality.class);
    }

    public List<Barangay> barangays() {
        return all(Barangay.class, this, Barangay.Columns.NAME, true);
    }

    public static Municipality find(Integer psgc) {
        if (psgc != null) {
            return find(Municipality.class, String.format("%s = ?", Columns.PSGC), psgc.toString());
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