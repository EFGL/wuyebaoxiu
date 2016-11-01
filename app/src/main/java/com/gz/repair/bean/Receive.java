package com.gz.repair.bean;

import java.util.ArrayList;

/**
 * 接单列表
 *
 * Created by Endeavor on 2016/8/29.
 */
public class Receive {


    public String timestamp;
    public int ret;
    public String msg;
    public ArrayList<Result> result;

    public class Result {

        public String status;
        public String address;
        public String repair_item;
        public String created_at;
        public int apply_type;
        public String code;
        public String telephone;
        public String apply_name;
        public String info;
        public String distribute_time;
        public String operator;
        public int is_charged;

    }


}
