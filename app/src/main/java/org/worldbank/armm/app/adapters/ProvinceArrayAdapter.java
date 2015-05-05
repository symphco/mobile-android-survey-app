package org.worldbank.armm.app.adapters;

import android.content.Context;

import org.worldbank.armm.app.models.Province;
import org.worldbank.armm.app.models.Region;

import java.util.ArrayList;
import java.util.List;

import ca.dalezak.androidbase.adapters.BaseArrayAdapter;

public class ProvinceArrayAdapter extends BaseArrayAdapter<Province> {

    private Region region;

    public ProvinceArrayAdapter(Context context) {
        super(context, new Province());
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
        notifyDataSetChanged();
    }

    @Override
    public List<Province> getItems() {
        if (region != null && !region.isBlank()) {
            return region.provinces();
        }
        return new ArrayList<>();
    }
}