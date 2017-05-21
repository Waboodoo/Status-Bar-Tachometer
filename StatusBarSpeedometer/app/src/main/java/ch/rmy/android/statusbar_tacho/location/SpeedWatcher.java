package ch.rmy.android.statusbar_tacho.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import ch.rmy.android.statusbar_tacho.utils.Destroyable;
import ch.rmy.android.statusbar_tacho.utils.EventSource;

public class SpeedWatcher implements Destroyable {

    private final LocationManager locationManager;
    private final EventSource<Float> speedSource = new EventSource<>();

    private String provider;
    private Float currentSpeed;
    private boolean enabled;
    private boolean locationProviderEnabled = false;

    private final LocationListener locationListener = new SimpleLocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            sendSpeedUpdate(location.getSpeed());
        }

        @Override
        public void onProviderEnabled(String provider) {
            locationProviderEnabled = true;
            speedSource.notify(currentSpeed);
        }

        @Override
        public void onProviderDisabled(String provider) {
            locationProviderEnabled = false;
            currentSpeed = null;
            speedSource.notify(null);
        }
    };

    public SpeedWatcher(Context context) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(true);
        provider = locationManager.getBestProvider(criteria, false);

        locationProviderEnabled = locationManager.isProviderEnabled(provider);
    }

    public void enable() {
        if (enabled) {
            return;
        }
        enabled = true;
        locationManager.requestLocationUpdates(provider, 800, 0, locationListener);
    }

    public void disable() {
        if (!enabled) {
            return;
        }
        enabled = false;
        locationManager.removeUpdates(locationListener);
    }

    private void sendSpeedUpdate(float speed) {
        if (currentSpeed == null || currentSpeed != speed) {
            currentSpeed = speed;
            speedSource.notify(speed);
        }
    }

    public Float getCurrentSpeed() {
        return currentSpeed;
    }

    public boolean isGPSEnabled() {
        return locationProviderEnabled;
    }

    public EventSource<Float> getSpeedSource() {
        return speedSource;
    }

    @Override
    public void destroy() {
        disable();
    }
}
