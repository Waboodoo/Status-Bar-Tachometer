package ch.rmy.android.statusbar_tacho.utils;

import java.util.ArrayList;
import java.util.List;

public class Destroyer implements Destroyable {

    private final List<Destroyable> destroyables = new ArrayList<>();

    public <T extends Destroyable> T own(T destroyable) {
        destroyables.add(destroyable);
        return destroyable;
    }

    @Override
    public void destroy() {
        for (Destroyable destroyable : destroyables) {
            destroyable.destroy();
        }
        destroyables.clear();
    }

}
