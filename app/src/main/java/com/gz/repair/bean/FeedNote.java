package com.gz.repair.bean;

import java.util.ArrayList;

/**
 * Created by Endeavor on 2016/9/7.
 */
public class FeedNote {


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
        public String maintainer;
        public String info;
        public String apply_name;
        public String begin_at;
        public String end_at;
        public String result;

    }


}
