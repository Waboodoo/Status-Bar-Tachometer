package ch.rmy.android.statusbar_tacho.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Links {

    private static final String GITHUB_URL = "https://github.com/Waboodoo/Status-Bar-Tachometer";

    public static void openGithub(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL));
        context.startActivity(intent);
    }

}
