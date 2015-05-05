package org.worldbank.armm.app.adapters;

import android.content.Context;

import org.worldbank.armm.app.models.Barangay;
import org.worldbank.armm.app.models.Municipality;

import java.util.ArrayList;
import java.util.List;

import ca.dalezak.androidbase.adapters.BaseArrayAdapter;

public class BarangayArrayAdapter extends BaseArrayAdapter<Barangay> {

    private Municipality municipality;

    public BarangayArrayAdapter(Context context) {
        super(context, new Barangay());
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
        notifyDataSetChanged();
    }

    @Override
    public List<Barangay> getItems() {
        if (municipality != null && !municipality.isBlank()) {
            return municipality.barangays();
        }
        return new ArrayList<>();
    }
}