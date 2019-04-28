package cn.fizzo.fizzosync.entity.net;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class GetTraineeListRE {


    public List<TraineesBean> trainees;

    public List<TraineesBean> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<TraineesBean> trainees) {
        this.trainees = trainees;
    }

    public static class TraineesBean implements Serializable{
        /**
         * id : 10373
         * nickname : 舟溪
         * gender : 2
         * mobile : 18621196736
         * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2016-03-01_56d55e0fea39c.jpg
         * jointime : 2016-10-10 11:18:12
         * weight : 52
         * max_hr : 195
         * rest_hr : 55
         * target_hr : 100
         * target_hr_high : 175
         * serialno : CRA921F668WE3
         * mac_addr : E4:87:CE:70:67:02
         * antplus_serialno : 1F92
         */

        public int id;
        public String nickname;
        public int gender;
        public String mobile;
        public String avatar;
        public String jointime;
        public int weight;
        public int max_hr;
        public int rest_hr;
        public int target_hr;
        public int target_hr_high;
        public String serialno;
        public String mac_addr;
        public String antplus_serialno;
        public String fizzosync_lasttime;

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

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getMax_hr() {
            return max_hr;
        }

        public void setMax_hr(int max_hr) {
            this.max_hr = max_hr;
        }

        public int getRest_hr() {
            return rest_hr;
        }

        public void setRest_hr(int rest_hr) {
            this.rest_hr = rest_hr;
        }

        public int getTarget_hr() {
            return target_hr;
        }

        public void setTarget_hr(int target_hr) {
            this.target_hr = target_hr;
        }

        public int getTarget_hr_high() {
            return target_hr_high;
        }

        public void setTarget_hr_high(int target_hr_high) {
            this.target_hr_high = target_hr_high;
        }

        public String getSerialno() {
            return serialno;
        }

        public void setSerialno(String serialno) {
            this.serialno = serialno;
        }

        public String getMac_addr() {
            return mac_addr;
        }

        public void setMac_addr(String mac_addr) {
            this.mac_addr = mac_addr;
        }

        public String getAntplus_serialno() {
            return antplus_serialno;
        }

        public void setAntplus_serialno(String antplus_serialno) {
            this.antplus_serialno = antplus_serialno;
        }

        public String getFizzosync_lasttime() {
            return fizzosync_lasttime;
        }

        public void setFizzosync_lasttime(String fizzosync_lasttime) {
            this.fizzosync_lasttime = fizzosync_lasttime;
        }
    }
}
