package com.gz.repair.bean;

import java.util.ArrayList;

/**
 * Created by Endeavor on 2016/9/7.
 */
public class FeedNote {


    public String msg;
    public int ret;
    public String timestamp;
    public ArrayList<Result> result;

    public class Result {

        public String end_at;
        public int is_charged;
        public String address;
        public String code;
        public String created_at;
        public String telephone;
        public String apply_name;
        public String maintainer;
        public String begin_at;
        public String result;
        public String repair_item;
        public int id;
        public int apply_type;
        public String info;
        public String note;
        public String status;
        public ArrayList<Images> images;

        public class Images {

            public String image_1;
            public String image_2;
            public String image_3;
            public String image_4;

        }

    }

}
