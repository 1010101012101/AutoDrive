package icegps.com.maplib.map.mapdata

class MapParameter {
    /**
     * 地图宽高
     */
    var mapHeight = 0f
    var mapWidth = 0f
    /**
     * 地图限制最大最小缩放倍数
     */
    val minMultiple = 1.0f
    val maxMultiple = 40.0f
    @Volatile
    var multiple = 20f

    /**
     * 左负右正    地图偏移量
     */
    @Volatile
    var tx: Float = 0f
    @Volatile
    var ty = 0f

    /**
     * 总偏移量
     */
    @Volatile
    var sumTx: Float = 0f
        get() {
            return tx - curX * multiple
        }
    @Volatile
    var sumTy: Float = 0f
        get() {
            return ty - curY * multiple
        }
    @Volatile
    var isIdle = true

    /**
     * 当前位置  比如坐标向X方向移动10个点  那么在偏移的时候则-10个点这样就保持坐标点始终在中心
     */
    @Volatile
    var curX = 0f
    @Volatile
    var curY = 0f


    /**
     * 重制偏移量以及缩放
     */
    fun recover() {
        curX = 0f
        curY = 0f
        tx = mapWidth / 2
        ty = mapHeight / 2
        multiple = 20f
    }


}