package cn.fizzo.fizzosync.entity.adapter;

import cn.fizzo.fizzosync.entity.net.GetTraineeListRE;

/**
 * Created by Raul.fan on 2017/6/27 0027.
 */

public class SyncTraineeEntity {

    public static final int SYNC_NONE = 0;
    public static final int SYNC_FAIL = 1;

    public GetTraineeListRE.TraineesBean trainee;
    public int syncState;

    public SyncTraineeEntity() {
    }

    public SyncTraineeEntity(GetTraineeListRE.TraineesBean trainee, int syncState) {
        super();
        this.trainee = trainee;
        this.syncState = syncState;
    }
}
