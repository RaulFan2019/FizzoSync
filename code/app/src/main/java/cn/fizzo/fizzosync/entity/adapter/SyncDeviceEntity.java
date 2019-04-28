package cn.fizzo.fizzosync.entity.adapter;

import cn.fizzo.fizzosync.entity.net.GetDeviceListRE;
import cn.fizzo.fizzosync.entity.net.GetTraineeListRE;

/**
 * Created by Raul.fan on 2017/6/27 0027.
 */

public class SyncDeviceEntity {

    public static final int SYNC_NONE = 0;
    public static final int SYNC_FAIL = 1;

    public GetDeviceListRE.HrdevicesBean trainee;
    public int syncState;

    public SyncDeviceEntity() {
    }

    public SyncDeviceEntity(GetDeviceListRE.HrdevicesBean trainee, int syncState) {
        super();
        this.trainee = trainee;
        this.syncState = syncState;
    }
}
