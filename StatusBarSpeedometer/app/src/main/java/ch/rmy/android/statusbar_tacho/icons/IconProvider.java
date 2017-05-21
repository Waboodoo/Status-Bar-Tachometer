package ch.rmy.android.statusbar_tacho.icons;

import android.content.Context;
import android.content.res.Resources;

public class IconProvider {

    private static final int MAX_VALUE = 199;
    private static final String RES_FORMAT = "%03dicon";
    private static final String RES_TYPE = "drawable";

    private final Resources resources;
    private final String packageName;

    public IconProvider(Context context) {
        this.resources = context.getResources();
        this.packageName = context.getPackageName();
    }

    public int getIconForNumber(int number) {
        number = Math.max(0, Math.min(MAX_VALUE, number));
        String iconName = String.format(RES_FORMAT, number);
        return resources.getIdentifier(iconName, RES_TYPE, packageName);
    }

}
