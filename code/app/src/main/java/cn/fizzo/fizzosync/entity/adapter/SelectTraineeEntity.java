package cn.fizzo.fizzosync.entity.adapter;

import cn.fizzo.fizzosync.entity.net.GetTraineeListRE;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class SelectTraineeEntity {

    public GetTraineeListRE.TraineesBean trainee;
    public boolean select;

    public SelectTraineeEntity() {
    }

    public SelectTraineeEntity(GetTraineeListRE.TraineesBean trainee, boolean select) {
        super();
        this.trainee = trainee;
        this.select = select;
    }
}
