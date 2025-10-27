import com.arrive.terminal.core.ui.utils.livedata.BaseSafeLiveEvent

class LiveEvent : BaseSafeLiveEvent<Unit>() {

    fun fire() {
        value = Unit
    }

    fun fireAsync() = postValue(Unit)

    fun fireUI() = super.postValueUI(Unit)
}