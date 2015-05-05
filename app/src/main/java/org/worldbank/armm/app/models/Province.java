package org.worldbank.armm.app.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Provinces")
public class Province extends Model {

    public class Columns extends Model.Columns {
        public static final String NAME = "name";
        public static final String PSGC = "psgc";
        public static final String REGION = "region";
    }

    @Column(name = Columns.NAME)
    public String name;

    @Column(name = Columns.PSGC)
    public Integer psgc;

    @Column(name = Columns.REGION)
    public Region region;

    public static Integer count() {
        return count(Province.class);
    }

    public static List<Province> all() {
        return all(Province.class, Columns.NAME, true);
    }

    public List<Municipality> municipalities() {
        return all(Municipality.class, this, Municipality.Columns.NAME, true);
    }

    @Override
    public boolean matches(String text) {
        return name.equalsIgnoreCase(text) || name.toLowerCase().startsWith(text.toLowerCase());
    }

    public static Province find(Integer psgc) {
        if (psgc != null) {
            return find(Province.class, String.format("%s = ?", Columns.PSGC), psgc.toString());
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