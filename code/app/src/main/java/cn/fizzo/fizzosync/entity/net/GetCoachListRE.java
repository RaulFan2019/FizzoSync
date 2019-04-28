package cn.fizzo.fizzosync.entity.net;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class GetCoachListRE {


    private List<CoachesBean> coaches;

    public List<CoachesBean> getCoaches() {
        return coaches;
    }

    public void setCoaches(List<CoachesBean> coaches) {
        this.coaches = coaches;
    }

    public static class CoachesBean implements Serializable{
        /**
         * id : 10958
         * nickname : 倩莹
         * gender : 2
         * mobile : 18758551032
         * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2016-10-26_58105dd7b2951.
         * jointime : 2016-10-10 10:53:22
         * traineecount : 2
         */

        public int id;
        public String nickname;
        public int gender;
        public String mobile;
        public String avatar;
        public String jointime;
        public int traineecount;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getJointime() {
            return jointime;
        }

        public void setJointime(String jointime) {
            this.jointime = jointime;
        }

        public int getTraineecount() {
            return traineecount;
        }

        public void setTraineecount(int traineecount) {
            this.traineecount = traineecount;
        }
    }
}
