package ch.rmy.android.statusbar_tacho.utils

class Destroyer : Destroyable {

    private val destroyables = mutableListOf<Destroyable>()

    fun <T : Destroyable> own(destroyable: T): T {
        destroyables.add(destroyable)
        return destroyable
    }

    fun own(destroyable: () -> Unit) {
        destroyables.add(object : Destroyable {
            override fun destroy() {
                destroyable.invoke()
            }
        })
    }

    override fun destroy() {
        for (destroyable in destroyables) {
            destroyable.destroy()
        }
        destroyables.clear()
    }

}
