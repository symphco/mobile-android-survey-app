package org.worldbank.armm.app.tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Barangay;
import org.worldbank.armm.app.models.Municipality;
import org.worldbank.armm.app.models.Province;
import org.worldbank.armm.app.models.Region;
import org.worldbank.armm.app.utils.Prefs;

import java.net.URI;
import java.util.Date;

import ca.dalezak.androidbase.tasks.HttpGetTask;
import ca.dalezak.androidbase.utils.Dates;
import ca.dalezak.androidbase.utils.Log;

public class DownloadRegions extends HttpGetTask<Region> {

    private static final String COOKIE = "Cookie";
    private static final String PROVINCES = "provinces";
    private static final String MUNICIPALITIES = "municipalities";
    private static final String BARANGAYS = "barangays";
    private static final String SINCE = "since";
    private URI uri;

    public DownloadRegions(Context context) {
        this(context, false);
    }

    public DownloadRegions(Context context, boolean loading) {
        super(context, Prefs.getServer(), "/api/v1/psgc", R.string.downloading_psgc_);
        this.uri = getURI();
    }

    @Override
    protected void onPrepareRequest() {
        if (Prefs.hasSince(uri)) {
            Date date = Prefs.getSince(uri);
            addParameter(SINCE, Dates.toEpochString(date));
        }
        addHeader(COOKIE, Prefs.getCookie());
        setUsername(Prefs.getUsername());
        setPassword(Prefs.getPassword());
    }

    @Override
    protected Region onHandleResponse(JSONObject json) throws JSONException {
        Log.i(this, "JSON %s", json);
        Integer psgc = json.getInt(Region.Columns.PSGC);
        Region region = Region.find(psgc);
        if (region == null) {
            region = new Region();
            region.psgc = psgc;
        }
        region.name = json.getString(Region.Columns.NAME);
        region.save();
        Log.i(this, "%s", region.name);
        JSONArray provinces = json.optJSONArray(PROVINCES);
        if (provinces != null) {
            for (int i = 0; i < provinces.length(); i++) {
                JSONObject provinceJSON = provinces.getJSONObject(i);
                Integer provincePsgc = provinceJSON.getInt(Province.Columns.PSGC);
                Province province = Province.find(provincePsgc);
                if (province == null) {
                    province = new Province();
                    province.psgc = provincePsgc;
                }
                province.name = provinceJSON.getString(Province.Columns.NAME);
                province.region = region;
                province.save();
                Log.i(this, "%s %s", region.name, province.name);
                JSONArray municipalities = provinceJSON.optJSONArray(MUNICIPALITIES);
                if (municipalities != null) {
                    for (int j = 0; j < municipalities.length(); j++) {
                        JSONObject municipalityJSON = municipalities.getJSONObject(j);
                        Integer municipalityPsgc = municipalityJSON.getInt(Province.Columns.PSGC);
                        Municipality municipality = Municipality.find(municipalityPsgc);
                        if (municipality == null) {
                            municipality = new Municipality();
                            municipality.psgc = municipalityPsgc;
                        }
                        municipality.name = municipalityJSON.getString(Municipality.Columns.NAME);
                        municipality.province = province;
                        municipality.save();
                        Log.i(this, "%s %s %s", region.name, province.name, municipality.name);
                        JSONArray barangays = municipalityJSON.optJSONArray(BARANGAYS);
                        if (barangays != null) {
                            for (int k = 0; k < barangays.length(); k++) {
                                JSONObject barangayJSON = barangays.getJSONObject(k);
                                Integer barangaysPsgc = barangayJSON.getInt(Province.Columns.PSGC);
                                Barangay barangay = Barangay.find(barangaysPsgc);
                                if (barangay == null) {
                                    barangay = new Barangay();
                                    barangay.psgc = barangaysPsgc;
                                }
                                barangay.name = barangayJSON.getString(Barangay.Columns.NAME);
                                barangay.municipality = municipality;
                                barangay.save();
                                Log.i(this, "%s %s %s %s", region.name, province.name, municipality.name, barangay.name);
                            }
                        }
                    }
                }
            }
        }
        Prefs.setSince(uri, new Date());
        return region;
    }
}